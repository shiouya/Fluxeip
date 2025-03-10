package com.example.fluxeip.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JsonWebTokenInterceptor implements HandlerInterceptor {
	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String method = request.getMethod();
		if("OPTIONS".equals(method)) {
			//GET、POST、PUT、DELETE
			return true;
		}

		String auth = request.getHeader("Authorization");
		if(auth!=null && auth.length()!=0 && auth.startsWith("Bearer ")) {
//			有登入資訊：JWT
			String token = auth.substring(7);
			String subject = jsonWebTokenUtility.validateToken(token);
			if(subject!=null && subject.length()!=0) {
//				通過檢查
				
				return true;
			}
		}
		
//		沒通過檢查
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "*");		
		return false;
	}
}
