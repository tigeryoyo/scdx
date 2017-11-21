package com.hust.scdx.interceptor;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.model.User;

public class PermissionInterceptor implements HandlerInterceptor {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

	@Autowired
	private MappingJackson2HttpMessageConverter converter;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		try {
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute(Constant.UESR);
			if (user == null) {
				logger.warn("用户未登陆，请重新登陆。");
				response.sendRedirect("/");
				return false;
			}
			String url = request.getRequestURI();
			if (url.endsWith(".html")) {
				return true;
			}
			HashSet<String> powers = (HashSet<String>) session.getAttribute(Constant.USERPOWER);
			
			if(powers==null){
				return false;
			}
			
			if (powers.contains(url)) {
				return true;
			}
		} catch (Exception e) {
			response.sendRedirect("/error.html");
			logger.error("permissionInterceptor 错误。 \t" + e.toString());
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}
}
