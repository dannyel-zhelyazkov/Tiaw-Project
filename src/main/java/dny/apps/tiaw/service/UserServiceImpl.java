package dny.apps.tiaw.service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.Rarity;
import dny.apps.tiaw.domain.entities.Role;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;
import dny.apps.tiaw.domain.models.service.UserServiceModel;
import dny.apps.tiaw.error.user.InvalidUserRegisterException;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.repository.GameAccRepository;
import dny.apps.tiaw.repository.RoleRepository;
import dny.apps.tiaw.repository.UserRepository;
import dny.apps.tiaw.validation.service.UserValidationService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final RoleRepository roleRepository;
    private final GameAccRepository gameAccRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserValidationService userValidationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CardRepository cardRepository, DeckRepository deckRepository, RoleRepository roleRepository, GameAccRepository gameAccRepository, RoleService roleService, 
    		ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, UserValidationService userValidationService) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.roleRepository = roleRepository;
        this.gameAccRepository = gameAccRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userValidationService = userValidationService;
    }

    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 	
		return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with given username was not found!"));
	}
    
    @Override
    public UserServiceModel findByUsername(String username) {	
        return this.modelMapper.map(this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with given username was not found!"))
                , UserServiceModel.class);
    }
    
    @Override
    public List<UserServiceModel> findAll() {
        return this.userRepository.findAll().stream()
                .map(u -> this.modelMapper.map(u, UserServiceModel.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<UserServiceModel> findAll(Pageable pageRequest) {
        Type pageUserServiceModelType = new TypeToken<Page<UserServiceModel>>() {}.getType();
        
        return this.modelMapper.map(this.userRepository.findAll(pageRequest), pageUserServiceModelType);
    }
    
    @Override
    public UserServiceModel registerUser(UserRegisterServiceModel userRegisterServiceModel) {
    	this.roleService.seedRolesInDb();
  
        if(!this.userValidationService.isValid(userRegisterServiceModel)) {
        	throw new InvalidUserRegisterException("Ivalid user!");
        }
        
        userRegisterServiceModel.setPassword(this.bCryptPasswordEncoder
        		.encode(userRegisterServiceModel.getPassword()));
        
        User user = this.modelMapper.map(userRegisterServiceModel, User.class);
        
        if (this.userRepository.count() == 0) {
        	user.setAuthorities(this.roleRepository.findAll().stream()
        			.collect(Collectors.toSet()));
        	Card da = new Card();
        	Card dao = new Card();
        	
        	da.setName("Da");
        	da.setPower(1);
        	da.setDefense(1);
        	da.setPrice(10);
        	da.setUrl("https://res.cloudinary.com/dxpozhcbf/image/upload/v1592386218/iqufsz3p73ocxleqah1h.jpg");
        	da.setReleaseDate(LocalDateTime.now());
        	da.setRarity(Rarity.Common);
        	
        	this.cardRepository.saveAndFlush(da);
        	
        	dao.setName("Dao");
        	dao.setPower(1);
        	dao.setDefense(1);
        	dao.setPrice(10);
        	dao.setUrl("https://res.cloudinary.com/dxpozhcbf/image/upload/v1592386232/jws38ddoqqao1xvsr8yy.jpg");
        	dao.setReleaseDate(LocalDateTime.now());
        	dao.setRarity(Rarity.Common);
        	
        	this.cardRepository.saveAndFlush(dao);
        } else {
        	user.setAuthorities(new HashSet<>());
        	user.getAuthorities().add(this.roleRepository.findByAuthority("ROLE_USER").get());
        }

        GameAcc gameAcc = new GameAcc();
		
        Deck deck = new Deck();
        deck.setName("StartDeck");
        deck.setCards(new ArrayList<>());
        deck.getCards().add(this.cardRepository.findByName("Da").get());
        deck.getCards().add(this.cardRepository.findByName("Dao").get());
        
        this.deckRepository.saveAndFlush(deck);
        
        gameAcc.setUsername(user.getUsername());
		gameAcc.setDecks(new ArrayList<>());
		
		gameAcc.getDecks().add(deck);
		
		gameAcc.setCards(new ArrayList<>());
		gameAcc.getCards().add(this.cardRepository.findByName("Da").get());
		gameAcc.getCards().add(this.cardRepository.findByName("Dao").get());
		
		gameAcc.setGold(50L);
		gameAcc.setBattlePoints(100L);
		gameAcc.setAttackTickets(3);
		gameAcc.setDefenseDeck(deck);
		
		this.gameAccRepository.saveAndFlush(gameAcc);
		
		user.setGameAcc(gameAcc);
        
		this.userRepository.saveAndFlush(user);
		
        return this.modelMapper.map(user, UserServiceModel.class);
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
