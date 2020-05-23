package dny.apps.tiaw.service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.domain.models.service.DeckServiceModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.deck.DeckContainsCardException;
import dny.apps.tiaw.error.deck.DeckNotFoundException;
import dny.apps.tiaw.error.deck.DeckSizeException;
import dny.apps.tiaw.error.deck.InvalidDeckCreateException;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.repository.UserRepository;

@Service
public class DeckServiceImpl implements DeckService {
	private final DeckRepository deckRepository;
	private final CardRepository cardRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final Validator validator;
	
	@Autowired
	public DeckServiceImpl(DeckRepository deckRepository, CardRepository cardRepository, UserRepository userRepository, ModelMapper modelMapper,  Validator validator) {
		this.deckRepository = deckRepository;
		this.cardRepository = cardRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.validator = validator;
	}
	
	@Override
	public DeckServiceModel findById(String id) {
		return this.modelMapper.map(this.deckRepository.findById(id)
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id does not exist!")), 
					DeckServiceModel.class);
	}

	@Override
	public DeckServiceModel findByOwner(String username, String deckName) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		
		Deck deck = user.getGameAcc().getDecks().stream()
				.filter(d -> d.getName().equals(deckName)).findFirst()
				.orElseThrow(() -> new DeckNotFoundException("Deck with given name does not exist!"));

		return this.modelMapper.map(deck, DeckServiceModel.class); 
	}
	
	@Override
	public DeckServiceModel findDefenseDeckByOwner(String owner) {
		return this.modelMapper.map(this.userRepository.findByUsername(owner)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"))
				.getGameAcc().getDefenseDeck(), DeckServiceModel.class);
	}
	
	@Override
	public Set<DeckServiceModel> findAllDecksByOwner(String owner) {
		return this.userRepository.findByUsername(owner)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"))
				.getGameAcc().getDecks().stream()
				.map(d->this.modelMapper.map(d, DeckServiceModel.class))
				.collect(Collectors.toSet());
	}
	
	@Override
	public DeckServiceModel createDeck(DeckServiceModel deckServiceModel) {
		
		if(!this.validator.validate(deckServiceModel).isEmpty()) {
			throw new InvalidDeckCreateException("Invalid deck");
		}
		
		deckServiceModel.setCards(new LinkedHashSet<CardServiceModel>());
		
		this.deckRepository.saveAndFlush(this.modelMapper.map(deckServiceModel, Deck.class));
		
		return deckServiceModel;
	}
	
	@Override
	public DeckServiceModel deleteDeck(String id) {
		Deck deck = this.deckRepository.findById(id)
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id does not exist!"));
		
		this.deckRepository.delete(deck);
		
		return this.modelMapper.map(deck, DeckServiceModel.class);
	}
	
	@Override
	public DeckServiceModel addCard(String deckName, String cardId, String username) {
		Deck deck = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"))
				.getGameAcc().getDecks().stream()
				.filter(d -> d.getName().equals(deckName))
				.findFirst()
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id doest not exist!"));
		
		
		if(deck.getCards().size() == 5) {
			throw new DeckSizeException("Deck is full!");
		}
		
		Card card = this.cardRepository.findById(cardId)
				.orElseThrow(() -> new CardNotFoundException("Card with given id was not found!"));
		
		deck.getCards().forEach(c -> {
			if(c.getName().equals(card.getName())) {
				throw new DeckContainsCardException(String.format("%s is already in %s", card.getName(), deck.getName()));
			}
		});
		
		deck.getCards().add(card);
		
		return this.modelMapper.map(this.deckRepository.saveAndFlush(deck), DeckServiceModel.class);
	}
	
	@Override
	public DeckServiceModel removeCard(String deckId, String cardName) {
		Deck deck = this.deckRepository.findById(deckId)
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id doest not exist!"));
		
		deck.getCards().removeIf(c -> c.getName().equals(cardName));
		
		return this.modelMapper.map(this.deckRepository.saveAndFlush(deck), DeckServiceModel.class);
	}
}
