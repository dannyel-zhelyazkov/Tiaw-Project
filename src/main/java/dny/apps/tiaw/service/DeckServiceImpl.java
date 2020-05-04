package dny.apps.tiaw.service;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.domain.models.service.DeckServiceModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.repository.UserRepository;

@Service
public class DeckServiceImpl implements DeckService {
	private final DeckRepository deckRepository;
	private final CardRepository cardRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	
	@Autowired
	public DeckServiceImpl(DeckRepository deckRepository, CardRepository cardRepository, UserRepository userRepository, ModelMapper modelMapper) {
		this.deckRepository = deckRepository;
		this.cardRepository = cardRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}
	
	@Override
	public DeckServiceModel findById(String id) {
		Deck deck = this.deckRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		DeckServiceModel deckServiceModel = this.modelMapper.map(deck, DeckServiceModel.class);
		deckServiceModel.setCards(deck.getCards().stream()
				.map(c->this.modelMapper.map(c, CardServiceModel.class))
				.collect(Collectors.toList()));
		
		return deckServiceModel;
	}

	@Override
	public DeckServiceModel findByOwner(String username, String deckName) {
		User user = this.userRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
		Deck deck = user.getGameAcc().getDecks().stream().filter(d -> d.getName().equals(deckName)).findFirst().orElseThrow(IllegalArgumentException::new);

		return this.modelMapper.map(deck, DeckServiceModel.class); 
	}
	
	@Override
	public DeckServiceModel findDefenseDeckByOwner(String owner) {
		return this.modelMapper.map(this.userRepository.findByUsername(owner)
				.orElseThrow(IllegalArgumentException::new)
				.getGameAcc().getDefenseDeck(), DeckServiceModel.class);
	}
	
	@Override
	public Set<DeckServiceModel> findAllDecksByOwner(String owner) {
		return this.userRepository.findByUsername(owner)
				.orElseThrow(IllegalArgumentException::new)
				.getGameAcc().getDecks().stream()
				.map(d->this.modelMapper.map(d, DeckServiceModel.class))
				.collect(Collectors.toSet());
	}
	
	@Override
	public DeckServiceModel createDeck(DeckServiceModel deckServiceModel) {
		Deck deck = this.modelMapper.map(deckServiceModel, Deck.class);
		deck.setCards(new ArrayList<Card>());
		
		return this.modelMapper.map(this.deckRepository.saveAndFlush(deck), DeckServiceModel.class);
	}
	
	@Override
	public DeckServiceModel deleteDeck(String id) {
		Deck deck = this.deckRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		this.deckRepository.delete(deck);
		
		return this.modelMapper.map(deck, DeckServiceModel.class);
	}
	
	@Override
	public DeckServiceModel addCard(String deckName, String cardId, String username) {
		Deck deck = this.modelMapper.map(findByOwner(username, deckName), Deck.class);
		deck.getCards().add(this.cardRepository.findById(cardId).orElseThrow(IllegalArgumentException::new));
		
		return this.modelMapper.map(this.deckRepository.saveAndFlush(deck), DeckServiceModel.class);
	}
	
	@Override
	public DeckServiceModel removeCard(DeckServiceModel deckServiceModel, CardServiceModel cardServiceModel) {
		Deck deck = this.modelMapper.map(deckServiceModel, Deck.class);
		deck.getCards().removeIf(c -> c.getName().equals(cardServiceModel.getName()));
		
		return this.modelMapper.map(this.deckRepository.saveAndFlush(deck), DeckServiceModel.class);
	}
}
