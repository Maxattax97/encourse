package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.impl.AccountServiceImpl;
import edu.purdue.cs.encourse.service.impl.AdminServiceImpl;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/secured")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private SessionRegistry sessionRegistry;


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/modify/account", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifyAccount(@RequestParam(name = "field") String field, @RequestParam(name = "value") String value) {
        User u = getUserFromAuth();
        int result = adminService.modifyAccount(u.getUsername(), field, value);
        HttpStatus status = (result == 0)? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        String response = (result == 0)? "Account " + u.getUsername() + " modified successfully": "Account not modified";
        return new ResponseEntity<>("{ \"result\": " + result + ",\"response\": " + response + " }", status);
    }

    // TODO: Adjust how frontend calls this endpoint
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/create/account", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<?> createAccount(@RequestParam(name = "password") String password, @RequestBody String json) {

        int result;
        User user;
        try {
            Account a = new ObjectMapper().readValue(json, Account.class);
            String userType;
            String accountType;
            switch (a.getRole()) {
                case Account.Roles.ADMIN:
                    userType = "ADMIN";
                    accountType = "Admin";
                    break;
                case Account.Roles.PROFESSOR:
                    userType = "PROFESSOR";
                    accountType = "Professor";
                    break;
                case Account.Roles.TA:
                    userType = "TA";
                    accountType = "TA";
                    break;
                case Account.Roles.STUDENT:
                default:
                    userType = "STUDENT";
                    accountType = "Student";
            }
            result = adminService.addAccount(a.getUserID(), a.getUserName(), a.getFirstName(), a.getLastName(), accountType, a.getMiddleInit(), a.getEduEmail());
            user = adminService.addUser(a.getUserName(), password, userType, false, false, false, true);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>((user != null)? user: result, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccounts() {
        return new ResponseEntity<>(accountService.retrieveAllAccounts(), HttpStatus.FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccount() {
        Account a = getAccountFromAuth();
        List<Account> accounts = new ArrayList<>();
        accounts.add(a);
        return new ResponseEntity<>(accounts, HttpStatus.FOUND);
    }

    @PreAuthorize("isAuthenticated()")
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

    private Account getAccountFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = ((User)securityContext.getAuthentication().getPrincipal());
        return accountService.retrieveAccount(user.getUsername());
    }

    private User getUserFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return ((User)securityContext.getAuthentication().getPrincipal());
    }

    private List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(Object::toString)
                .collect(Collectors.toList());
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
