package dny.apps.tiaw.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.GameAccServiceModel;
import dny.apps.tiaw.exception.CardNotFoundException;
import dny.apps.tiaw.exception.DeckNotFoundException;
import dny.apps.tiaw.exception.UserNotFoundException;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.repository.GameAccRepository;
import dny.apps.tiaw.repository.UserRepository;

@Service
public class GameAccServiceImpl implements GameAccService {
	private final GameAccRepository gameAccRepository;
	private final DeckRepository deckRepository;
	private final UserRepository userRepository;
	private final CardRepository cardRepository;
	private final ModelMapper modelMapper;
	
	@Autowired
	public GameAccServiceImpl(GameAccRepository gameAccRepository, DeckRepository deckRepository, UserRepository userRepository, 
			CardRepository cardRepository, ModelMapper modelMapper) {
		this.gameAccRepository = gameAccRepository;
		this.deckRepository = deckRepository;
		this.userRepository = userRepository;
		this.cardRepository = cardRepository;
		this.modelMapper = modelMapper;
	}
	
	@Override
	public GameAccServiceModel findByUser(String username) {
		GameAcc gameAcc = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!")).getGameAcc();
		
		GameAccServiceModel gameAccServiceModel = this.modelMapper.map(gameAcc, GameAccServiceModel.class);
		
		gameAccServiceModel.setUsername(username);
		
		return gameAccServiceModel;
	}
	
	@Override
	public List<GameAccServiceModel> findAllGameAccs() {
		return this.userRepository.findAll().stream()
				.filter(u -> u.getGameAcc().getDefenseDeck() != null)
				.sorted((u, u1) -> u1.getGameAcc().getBattlePoints().compareTo(u.getGameAcc().getBattlePoints()))
				.map(u-> {
					GameAccServiceModel gameAccServiceModel = this.modelMapper.map(u.getGameAcc(), GameAccServiceModel.class);
					gameAccServiceModel.setUsername(u.getUsername());
					
					return gameAccServiceModel;
				})
				.collect(Collectors.toList());
	}
	
	@Override
	public GameAccServiceModel buyCard(String cardId, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		GameAcc gameAcc = user.getGameAcc();
		Card card = this.cardRepository.findById(cardId)
				.orElseThrow(() -> new CardNotFoundException("Card with given id does not exist!"));
		gameAcc.getCards().add(card);
						
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}
	
	@Override
	public GameAccServiceModel addDeck(String deckId, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		GameAcc gameAcc = user.getGameAcc();
		gameAcc.getDecks().add(this.deckRepository.findById(deckId)
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id does not exist!")));
		
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}
	
	@Override
	public GameAccServiceModel setDefense(String deckId, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));

		GameAcc gameAcc = user.getGameAcc();
		
		if(gameAcc.getDefenseDeck() != null) {			
			gameAcc.setDefenseDeck(null);
		}
		
		Deck deck = this.deckRepository.findById(deckId)
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id does not exist!"));
		gameAcc.setDefenseDeck(deck);
		
		this.deckRepository.saveAndFlush(deck);
				
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}
	
	@Override
	public GameAccServiceModel setAttack(String deckId, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		
		GameAcc gameAcc = user.getGameAcc();
		
		if(gameAcc.getAttackDeck() != null) {
			gameAcc.setAttackDeck(null);
		}
		
		Deck deck = this.deckRepository.findById(deckId)
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id does not exist!"));
		
		gameAcc.setAttackDeck(deck);
		
		this.deckRepository.saveAndFlush(deck);
				
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}

	@Override
	public GameAccServiceModel removeDeck(String id, String username) {
		User user =  this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		
		GameAcc gameAcc = user.getGameAcc();
		gameAcc.getDecks().removeIf(d -> d.getId().equals(id));
		
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}

	@Override
	public GameAccServiceModel fight(String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		
		GameAcc gameAcc = user.getGameAcc();
		gameAcc.setGold(gameAcc.getGold() + 10);
		gameAcc.setBattlePoints(gameAcc.getBattlePoints() + 10);
		
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}

	@Override
	public GameAccServiceModel wonFight(String defender, String attacker, String ubp, String ebp) {
		User defenderAcc = this.userRepository.findByUsername(defender)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		
		User attackerAcc = this.userRepository.findByUsername(attacker)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
				
		attackerAcc.getGameAcc().setBattlePoints(attackerAcc.getGameAcc().getBattlePoints() + Integer.parseInt(ubp));
		attackerAcc.getGameAcc().setGold(attackerAcc.getGameAcc().getGold() + 5L);
		defenderAcc.getGameAcc().setBattlePoints(defenderAcc.getGameAcc().getBattlePoints() - Integer.parseInt(ebp));
		
		this.gameAccRepository.saveAndFlush(defenderAcc.getGameAcc());
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(attackerAcc.getGameAcc()), GameAccServiceModel.class);
	}
	
	@Override
	public GameAccServiceModel lostFight(String defender, String attacker, String ubp, String ebp) {
		User defenderAcc = this.userRepository.findByUsername(defender)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		
		User attackerAcc = this.userRepository.findByUsername(attacker)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
				
		attackerAcc.getGameAcc().setBattlePoints(attackerAcc.getGameAcc().getBattlePoints() - Integer.parseInt(ubp));
		defenderAcc.getGameAcc().setBattlePoints(defenderAcc.getGameAcc().getBattlePoints() + Integer.parseInt(ebp));
		
		this.gameAccRepository.saveAndFlush(defenderAcc.getGameAcc());
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(attackerAcc.getGameAcc()), GameAccServiceModel.class);
	}
}
