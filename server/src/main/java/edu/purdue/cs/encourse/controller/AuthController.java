package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.EmailService;
import edu.purdue.cs.encourse.service.impl.UserDetailsServiceImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.InvalidRelationIdException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value="/api")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private EmailService emailService;
    

    /**
     * Retrieves Account of current logged in User
     *
     * @param  userName (not required : defaults to current User) userName of Account to return
     * @return      account
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccount(@RequestParam(name = "userName", required = false) String userName) {
        Account a = null;
        try {
            a = getAccountFromAuth();
        }
        catch (InvalidRelationIdException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        boolean flag = true;
        if (flag) {
            List<Account> accounts = new ArrayList<>();
            accounts.add(a);
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Logs the current logged in User out
     *
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }
        User user = getUserFromAuth();
        int result = logout(user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Account getAccountFromAuth() throws InvalidRelationIdException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = ((User)securityContext.getAuthentication().getPrincipal());
        return accountService.getAccount(user.getId());
    }

    private User getUserFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return ((User)securityContext.getAuthentication().getPrincipal());
    }

    private int logout(User user) {
        List<Object> objs = sessionRegistry.getAllPrincipals();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < objs.size(); i++) {
            users.add((User)objs.get(i));
        }
        if (users.contains(user)) {
            sessionRegistry.getAllPrincipals().remove(users.indexOf(user));
            return 0;
        }
        return -1;
    }
}
