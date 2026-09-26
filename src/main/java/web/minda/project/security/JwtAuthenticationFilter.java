package web.minda.project.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.Session;

//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//	private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
//
//	@Autowired
//	private JwtHelper jwtHelper;
//
//	@Autowired
//	private UserDetailsService userDetailsService;
//	
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//	        throws ServletException, IOException {
//
//	    String requestHeader = request.getHeader("Authorization");
//	    String token = null;
//	    String username = null;
////	    // ✅ 1. If no Authorization header, try cookies
////	    if (requestHeader == null) {
////	    	
////	        
////	    }else {
////	    	 response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorised Access");
////	         return;
////		}
////	    
//	    if (request.getCookies() != null) {
//            for (Cookie cookie : request.getCookies()) {
//                if ("JWT_TOKEN".equals(cookie.getName())) {   // your cookie name
//                    token = cookie.getValue();
//                    requestHeader = "Bearer " + token;
//                    break;
//                }
//            }
//        }else {
//        	 response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorised Access");
//	         return;
//        }
//
//	    if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
//	        token = requestHeader.substring(7);
//	        try {
//	            username = this.jwtHelper.getUsernameFromToken(token);
//	        } catch (IllegalArgumentException e) {
//	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
//	            return;
//	        } catch (ExpiredJwtException e) {
//	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session Expired. Please login again.");
//	            return;
//	        } catch (MalformedJwtException e) {
//	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed Token");
//	            return;
//	        }
//	    }
//
//	    // ✅ 2. Authenticate
//	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//	        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//
//	        if (this.jwtHelper.validateToken(token, userDetails)) {
//	            UsernamePasswordAuthenticationToken authentication =
//	                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//	            SecurityContextHolder.getContext().setAuthentication(authentication);
//	        } else {
//	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Validation Failed");
//	            return;
//	        }
//	    }
//
//	    filterChain.doFilter(request, response);
//	}
//

//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//
//		String requestHeader = request.getHeader("Authorization");
//
//		if (requestHeader == null) {
//			try {
//				String queryString = request.getQueryString();
//
//				if (queryString != null) {
//					String[] params = queryString.split("&");
//					for (String param : params) {
//						String[] pair = param.split("=");
//						if (pair.length == 2 && pair[0].equals("token")) {
//							requestHeader = "Bearer " + pair[1];
//							break;
//						}
//					}
//				}
//			} catch (Exception e) {
//				System.out.println(e);
//			}
//		}
//
//		String username = null;
//		String token = null;
//
//		if (requestHeader != null && requestHeader.startsWith("Bearer")) {
//			token = requestHeader.substring(7);
//			try {
//				username = this.jwtHelper.getUsernameFromToken(token);
//			} catch (IllegalArgumentException e) {
//				PrintWriter writer = response.getWriter();
//				writer.println("Access Denied !! " + "Illegal Argument while fetching the username.");
//			} catch (ExpiredJwtException e) {
//				PrintWriter writer = response.getWriter();
//				writer.println("Access Denied !! " + "Session time out. please login again.");
//			} catch (MalformedJwtException e) {
//				PrintWriter writer = response.getWriter();
//				writer.println("Access Denied !! " + "Some changed has done in with Security Token.");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			// logger.info("Invalid Header Value !! ");
//		}
//
//		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//			Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
//
//			if (validateToken) {
//				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//						userDetails, null, userDetails.getAuthorities());
//				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//				SecurityContextHolder.getContext().setAuthentication(authentication);
//
//			} else {
//				PrintWriter writer = response.getWriter();
//				writer.println("Access Denied !! " + "Validation fails !!");
//			}
//		}
//
//		filterChain.doFilter(request, response);
//
//	}
//}

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();

		return path.equals("/login") || path.equals("/unauthorizeaccess") || path.startsWith("/css/")
				|| path.startsWith("/js/") || path.startsWith("/images/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = null;

		// 🔹 Extract token from cookie (if exists)
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("JWT_TOKEN".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}

		// 🔹 No token → allow request to continue
		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String username = jwtHelper.getUsernameFromToken(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				if (jwtHelper.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}

		} catch (ExpiredJwtException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired");
			return;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
