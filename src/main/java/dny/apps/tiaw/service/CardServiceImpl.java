package dny.apps.tiaw.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Rarity;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.card.InvalidCardCreateEditException;
import dny.apps.tiaw.error.rarity.InvalidRarityException;
import dny.apps.tiaw.repository.CardRepository;

@Service
public class CardServiceImpl implements CardService{
	private final CardRepository cardRepository;
	private final ModelMapper modelMapper;
	private final Validator validator;

	@Autowired
	public CardServiceImpl(CardRepository cardRepository, ModelMapper modelMapper, Validator validator) {
		this.cardRepository = cardRepository;
		this.modelMapper = modelMapper;
		this.validator = validator;
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
			throw new InvalidRarityException("Invalid rarity!");
		}
		
		return this.cardRepository.findAll().stream()
				.filter(c-> c.getRarity().toString().equals(rarity))
				.map(c->this.modelMapper.map(c, CardServiceModel.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CardServiceModel> findAll() {
		return this.cardRepository.findAll().stream()
				.map(c -> this.modelMapper.map(c, CardServiceModel.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public void setPrice(Card card) {
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
	}
	
	@Override
	public CardServiceModel createCard(CardServiceModel cardServiceModel) {
		
		if(!this.validator.validate(cardServiceModel).isEmpty()) {
			throw new InvalidCardCreateEditException("Invalid card");
		}
		
		Card card = this.modelMapper.map(cardServiceModel, Card.class);
		
		setPrice(card);
		
		return this.modelMapper.map(this.cardRepository.saveAndFlush(card), CardServiceModel.class);
	}

	@Override
	public CardServiceModel updateCard(String id, CardServiceModel cardServiceModel) {
		
		if(!this.validator.validate(cardServiceModel).isEmpty()) {
			throw new InvalidCardCreateEditException("Invalid card");
		}
		
		Card card = this.cardRepository.findById(id)
				.orElseThrow(() -> new CardNotFoundException("Card with given id does not exist!"));
		
		card.setName(cardServiceModel.getName());
		card.setPower(cardServiceModel.getPower());
		card.setDefense(cardServiceModel.getDefense());
		
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
