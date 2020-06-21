package dny.apps.tiaw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.SaleCard;
import dny.apps.tiaw.domain.models.service.SaleCardServiceModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.SaleCardRepository;

@Service
public class SaleCardServiceImpl implements SaleCardService {

	private final SaleCardRepository saleCardRepository;
	private final CardRepository cardRepository;
	private final ModelMapper modelMapper;
	
	@Autowired
	public SaleCardServiceImpl(SaleCardRepository saleCardRepository, CardRepository cardRepository, ModelMapper modelMapper) {
		this.saleCardRepository = saleCardRepository;
		this.cardRepository = cardRepository;
		this.modelMapper = modelMapper;
	}
	
	@Override
	public List<SaleCardServiceModel> findAll() {
		return this.saleCardRepository.findAll().stream()
				.sorted((sc1, sc2) -> sc1.getPrice().compareTo(sc2.getPrice()))
				.map(sc -> this.modelMapper.map(sc, SaleCardServiceModel.class))
				.collect(Collectors.toList());
	}
	
	private SaleCard generateRandomCard(List<Card> cards) {
		Card card = cards.get(ThreadLocalRandom.current().nextInt(0, cards.size()));
		card.setPrice(card.getPrice() / 2);
		
		return this.modelMapper.map(card, SaleCard.class);
	}
	
	@Override
	public List<SaleCardServiceModel> setOnSale() {
		this.saleCardRepository.deleteAll();
		List<Card> cards = this.cardRepository.findAll();
		
		if(cards.isEmpty()) return null;
		
		List<SaleCard> saleCards = new ArrayList<>();
		
		SaleCard saleCard;
		int i = 0;
		boolean contains;
		
		while(i < 4) {
			contains = false;
			saleCard = generateRandomCard(cards);
			
			if(saleCard.getName().equals("Dao") || saleCard.getName().equals("Da")) continue;
			
			for(int j = 0; j < saleCards.size(); j++) {
				if(saleCards.get(j).getName().equals(saleCard.getName())) {
					contains = true;
					break;
				}
			}
			
			if(!contains) {
				saleCards.add(saleCard);
				this.saleCardRepository.saveAndFlush(saleCard);
				i++;
			}
		}
		
		return saleCards.stream()
				.map(sc -> this.modelMapper.map(sc, SaleCardServiceModel.class))
				.collect(Collectors.toList());
	}

}
