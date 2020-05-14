package dny.apps.tiaw.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import dny.apps.tiaw.web.annotations.PageTitle;

@Component
public class TitleInterceptor extends HandlerInterceptorAdapter {
	
	private static final String TITLE = "TIAW";
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if(modelAndView == null) {
			modelAndView = new ModelAndView();
		} else {
			if(handler instanceof HandlerMethod) {
				PageTitle methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(PageTitle.class);
				
				if(methodAnnotation != null) {
					modelAndView.addObject("title", TITLE + '-' + methodAnnotation.value());
				}
			}
		}
	}
}
