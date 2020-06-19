package dny.apps.tiaw.web.controllers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.domain.models.binding.CardCreateBindingModel;
import dny.apps.tiaw.domain.models.binding.CardDeleteBindingModel;
import dny.apps.tiaw.domain.models.binding.CardEditBindingModel;
import dny.apps.tiaw.domain.models.binding.CardSearchBindingModel;
import dny.apps.tiaw.domain.models.service.CardCreateServiceModel;
import dny.apps.tiaw.domain.models.service.CardEditServiceModel;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.domain.models.view.CardViewModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.card.InvalidCardCreateEditException;
import dny.apps.tiaw.error.rarity.RarityNotFoundException;
import dny.apps.tiaw.service.CardService;
import dny.apps.tiaw.service.CloudinaryService;
import dny.apps.tiaw.service.UserService;
import dny.apps.tiaw.validation.controller.CardCreateValidaor;
import dny.apps.tiaw.validation.controller.CardEditValidator;
import dny.apps.tiaw.validation.controller.CardSearchValidator;
import dny.apps.tiaw.web.annotations.PageTitle;

@Controller
@RequestMapping("/cards")
public class CardController extends BaseController {

	private final CardService cardService;
	private final UserService userService;
	private final ModelMapper modelMapper;
	private final CardCreateValidaor cardCreateValidator;
	private final CardEditValidator cardEditValidator;
	private final CardSearchValidator cardSerSearchValidator;
	private final CloudinaryService cloudinaryService;

	@Autowired
	public CardController(CardService cardService, UserService userService, ModelMapper modelMapper,
			CardCreateValidaor cardCreateValidator, CardEditValidator cardEditValidator, CardSearchValidator cardSerSearchValidator, 
			CloudinaryService cloudinaryService) {
		this.cardService = cardService;
		this.userService = userService;
		this.modelMapper = modelMapper;
		this.cardCreateValidator = cardCreateValidator;
		this.cardEditValidator = cardEditValidator;
		this.cardSerSearchValidator = cardSerSearchValidator;
		this.cloudinaryService = cloudinaryService;
	}

	@GetMapping("/add")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PageTitle("Add Card")
	public ModelAndView addCardGet(ModelAndView modelAndView) {
		modelAndView.addObject("card", new CardCreateBindingModel());
		return super.view("/card/add-card", modelAndView);
	}

	@PostMapping("/add")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	public ModelAndView addCardPost(@ModelAttribute("card") CardCreateBindingModel model, BindingResult reuslt) throws IOException {
		
		this.cardCreateValidator.validate(model, reuslt);
		
		if(reuslt.hasErrors()) {
			return super.view("/card/add-card");
		}
		
		CardCreateServiceModel cardCreateServiceModel = this.modelMapper.map(model, CardCreateServiceModel.class);
		cardCreateServiceModel.setUrl(this.cloudinaryService.uploadImage(model.getImage()));
		
		this.cardService.createCard(cardCreateServiceModel);

		return super.redirect("/cards/all");
	}

	@GetMapping("/all")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PageTitle("Cards")
	public ModelAndView allCardsGet(ModelAndView modelAndView, @RequestParam(defaultValue = "0") int page) {
		
		Type pageCardViewModeType = new TypeToken<Page<CardViewModel>> () {}.getType();
		
		Page<CardViewModel> cards = this.modelMapper.map(this.cardService.findAll(PageRequest.of(page, 6, Sort.by("releaseDate"))),pageCardViewModeType);
		
		modelAndView.addObject("card", new CardSearchBindingModel());
		modelAndView.addObject("cards", cards);
		modelAndView.addObject("currentPage", page);
		return super.view("/card/all-cards", modelAndView);
	}
	
	@PostMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView search(ModelAndView modelAndView, @ModelAttribute("card") CardSearchBindingModel model, BindingResult result, @RequestParam(defaultValue = "0") int page) {
		
		this.cardSerSearchValidator.validate(model, result);

		if(result.hasErrors()) {
			Type pageCardViewModeType = new TypeToken<Page<CardViewModel>> () {}.getType();
			
			Page<CardViewModel> cards = this.modelMapper.map(this.cardService.findAll(PageRequest.of(page, 6, Sort.by("releaseDate"))),pageCardViewModeType);
			
			modelAndView.addObject("cards", cards);
			modelAndView.addObject("currentPage", page);
			
			return super.view("/card/all-cards", modelAndView);
		}
		
    	CardViewModel card = this.modelMapper.map(this.cardService.findByName(model.getName()), CardViewModel.class);
    	
    	modelAndView.addObject("card", card);
    	
    	return super.view("/card/card-search", modelAndView);
    }

