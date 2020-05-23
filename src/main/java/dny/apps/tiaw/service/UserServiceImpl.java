package dny.apps.tiaw.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.Role;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.UserServiceModel;
import dny.apps.tiaw.error.user.InvalidUserRegisterException;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.GameAccRepository;
import dny.apps.tiaw.repository.RoleRepository;
import dny.apps.tiaw.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GameAccRepository gameAccRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Validator validator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, GameAccRepository gameAccRepository, RoleService roleService, 
    		ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, Validator validator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.gameAccRepository = gameAccRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.validator = validator;
    }

    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 	
		return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with given username was not found!"));
	}
    
    @Override
    public UserServiceModel findUserByUsername(String username) {
    	
    	if(username.equals(null)) {
    		throw new UserNotFoundException("User with given username was not found!");
    	}
    	
        return this.userRepository.findByUsername(username)
                .map(u -> this.modelMapper.map(u, UserServiceModel.class))
                .orElseThrow(() -> new UserNotFoundException("User with given username was not found!"));
    }
    
    @Override
    public List<UserServiceModel> findAllUsers() {
        return this.userRepository.findAll().stream()
                .map(u -> this.modelMapper.map(u, UserServiceModel.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        this.roleService.seedRolesInDb();

        if(!this.validator.validate(userServiceModel).isEmpty()) {
        	throw new InvalidUserRegisterException("Ivalid user!");
        }
        
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
		gameAcc.setGold(50L);
		gameAcc.setBattlePoints(100L);
		gameAcc.setAttackTickets(3);
		
		this.gameAccRepository.saveAndFlush(gameAcc);
		
		user.setGameAcc(gameAcc);
        
        return this.modelMapper.map(this.userRepository.saveAndFlush(user), UserServiceModel.class);
    }
    
    //TODO
    @Override
    public UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword) {
        User user = this.userRepository.findByUsername(userServiceModel.getUsername())
        		.orElseThrow(() -> new UserNotFoundException("User with given username was not found!"));
       
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
    public void setUserRole(String id, String role) {
        User user = this.userRepository.findById(id)
        		.orElseThrow(() -> new UserNotFoundException("User with given id was not found!"));
        
       if(user.getAuthorities() != null) {
    	   user.getAuthorities().clear();
       }
        
        Role userRole = this.roleRepository.findByAuthority("ROLE_USER").get();  
        Role moderatorRole = this.roleRepository.findByAuthority("ROLE_MODERATOR").get();
        Role adminRole = this.roleRepository.findByAuthority("ROLE_ADMIN").get();
        
        switch (role) {
            case "user":
            	user.getAuthorities().add(userRole);
                break;
            case "moderator":
            	user.getAuthorities().add(userRole);
            	user.getAuthorities().add(moderatorRole);
                break;
            case "admin":
            	user.getAuthorities().add(userRole);
            	user.getAuthorities().add(moderatorRole);
            	user.getAuthorities().add(adminRole);
                break;
        }

        this.userRepository.saveAndFlush(user);
    }
}
