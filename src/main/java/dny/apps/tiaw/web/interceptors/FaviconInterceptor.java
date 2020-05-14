package dny.apps.tiaw.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class FaviconInterceptor extends HandlerInterceptorAdapter {
	
	private final static String FAVICON_LINK = "https://res.cloudinary.com/dxpozhcbf/image/upload/v1589458043/favicon/favicon_huw2kp.ico";
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if(modelAndView != null) {
			modelAndView.addObject("favicon", FAVICON_LINK);
		}
	}
}
