package edu.purdue.cs.encourse.controller;

import edu.purdue.cs.encourse.auth.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.model.AccountModel;
import edu.purdue.cs.encourse.model.ChangePasswordModel;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminServiceV2;
import edu.purdue.cs.encourse.service.EmailService;
import edu.purdue.cs.encourse.service.impl.UserDetailsServiceImpl;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.InvalidRelationIdException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value="/api")
public class AuthController {

    private final AccountService accountService;
    
    private final AdminServiceV2 adminService;

    private final TokenStore tokenStore;

    private final Sender sender;

    private final Authenticator authenticator;

    private final SessionRegistry sessionRegistry;
    
    private final UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    public AuthController(AccountService accountService, TokenStore tokenStore, SessionRegistry sessionRegistry, AdminServiceV2 adminService, UserDetailsServiceImpl userDetailsService) {
        this.accountService = accountService;
        this.tokenStore = tokenStore;
        this.sender = sender;
        this.authenticator = authenticator;
        this.sessionRegistry = sessionRegistry;
        this.adminService = adminService;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> logIn(@RequestParam("email") String email) {
        // TODO: verify that the user is in the database

        // send sign-in email
        String token = tokenStore.create(email);
        sender.send(email, token);

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * Retrieves Account of current logged in User
     *
     * @return      account
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/account",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccount() {
        try {
            return new ResponseEntity<>(new AccountModel(accountService.getAccount(adminService.getUser().getId())), HttpStatus.OK);
        }
        catch (InvalidRelationIdException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    /**
     * Logs the current logged in User out
     *
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String tokenValue = authHeader.substring(authHeader.indexOf(' ')).trim();
            OAuth2AccessToken accessToken = tokenStore.remove(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }
        User user = adminService.getUser();
        int result = logout(user);
        return new ResponseEntity<>(result, HttpStatus.OK);
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
