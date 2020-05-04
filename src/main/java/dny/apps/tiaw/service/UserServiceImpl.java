package dny.apps.tiaw.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.UserServiceModel;
import dny.apps.tiaw.repository.GameAccRepository;
import dny.apps.tiaw.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GameAccRepository gameAccRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, GameAccRepository gameAccRepository, RoleService roleService, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.gameAccRepository = gameAccRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        this.roleService.seedRolesInDb();

        if (this.userRepository.count() == 0) {
            userServiceModel.setAuthorities(this.roleService.findAllRoles());
        } else {
            userServiceModel.setAuthorities(new LinkedHashSet<>());
            userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_USER"));
        }

        User user = this.modelMapper.map(userServiceModel, User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()));

        GameAcc gameAcc = new GameAcc();
		
		gameAcc.setDecks(new HashSet<Deck>());
		gameAcc.setCards(new HashSet<Card>());
		gameAcc.setGold(0L);
		gameAcc.setDiamonds(0L);
		gameAcc.setBattlePoints(100L);
		
		this.gameAccRepository.saveAndFlush(gameAcc);
		
		user.setGameAcc(gameAcc);
        
        return this.modelMapper.map(this.userRepository.saveAndFlush(user), UserServiceModel.class);
    }
    
    @Override
    public UserServiceModel findUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .map(u -> this.modelMapper.map(u, UserServiceModel.class))
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    @Override
    public UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword) {
        User user = this.userRepository.findByUsername(userServiceModel.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));

        if (!this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password!");
        }

        user.setPassword(userServiceModel.getPassword() != null ?
                this.bCryptPasswordEncoder.encode(userServiceModel.getPassword())
                : user.getPassword());

        user.setEmail(userServiceModel.getEmail());

        return this.modelMapper.map(this.userRepository.saveAndFlush(user), UserServiceModel.class);
    }

    @Override
    public List<UserServiceModel> findAllUsers() {
        return this.userRepository.findAll().stream()
                .map(u -> this.modelMapper.map(u, UserServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public void setUserRole(String id, String role) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect id"));

        UserServiceModel userServiceModel = this.modelMapper.map(user, UserServiceModel.class);
        userServiceModel.getAuthorities().clear();

        switch (role) {
            case "user":
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_USER"));
                break;
            case "moderator":
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_USER"));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_MODERATOR"));
                break;
            case "admin":
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_USER"));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_MODERATOR"));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_ADMIN"));
                break;
        }

        this.userRepository.saveAndFlush(this.modelMapper.map(userServiceModel, User.class));
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
	}
}
