package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dny.apps.tiaw.domain.entities.Fight;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.FightServiceModel;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.FightRepository;
import dny.apps.tiaw.repository.UserRepository;

class FightServiceTest extends BaseServiceTest {

	@Autowired
	private FightService fightService;
	
	@MockBean
	private FightRepository fightRepsitory;
	
	@MockBean 
	private UserRepository userRepository;
	
	@Test
	void findAllByUser_whenUserDoesNotExists_shouldThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () ->
			this.fightService.findAllByUser("WRONG_USER")
		);
	}
	
	@Test
	void testFindAllByUser() {
		User user = new User();
		user.setGameAcc(new GameAcc());
		user.getGameAcc().setId("WINNEER_ID");
		
		GameAcc winner = new GameAcc();
		winner.setId("WINNEER_ID");
		GameAcc loser = new GameAcc();
		loser.setId("LOSER_ID");
		
		Fight fight = new Fight();
		fight.setFightTime(LocalDateTime.now());
		fight.setWinner(winner);
		fight.setLoser(loser);
		
		List<Fight> allFights = new ArrayList<>();
		allFights.add(fight);
		
		Mockito.when(this.fightRepsitory.findAll())
			.thenReturn(allFights);
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		List<FightServiceModel> userFights = this.fightService.findAllByUser("USER");
		
		assertEquals(userFights.size(), 1);
	}

	@Test
	void registerFight() {
		User winner = new User();
		winner.setGameAcc(new GameAcc());
		winner.getGameAcc().setUsername("WINNER");
		
		User loser = new User();
		loser.setGameAcc(new GameAcc());
		loser.getGameAcc().setUsername("LOSER");
		
		Mockito.when(this.userRepository.findByUsername("WINNER"))
			.thenReturn(Optional.of(winner));
		
		Mockito.when(this.userRepository.findByUsername("LOSER"))
		.thenReturn(Optional.of(loser));
		
		this.fightService.registerFight("WINNER", "LOSER", 10, 20);
		
		ArgumentCaptor<Fight> argumentFight = ArgumentCaptor.forClass(Fight.class);

		Mockito.verify(this.fightRepsitory).saveAndFlush(argumentFight.capture());		
		
		Fight fight = argumentFight.getValue();
		
		assertEquals(fight.getWinner().getUsername(), winner.getGameAcc().getUsername());
		assertEquals(fight.getLoser().getUsername(), loser.getGameAcc().getUsername());
		assertEquals(fight.getWinnerWonPoints(), 10);
		assertEquals(fight.getLoserLostPoints(), 20);
	}
}
