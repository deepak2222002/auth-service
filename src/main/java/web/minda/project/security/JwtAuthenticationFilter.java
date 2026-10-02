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
//	private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//
//	@Autowired
//	private JwtHelper jwtHelper;
//
//	@Autowired
//	private UserDetailsService userDetailsService;
//
//	@Override
//	protected boolean shouldNotFilter(HttpServletRequest request) {
//		String path = request.getServletPath();
//
//		return path.equals("/login") || path.equals("/unauthorizeaccess") || path.startsWith("/css/")
//				|| path.startsWith("/js/") || path.startsWith("/images/");
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//
//		String token = null;
//
//		// 🔹 Extract token from cookie (if exists)
//		if (request.getCookies() != null) {
//			for (Cookie cookie : request.getCookies()) {
//				if ("JWT_TOKEN".equals(cookie.getName())) {
//					token = cookie.getValue();
//					break;
//				}
//			}
//		}
//
//		// 🔹 No token → allow request to continue
//		if (token == null) {
//			filterChain.doFilter(request, response);
//			return;
//		}
//
//		try {
//			String username = jwtHelper.getUsernameFromToken(token);
//
//			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//				if (jwtHelper.validateToken(token, userDetails)) {
//					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//							userDetails, null, userDetails.getAuthorities());
//
//					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//					SecurityContextHolder.getContext().setAuthentication(authentication);
//				}
//			}
//
//		} catch (ExpiredJwtException e) {
//			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired");
//			return;
//		} catch (Exception e) {
//			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
//			return;
//		}
//
//		filterChain.doFilter(request, response);
//	}
//}



@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.equals("/login")
                || path.equals("/loginpage")
                || path.equals("/unauthorizeaccess")
                || path.equals("/favicon.ico")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/image/")
                || path.startsWith("/projectLoginpage/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        // Extract JWT token from cookie
        if (request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {

                if ("JWT_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // No token -> continue normally
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String username = jwtHelper.getUsernameFromToken(token);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtHelper.validateToken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (ExpiredJwtException e) {

            logger.warn("JWT token expired");

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Session expired"
            );

            return;

        } catch (Exception e) {

            logger.warn("Invalid JWT token", e);

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid token"
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}