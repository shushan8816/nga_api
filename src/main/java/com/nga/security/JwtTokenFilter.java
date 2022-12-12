package com.nga.security;

import com.nga.utils.exceptions.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String jwtToken = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);

        try {
            if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {

                Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);

                if(authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtAuthenticationException e) {

            log.error("Failed to set user authentication in security context: ", e);
            SecurityContextHolder.clearContext();
            servletRequest.setAttribute("expired", e.getMessage());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
