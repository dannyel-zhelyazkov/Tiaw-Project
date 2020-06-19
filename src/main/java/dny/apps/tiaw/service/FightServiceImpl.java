package dny.apps.tiaw.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Fight;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.FightServiceModel;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.FightRepository;
import dny.apps.tiaw.repository.UserRepository;

@Service
public class FightServiceImpl implements FightService {

	private final FightRepository fightRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	
	@Autowired
	public FightServiceImpl(FightRepository fightRepository, UserRepository userRepository, ModelMapper modelMapper) {
		this.fightRepository = fightRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}
		
	@Override
	public List<FightServiceModel> findAllByUser(String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));
		
		GameAcc gameAcc = user.getGameAcc();
		
		List<FightServiceModel> fights = this.fightRepository.findAll().stream()
				.sorted((f1,f2) -> f2.getFightTime().compareTo(f1.getFightTime()))
				.filter(f -> {
					return f.getWinner().getId().equals(gameAcc.getId()) || f.getLoser().getId().equals(gameAcc.getId());
				})
				.limit(10)
				.map(f -> this.modelMapper.map(f, FightServiceModel.class))
				.collect(Collectors.toList());
		
		return fights;
	}

	@Override
	public FightServiceModel registerFight(String winner, String loser, Integer winnerWonPoints, Integer loserLostPoints) {
		Fight fight = new Fight();

		GameAcc w = this.userRepository.findByUsername(winner).get().getGameAcc();
		GameAcc l = this.userRepository.findByUsername(loser).get().getGameAcc();
		
		fight.setFightTime(LocalDateTime.now());
		fight.setWinner(w);
		fight.setLoser(l);
		fight.setWinnerWonPoints(winnerWonPoints);
		fight.setLoserLostPoints(loserLostPoints);

		this.fightRepository.saveAndFlush(fight);
		
		return this.modelMapper.map(fight, FightServiceModel.class);
	}}
