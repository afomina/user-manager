package andrianova.usermanager.auth;

import andrianova.usermanager.config.SecurityProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Parses jwt token from request Authorization header and sets auth context
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(SecurityProperties securityProperties,
                         UserDetailsService userDetailsService) {
        this.securityProperties = securityProperties;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = JWT.require(Algorithm.HMAC512(securityProperties.getSigningKey()))
                .build()
                .verify(header)
                .getSubject();
        if (username == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ? Collections.emptyList() : userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
