package dny.apps.tiaw.web.controllers;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.domain.models.binding.DeckAddBindingModel;
import dny.apps.tiaw.domain.models.service.DeckCreateServiceModel;
import dny.apps.tiaw.domain.models.service.GameAccServiceModel;
import dny.apps.tiaw.domain.models.view.CardViewModel;
import dny.apps.tiaw.domain.models.view.DeckCardsViewModel;
import dny.apps.tiaw.domain.models.view.DeckViewModel;
import dny.apps.tiaw.service.CardService;
import dny.apps.tiaw.service.DeckService;
import dny.apps.tiaw.service.UserService;
import dny.apps.tiaw.validation.controller.DeckCreateValidator;
import dny.apps.tiaw.web.annotations.PageTitle;

@Controller
@RequestMapping("/decks")
public class DeckController extends BaseController {
	private final UserService userService;
	private final DeckService deckService;
	private final CardService cardService;
	private final DeckCreateValidator deckCreateValidator;
	private final ModelMapper modelMapper;

	@Autowired
	public DeckController(UserService userService, DeckService deckService, CardService cardService, DeckCreateValidator deckCreateValidator,
			ModelMapper modelMapper) {
		this.cardService = cardService;
		this.userService = userService;
		this.deckService = deckService;
		this.deckCreateValidator = deckCreateValidator;
		this.modelMapper = modelMapper;
	}
		
	@GetMapping("/deck")
	@PreAuthorize("isAuthenticated()")
	@PageTitle("Deck")
	public ModelAndView deck(ModelAndView modelAndView, Principal principal, @RequestParam(defaultValue = "0") int page) {
		Type listDeckViewModelType = new TypeToken<List<DeckViewModel>>() {}.getType();
		Type pageCardViewModel = new TypeToken<Page<CardViewModel>>() {}.getType();
		
		List<DeckViewModel> deckViewModels = this.modelMapper
				.map(this.deckService.findAllDecksByOwner(principal.getName()), listDeckViewModelType);
		
		Page<CardViewModel> cards = this.modelMapper.map(this.cardService.findAllByOwner(PageRequest.of(page, 4, Sort.by("Name")), principal.getName()), pageCardViewModel);
		
		modelAndView.addObject("bind", new DeckAddBindingModel());
		modelAndView.addObject("username", principal.getName());
		modelAndView.addObject("cards", cards);
		modelAndView.addObject("currentPage", page);
		modelAndView.addObject("decks", deckViewModels);
		modelAndView.addObject("username", principal.getName());
		
		return super.view("/deck/deck", modelAndView);
	}
	
	@PostMapping("/add-deck")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView createDeck(@ModelAttribute("bind") DeckAddBindingModel model, BindingResult result, Principal principal, ModelAndView modelAndView, @RequestParam(defaultValue = "0") int page) {
		
		this.deckCreateValidator.validate(model, result);
		
		if(result.hasErrors()) {
			Type listDeckViewModelType = new TypeToken<List<DeckViewModel>>() {}.getType();
			Type pageCardViewModel = new TypeToken<Page<CardViewModel>>() {}.getType();
			
			List<DeckViewModel> deckViewModels = this.modelMapper
					.map(this.deckService.findAllDecksByOwner(principal.getName()), listDeckViewModelType);
			
			Page<CardViewModel> cards = this.modelMapper.map(this.cardService.findAllByOwner(PageRequest.of(page, 4, Sort.by("Name")), principal.getName()), pageCardViewModel);
			
			modelAndView.addObject("username", principal.getName());
			modelAndView.addObject("cards", cards);
			modelAndView.addObject("currentPage", page);
			modelAndView.addObject("decks", deckViewModels);
			modelAndView.addObject("username", principal.getName());
			
			return super.view("/deck/deck", modelAndView);
		}
		
		DeckCreateServiceModel deckCreateServiceModel = this.modelMapper.map(model, DeckCreateServiceModel.class);
		this.deckService.createDeck(deckCreateServiceModel, principal.getName());
		
		return super.redirect("/decks/deck");
	}
	
	@PostMapping("/remove-deck/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView deleteDeck(@PathVariable String id, Principal principal) {
		this.deckService.deleteDeck(id, principal.getName());
		return super.redirect("/decks/deck");
	}
	
	@GetMapping("/deck-cards/{id}")
	@PreAuthorize("isAuthenticated()")
	@PageTitle("Deck-Cards")
	public ModelAndView viewDeck(@PathVariable String id, ModelAndView modelAndView, Principal principal) {
		DeckCardsViewModel deckCardsViewModel = this.modelMapper
				.map(this.deckService.findById(id), DeckCardsViewModel.class);
		
		GameAccServiceModel gameAccServiceModel = this.modelMapper
				.map(this.userService.findByUsername(principal.getName()).getGameAcc(), GameAccServiceModel.class);
		
		if(gameAccServiceModel.getDefenseDeck() != null && 
				gameAccServiceModel.getDefenseDeck().getId().equals(deckCardsViewModel.getId())) {
			modelAndView.addObject("defense", true);
		} else {
			modelAndView.addObject("defense", false);
		}
		
		if(gameAccServiceModel.getAttackDeck() != null && 
				gameAccServiceModel.getAttackDeck().getId().equals(deckCardsViewModel.getId())) {
			modelAndView.addObject("attack", true);
		} else {
			modelAndView.addObject("attack", false);
		}
		
		modelAndView.addObject("deck", deckCardsViewModel);
		modelAndView.addObject("cards", deckCardsViewModel.getCards());
		modelAndView.addObject("deckId", id);
		
		return super.view("/deck/deck-cards", modelAndView);
	}
	
	@PostMapping("/add-to-deck/{cardId}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView addCard(@PathVariable String cardId, @ModelAttribute("addBind") DeckAddBindingModel model, BindingResult result, Principal principal) {
		
		this.deckService.addCard(model.getName(), cardId, principal.getName());				
		return super.redirect("/decks/deck");
	}
	
	@PostMapping("/remove-from-deck/{card}/{deck}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView removeCard(@PathVariable String card, @PathVariable String deck) {				
		this.deckService.removeCard(deck, card);
	
		return super.redirect("/decks/deck-cards/" + deck);
	}
}
