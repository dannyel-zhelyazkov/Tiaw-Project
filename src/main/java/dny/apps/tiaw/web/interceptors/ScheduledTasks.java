package dny.apps.tiaw.web.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dny.apps.tiaw.service.GameAccService;

@Component
public class ScheduledTasks {
	private final GameAccService gameAccService;
	
	@Autowired
	public ScheduledTasks(GameAccService gameAccService) {
		this.gameAccService = gameAccService;
	}
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void reportCurrentTime() {
		this.gameAccService.resetAttackTickets();
	}
}
