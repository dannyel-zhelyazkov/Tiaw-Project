package dny.apps.tiaw.web.controllers;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.error.card.BuyCardGetException;
import dny.apps.tiaw.service.GameAccService;

@Controller
public class GameAccController extends BaseController {
	private final GameAccService gameAccService;
	
	public GameAccController(GameAccService gameAccService) {
		this.gameAccService = gameAccService;
	}
	
	@PostMapping("/buy-card-id/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView buyCardById(@PathVariable String id, Principal principal, ModelAndView modelAndView) {
		this.gameAccService.buyCardById(id, principal.getName());
		return super.redirect("/decks/deck");
	}
	
	@PostMapping("/buy-card-name/{name}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView buyCardByName(@PathVariable String name, Principal principal, ModelAndView modelAndView) {
		this.gameAccService.buyCardByName(name, principal.getName());
		return super.redirect("/decks/deck");
	}
	
	@PostMapping("/set-defense/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView setDefenseDeckPost(@PathVariable String id, Principal principal) {
		this.gameAccService.setDefenseDeck(id, principal.getName());
		return super.redirect("/decks/deck-cards/" + id);
	}
	
	@PostMapping("/set-attack/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView setAttackDeckPost(@PathVariable String id, Principal principal) {
		this.gameAccService.setAttackDeck(id, principal.getName());
		return super.redirect("/decks/deck-cards/" + id);
	}
	
	@ExceptionHandler(BuyCardGetException.class)
	public ModelAndView fullDeck(BuyCardGetException ex) {
		ModelAndView modelAndView = new ModelAndView("error");
		
		modelAndView.addObject("message", ex.getMessage());
		
		return modelAndView;
	}
}
	