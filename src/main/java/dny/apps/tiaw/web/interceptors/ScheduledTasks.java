package dny.apps.tiaw.web.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dny.apps.tiaw.service.GameAccService;
import dny.apps.tiaw.service.SaleCardService;

@Component
public class ScheduledTasks {
	private final GameAccService gameAccService;
	private final SaleCardService saleCardService;
	
	@Autowired
	public ScheduledTasks(GameAccService gameAccService, SaleCardService saleCardService) {
		this.gameAccService = gameAccService;
		this.saleCardService = saleCardService;
	}
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void reportCurrentTime() {
		this.gameAccService.resetAttackTickets();
	}
	
	@Scheduled(cron = "*/60 * * * * *")
	public void cardsOnSale() {
		this.saleCardService.setForSale();
	}
}
