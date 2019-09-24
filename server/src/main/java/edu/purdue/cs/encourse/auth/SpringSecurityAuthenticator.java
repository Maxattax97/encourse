package edu.purdue.cs.encourse.auth;

import java.security.Principal;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Arik Cohen
 * @since Jan 30, 2018
 */
public class SpringSecurityAuthenticator implements Authenticator {

    private final TokenStore tokenStore;

    public SpringSecurityAuthenticator(TokenStore aTokenStore) {
        tokenStore = aTokenStore;
    }

    @Override
    public Principal authenticate (String userId, String aToken) {
        String token = tokenStore.get(userId);
        if(aToken.equals(token)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null,AuthorityUtils.createAuthorityList("ROLE_USER"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        }
        throw new BadCredentialsException("Invalid auth token for user: " + userId);
    }

}