package edu.purdue.cs.encourse.auth;

import java.security.Principal;

import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.model.AccountModel;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminServiceV2;
import edu.purdue.cs.encourse.service.impl.AccountServiceImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.management.relation.InvalidRelationIdException;

/**
 * @author Arik Cohen
 * @since Jan 30, 2018
 */
public class SpringSecurityAuthenticator implements Authenticator {

    private final TokenStore tokenStore;
    private AccountService accountService;
    private AdminServiceV2 adminService;

    public SpringSecurityAuthenticator(TokenStore aTokenStore) {
        tokenStore = aTokenStore;
    }

    @Override
    public Principal authenticate (String userId, String aToken) {
        String token = tokenStore.get(userId);
        if(aToken.equals(token)) {
            try {
                Account account = accountService.getAccount(adminService.getUser().getId());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null,AuthorityUtils.createAuthorityList(account.getRole().name()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return authentication;
            } catch (InvalidRelationIdException e) {
                e.printStackTrace();
            }
        }
        throw new BadCredentialsException("Invalid auth token for user: " + userId);
    }

}