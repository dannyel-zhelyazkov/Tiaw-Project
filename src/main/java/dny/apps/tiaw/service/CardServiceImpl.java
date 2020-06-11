package dny.apps.tiaw.service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Rarity;
import dny.apps.tiaw.domain.models.service.CardCreateServiceModel;
import dny.apps.tiaw.domain.models.service.CardEditServiceModel;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.card.InvalidCardCreateEditException;
import dny.apps.tiaw.error.rarity.RarityNotFoundException;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.validation.card.CardValidationService;

@Service
public class CardServiceImpl implements CardService{
	private final CardRepository cardRepository;
	private final ModelMapper modelMapper;
	private final CardValidationService cardValidationService;

	@Autowired
	public CardServiceImpl(CardRepository cardRepository, ModelMapper modelMapper, CardValidationService cardValidationService) {
		this.cardRepository = cardRepository;
		this.modelMapper = modelMapper;
		this.cardValidationService = cardValidationService;
	}

	@Override
	public CardServiceModel findById(String id) {
		return this.cardRepository.findById(id)
				.map(c->this.modelMapper.map(c, CardServiceModel.class))
				.orElseThrow(() -> new CardNotFoundException("Card with given id does not exist!"));
	}
	
	@Override
	public List<CardServiceModel> findAllByRarity(String rarity) {		
		if(!Arrays.asList(Rarity.values()).stream()
				.map(r -> r.toString())
				.collect(Collectors.toList())
				.contains(rarity)) {
			throw new RarityNotFoundException("Invalid rarity!");
		}
		
		return this.cardRepository.findAll().stream()
				.filter(c-> c.getRarity().toString().equals(rarity))
				.map(c->this.modelMapper.map(c, CardServiceModel.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CardServiceModel> findAll() {
		return this.cardRepository.findAll().stream()
				.sorted((m,n) -> m.getReleaseDate().compareTo(n.getReleaseDate()))
				.map(c -> this.modelMapper.map(c, CardServiceModel.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public Page<CardServiceModel> findAll(PageRequest pageRequest) {
		Type pageCardServiceModel = new TypeToken< Page<CardServiceModel>>() {}.getType();
		return this.modelMapper.map(this.cardRepository.findAll(pageRequest), pageCardServiceModel);
	}
	
	@Override
	public CardServiceModel createCard(CardCreateServiceModel cardCreateServiceModel) {
		if(!this.cardValidationService.isValid(cardCreateServiceModel)) {
			throw new InvalidCardCreateEditException("Invalid card");
		}
		
		Card card = this.modelMapper.map(cardCreateServiceModel, Card.class);
		
		if(card.getRarity().toString().equals("Common")) {
			card.setPrice(10);
		} else if(card.getRarity().toString().equals("Uncommon")) {
			card.setPrice(30);
		} else if(card.getRarity().toString().equals("Rare")) {
			card.setPrice(70);
		} else if(card.getRarity().toString().equals("Epic")) {
			card.setPrice(120);
		} else if(card.getRarity().toString().equals("Legendary")) {
			card.setPrice(180);
		} else if(card.getRarity().toString().equals("Mythic")) {
			card.setPrice(340);
		}
		
		card.setReleaseDate(LocalDateTime.now());
		
		this.cardRepository.saveAndFlush(card);
		
		return this.modelMapper.map(card, CardServiceModel.class);
	}

	@Override
	public CardServiceModel updateCard(String id, CardEditServiceModel cardEditServiceModel) {
		if(!this.cardValidationService.isValid(cardEditServiceModel)) {
			throw new InvalidCardCreateEditException("Invalid card");
		}
		
		Card card = this.cardRepository.findById(id)
				.orElseThrow(() -> new CardNotFoundException("Card with given id does not exist!"));
		
		card.setName(cardEditServiceModel.getName());
		card.setPower(cardEditServiceModel.getPower());
		card.setDefense(cardEditServiceModel.getDefense());
		
		return this.modelMapper.map(this.cardRepository.saveAndFlush(card), CardServiceModel.class);
	}

	@Override
	public CardServiceModel deleteCard(String id) {
		Card card = this.cardRepository.findById(id)
				.orElseThrow(() -> new CardNotFoundException("Card with given id does not exist!"));
		
        this.cardRepository.delete(card);

        return this.modelMapper.map(card, CardServiceModel.class);
	}
}