	@GetMapping("/edit/{id}")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PageTitle("Edit Card")
	public ModelAndView editCardGet(@PathVariable String id, ModelAndView modelAndView) {
		CardServiceModel cardServiceModel = this.cardService.findById(id);

		CardEditBindingModel card = this.modelMapper.map(cardServiceModel, CardEditBindingModel.class);
		card.setOldName(card.getName());
		
		modelAndView.addObject("card", card);
		modelAndView.addObject("cardId", id);

		return super.view("/card/edit-card", modelAndView);
	}

	@PostMapping("/edit/{id}")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	public ModelAndView editCardPost(@PathVariable String id, @ModelAttribute("card") CardEditBindingModel model, BindingResult reuslt, ModelAndView modelAndView) {
		this.cardEditValidator.validate(model, reuslt);
		
		if(reuslt.hasErrors()) {
			modelAndView.addObject("cardId", id);

			return super.view("/card/edit-card", modelAndView);
		}
		
		this.cardService.updateCard(id, this.modelMapper.map(model, CardEditServiceModel.class));

		return super.redirect("/cards/all");
	}

	@GetMapping("/delete/{id}")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PageTitle("Delete Card")
	public ModelAndView deleteCardGet(@PathVariable String id, ModelAndView modelAndView) {
		CardServiceModel cardServiceModel = this.cardService.findById(id);

		CardDeleteBindingModel card = this.modelMapper.map(cardServiceModel, CardDeleteBindingModel.class);

		modelAndView.addObject("card", card);
		modelAndView.addObject("cardId", id);

		return super.view("/card/delete-card", modelAndView);
	}

	@PostMapping("/delete/{id}")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	public ModelAndView deleteCardPost(@PathVariable String id) {
		this.cardService.deleteCard(id);

		return super.redirect("/cards/all");
	}

	@GetMapping("/view/{id}")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PageTitle("View Card")
	public ModelAndView viewCardGet(@PathVariable String id, ModelAndView model) {
		CardServiceModel cardServiceModel = this.cardService.findById(id);

		CardViewModel cardViewModel = this.modelMapper.map(cardServiceModel, CardViewModel.class);

		model.addObject("card", cardViewModel);

		return super.view("/card/view-card", model);
	}

	@GetMapping("/shop")
	@PreAuthorize("isAuthenticated()")
	@PageTitle("Shop")
	public ModelAndView shop(ModelAndView modelAndView, Principal principal, @RequestParam(defaultValue = "0") int page) {
		Type pageCardViewModel = new TypeToken<Page<CardViewModel>>() {}.getType();
		Page<CardViewModel> cards = this.modelMapper.map(this.cardService.findAll(PageRequest.of(page, 4, Sort.by("releaseDate"))), pageCardViewModel);
		
		modelAndView.addObject("cards", cards);
		modelAndView.addObject("currentPage", page);
		modelAndView.addObject("gold", this.userService.findByUsername(principal.getName()).getGameAcc().getGold());
		
		return super.view("/card/shop", modelAndView);
	}

	@GetMapping("/shop/{rarity}")
	@ResponseBody
	public ModelAndView fetchByRarity(ModelAndView modelAndView, @PathVariable String rarity, Principal principal, @RequestParam(defaultValue = "0") int page) {
		Type pageCardViewModel = new TypeToken<Page<CardViewModel>>() {}.getType();
		Page<CardViewModel> cards = this.modelMapper.map(this.cardService.findAllByRarity(PageRequest.of(page, 4, Sort.by("releaseDate")), rarity), pageCardViewModel);
		
		modelAndView.addObject("cards", cards);
		modelAndView.addObject("rarity", rarity);
		modelAndView.addObject("currentPage", page);
		modelAndView.addObject("gold", this.userService.findByUsername(principal.getName()).getGameAcc().getGold());
		
		return super.view("/card/shop", modelAndView);
	}

	@GetMapping("/user-cards/{user}")
	@ResponseBody
	public List<CardViewModel> fetchByUserGet(@PathVariable String user) {
		return this.userService.findByUsername(user).getGameAcc().getCards().stream()
				.map(c -> this.modelMapper.map(c, CardViewModel.class))
				.collect(Collectors.toList());
	}
	
	@ExceptionHandler({CardNotFoundException.class, InvalidCardCreateEditException.class, RarityNotFoundException.class})
	public ModelAndView cardNotFound(Throwable ex) {
		ModelAndView modelAndView = new ModelAndView("/card/card-error");
		
		modelAndView.addObject("message", ex.getMessage());
		
		return modelAndView;
	}
}
