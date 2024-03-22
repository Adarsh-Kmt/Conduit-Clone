package kamathadarsh.Conduit.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kamathadarsh.Conduit.security.securityDTO.CustomUserDetails;
import kamathadarsh.Conduit.security.service.CustomUserDetailsService;
import kamathadarsh.Conduit.security.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor

public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{

            String jwtToken = getJWTToken(request);

            if(jwtToken != null && jwtUtils.validateJWTToken(jwtToken)){

                String username = jwtUtils.getUsernameFromJWTToken(jwtToken);

                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

        }
        catch(Exception e){

            System.out.println("error in jwt authentication");
        }

        filterChain.doFilter(request, response);
    }

    public String getJWTToken(HttpServletRequest request){

        String authorizationHeader = request.getHeader("Authorization");

        if(!authorizationHeader.startsWith("Bearer ")) return null;

        return authorizationHeader.substring(7, authorizationHeader.length());

    }
}
