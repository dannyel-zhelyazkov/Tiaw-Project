package dny.apps.tiaw.web.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.domain.models.binding.CardAddBindingModel;
import dny.apps.tiaw.domain.models.binding.CardDeleteBindingModel;
import dny.apps.tiaw.domain.models.binding.CardEditBindingModel;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.domain.models.service.CloudinaryService;
import dny.apps.tiaw.domain.models.service.GameAccServiceModel;
import dny.apps.tiaw.domain.models.view.CardViewModel;
import dny.apps.tiaw.exception.CardNotFoundException;
import dny.apps.tiaw.service.CardService;
import dny.apps.tiaw.service.UserService;
import dny.apps.tiaw.web.annotations.PageTitle;

@Controller
@RequestMapping("/cards")
public class CardController extends BaseController {

	private final CardService cardService;
	private final UserService userService;
	private final ModelMapper modelMapper;
	private final CloudinaryService cloudinaryService;

	@Autowired
	public CardController(CardService cardService, UserService userService, ModelMapper modelMapper,
			CloudinaryService cloudinaryService) {
		this.cardService = cardService;
		this.userService = userService;
		this.modelMapper = modelMapper;
		this.cloudinaryService = cloudinaryService;
	}

	@GetMapping("/add")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PageTitle("Add Card")
	public ModelAndView addCardGet(ModelAndView modelAndView) {
		modelAndView.addObject("card", new CardAddBindingModel());
		return super.view("/card/add-card", modelAndView);
	}

	@PostMapping("/add")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	public ModelAndView addCardPost(@Valid @ModelAttribute("card") CardAddBindingModel model, BindingResult reuslt) throws IOException {
		if(reuslt.hasErrors()) {
			return super.view("/card/add-card");
		}
		
		CardServiceModel cardServiceModel = this.modelMapper.map(model, CardServiceModel.class);
		cardServiceModel.setUrl(this.cloudinaryService.uploadImage(model.getImage()));
		
		this.cardService.createCard(cardServiceModel);

		return super.redirect("/cards/all");
	}

	@GetMapping("/all")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PageTitle("Cards")
	public ModelAndView allCardsGet(ModelAndView model) {
		model.addObject("cards", this.cardService.findAll().stream()
				.map(c -> this.modelMapper.map(c, CardViewModel.class))
				.collect(Collectors.toList()));

		return super.view("/card/all-cards", model);
	}

	@GetMapping("/edit/{id}")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PageTitle("Edit Card")
	public ModelAndView editCardGet(@PathVariable String id, ModelAndView model) {
		CardServiceModel cardServiceModel = this.cardService.findById(id);

		CardEditBindingModel card = this.modelMapper.map(cardServiceModel, CardEditBindingModel.class);

		model.addObject("card", card);
		model.addObject("cardId", id);

		return super.view("/card/edit-card", model);
	}

	@PostMapping("/edit/{id}")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	public ModelAndView editCardPost(@PathVariable String id, @Valid @ModelAttribute("card") CardEditBindingModel model, BindingResult reuslt, ModelAndView modelAndView) {
		if(reuslt.hasErrors()) {
			modelAndView.addObject("card", model);
			modelAndView.addObject("cardId", id);

			return super.view("/card/edit-card", modelAndView);
		}
		
		this.cardService.updateCard(id, this.modelMapper.map(model, CardServiceModel.class));

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
	public ModelAndView shop(ModelAndView modelAndView, Principal principal) {
		modelAndView.addObject("gold", this.userService.findUserByUsername(principal.getName()).getGameAcc().getGold());
		return super.view("/card/shop", modelAndView);
	}

	@GetMapping("/fetch/{rarity}")
	@ResponseBody
	public List<CardViewModel> fetchByRarityGet(@PathVariable String rarity, Principal principal) {
		GameAccServiceModel gameAccServiceModel = this.modelMapper
				.map(this.userService.findUserByUsername(principal.getName()).getGameAcc(), GameAccServiceModel.class);
		
		if (rarity.equals("all")) {
			return this.cardService.findAll().stream()
					.filter(card -> {
						Iterator<CardServiceModel> cards = gameAccServiceModel.getCards().iterator();
						while(cards.hasNext()) {
							if(cards.next().getName().equals(card.getName())) {
								return false;
							}
						}
						
						return true;
					})
					.map(card -> this.modelMapper.map(card, CardViewModel.class))
					.collect(Collectors.toList());
		}

		return this.cardService.findAllByRarity(rarity).stream()
				.map(card -> this.modelMapper.map(card, CardViewModel.class))
				.collect(Collectors.toList());
	}

	@GetMapping("/user-cards/{user}")
	@ResponseBody
	public List<CardViewModel> fetchByUserGet(@PathVariable String user) {
		return this.userService.findUserByUsername(user).getGameAcc().getCards().stream()
				.map(c -> this.modelMapper.map(c, CardViewModel.class))
				.collect(Collectors.toList());
	}
	
	@ExceptionHandler(CardNotFoundException.class)
	public ModelAndView cardNotFound(CardNotFoundException ex) {
		ModelAndView modelAndView = new ModelAndView("/card/not-found");
		
		modelAndView.addObject("message", ex.getMessage());
		
		return modelAndView;
	}
}
