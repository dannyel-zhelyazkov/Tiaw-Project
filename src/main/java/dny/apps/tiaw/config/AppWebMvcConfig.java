package dny.apps.tiaw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dny.apps.tiaw.web.interceptors.FaviconInterceptor;
import dny.apps.tiaw.web.interceptors.TitleInterceptor;

@Configuration
public class AppWebMvcConfig implements WebMvcConfigurer {

	private final TitleInterceptor titleInterceptor;
	private final FaviconInterceptor faviconInterceptor;

	@Autowired
	public AppWebMvcConfig(TitleInterceptor titleInterceptor, FaviconInterceptor faviconInterceptor) {
		this.titleInterceptor = titleInterceptor;
		this.faviconInterceptor = faviconInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this.titleInterceptor);
		registry.addInterceptor(this.faviconInterceptor);
	}
}
