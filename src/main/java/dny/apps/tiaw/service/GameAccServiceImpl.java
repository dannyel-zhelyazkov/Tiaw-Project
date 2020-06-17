package dny.apps.tiaw.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.GameAccServiceModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.deck.DeckNotFoundException;
import dny.apps.tiaw.error.gameacc.GameAccOwnCardException;
import dny.apps.tiaw.error.gameacc.NotEnoughGoldException;
import dny.apps.tiaw.error.user.UserNotFoundException;
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
	private final FightService fightService;
	private final ModelMapper modelMapper;

	@Autowired
	public GameAccServiceImpl(GameAccRepository gameAccRepository, DeckRepository deckRepository,
			UserRepository userRepository, CardRepository cardRepository, FightService fightService,
			ModelMapper modelMapper) {
		this.gameAccRepository = gameAccRepository;
		this.deckRepository = deckRepository;
		this.userRepository = userRepository;
		this.cardRepository = cardRepository;
		this.fightService = fightService;
		this.modelMapper = modelMapper;
	}

	@Override
	public GameAccServiceModel findByUser(String username) {
		GameAcc gameAcc = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"))
				.getGameAcc();

		GameAccServiceModel gameAccServiceModel = this.modelMapper.map(gameAcc, GameAccServiceModel.class);
		gameAccServiceModel.setUsername(username);

		return gameAccServiceModel;
	}

	@Override
	public List<GameAccServiceModel> findAll() {
		return this.userRepository.findAll().stream().map(u -> {
			GameAccServiceModel gameAccServiceModel = 
					this.modelMapper.map(u.getGameAcc(), GameAccServiceModel.class);
			gameAccServiceModel.setUsername(u.getUsername());

			return gameAccServiceModel;
		}).collect(Collectors.toList());
	}
	
	@Override
	public Page<GameAccServiceModel> findAllGameAccFightModels(PageRequest pageRequest) {
        Type pageGameAccServiceModelType = new TypeToken<Page<GameAccServiceModel>>() {}.getType();
		
        return this.modelMapper.map(this.gameAccRepository.findAllByDefenseDeckNotNull(pageRequest), pageGameAccServiceModelType);
	}
	
	@Override
	public GameAccServiceModel buyCardByName(String cardName, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		GameAcc gameAcc = user.getGameAcc();
		
		Card card = this.cardRepository.findByName(cardName)
				.orElseThrow(() -> new CardNotFoundException("Card with given name does not exist!"));

		if(gameAcc.getCards().contains(card)) {
			throw new GameAccOwnCardException("You already own this card!");
		}
		
		if(gameAcc.getGold() < card.getPrice()) {
			throw new NotEnoughGoldException("Not enough gold for Card: " + card.getName());
		}
		
		gameAcc.getCards().add(card);
		gameAcc.setGold(gameAcc.getGold() - card.getPrice() / 2);

		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}
	
	@Override
	public GameAccServiceModel buyCardById(String cardId, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		GameAcc gameAcc = user.getGameAcc();
		
		Card card = this.cardRepository.findById(cardId)
				.orElseThrow(() -> new CardNotFoundException("Card with given id does not exist!"));

		if(gameAcc.getCards().contains(card)) {
			throw new GameAccOwnCardException("You already own this card!");
		}
		
		if(gameAcc.getGold() < card.getPrice()) {
			throw new NotEnoughGoldException("Not enough gold for Card: " + card.getName());
		}
		
		gameAcc.getCards().add(card);
		gameAcc.setGold(gameAcc.getGold() - card.getPrice());

		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}

	@Override
	public GameAccServiceModel setDefenseDeck(String deckId, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));

		GameAcc gameAcc = user.getGameAcc();

		if (gameAcc.getDefenseDeck() != null) {
			gameAcc.setDefenseDeck(null);
		}

		Deck deck = this.deckRepository.findById(deckId)
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id does not exist!"));
		
		gameAcc.setDefenseDeck(deck);

		this.deckRepository.saveAndFlush(deck);

		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}

	@Override
	public GameAccServiceModel setAttackDeck(String deckId, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));

		GameAcc gameAcc = user.getGameAcc();

		if (gameAcc.getAttackDeck() != null) {
			gameAcc.setAttackDeck(null);
		}

		Deck deck = this.deckRepository.findById(deckId)
				.orElseThrow(() -> new DeckNotFoundException("Deck with given id does not exist!"));

		gameAcc.setAttackDeck(deck);

		this.deckRepository.saveAndFlush(deck);

		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(gameAcc), GameAccServiceModel.class);
	}

	@Override
	public GameAccServiceModel wonFight(String defender, String attacker, String ubp, String ebp) {
		User defenderAcc = this.userRepository.findByUsername(defender)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));

		User attackerAcc = this.userRepository.findByUsername(attacker)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));

		if(attackerAcc.getGameAcc().getAttackTickets() == 0) {
			return this.modelMapper.map(attackerAcc.getGameAcc(), GameAccServiceModel.class);
		}
		
		this.fightService.registerFight(attacker, defender, Integer.parseInt(ubp), Integer.parseInt(ebp));

		attackerAcc.getGameAcc().setBattlePoints(attackerAcc.getGameAcc().getBattlePoints() + Integer.parseInt(ubp));
		attackerAcc.getGameAcc().setAttackTickets(attackerAcc.getGameAcc().getAttackTickets() - 1);
		attackerAcc.getGameAcc().setGold(attackerAcc.getGameAcc().getGold() + 5L);
		
		defenderAcc.getGameAcc().setBattlePoints(defenderAcc.getGameAcc().getBattlePoints() - Integer.parseInt(ebp));

		this.gameAccRepository.saveAndFlush(defenderAcc.getGameAcc());
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(attackerAcc.getGameAcc()),
				GameAccServiceModel.class);
	}

	@Override
	public GameAccServiceModel lostFight(String defender, String attacker, String ubp, String ebp) {
		User defenderAcc = this.userRepository.findByUsername(defender)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));

		User attackerAcc = this.userRepository.findByUsername(attacker)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));

		if(attackerAcc.getGameAcc().getAttackTickets() == 0) {
			return this.modelMapper.map(attackerAcc.getGameAcc(), GameAccServiceModel.class);
		}
		
		this.fightService.registerFight(defender, attacker, Integer.parseInt(ebp), Integer.parseInt(ubp));

		attackerAcc.getGameAcc().setBattlePoints(attackerAcc.getGameAcc().getBattlePoints() - Integer.parseInt(ubp));
		attackerAcc.getGameAcc().setAttackTickets(attackerAcc.getGameAcc().getAttackTickets() - 1);
		
		defenderAcc.getGameAcc().setBattlePoints(defenderAcc.getGameAcc().getBattlePoints() + Integer.parseInt(ebp));

		this.gameAccRepository.saveAndFlush(defenderAcc.getGameAcc());
		return this.modelMapper.map(this.gameAccRepository.saveAndFlush(attackerAcc.getGameAcc()),
				GameAccServiceModel.class);
	}

	public void resetAttackTickets() {
		List<GameAcc> gameAccs = this.gameAccRepository.findAll().stream()
				.map(ga -> {
					ga.setAttackTickets(3);
					return ga;
				})
				.collect(Collectors.toList());
		
		this.gameAccRepository.saveAll(gameAccs);
	}
}
