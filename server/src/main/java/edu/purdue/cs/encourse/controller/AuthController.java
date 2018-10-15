package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.EmailService;
import edu.purdue.cs.encourse.service.impl.AccountServiceImpl;
import edu.purdue.cs.encourse.service.impl.AdminServiceImpl;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private EmailService emailService;



    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/modify/account", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifyAccount(@RequestParam(name = "userName") String userName,
                                                         @RequestParam(name = "field") String field,
                                                         @RequestParam(name = "value") String value) {
        //User u = getUserFromAuth();
        int result = adminService.modifyAccount(userName, field, value);
        HttpStatus status = (result == 0)? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        return new ResponseEntity<>(result, status);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/modify/password", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifyAccount(@RequestParam(name = "password") String password) {
        userDetailsService.updatePassword(getUserFromAuth(), password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/accounts", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<?> createAccountsBulk(@RequestBody String body) {
        String curr = null;
        List<String> users = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(body);
            ListIterator iter = json.listIterator();

            while(iter.hasNext()) {
                JSONObject obj = (JSONObject) iter.next();
                curr = obj.toJSONString();
                ResponseEntity<?> response = createAccount(null, curr);
                if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    throw new ParseException(0);
                }
                users.add(response.getBody().toString());
            }
        } catch (ParseException e) {
            return new ResponseEntity<>("Could not parse the following index in array: \n" + curr, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/account", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<?> createAccount(@RequestParam(name = "password", required = false) String password, @RequestBody String json) {

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
            String genPassword = emailService.sendGeneratedPasswordMessage(a);
            if (password == null || password.isEmpty()) {
                password = genPassword;
            }
            user = adminService.addUser(a.getUserName(), password, userType, false, false, false, true);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>((user != null)? user: result, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccounts() {
        List<Account> accounts = accountService.retrieveAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccount(@RequestParam(name = "userName", required = false) String userName) {
        Account a = getAccountFromAuth();
        boolean flag = true;
        if (userName != null) {
            if (!hasPermissionOverAccount(userName)) {
                flag = false;
            }
        }
        if (flag) {
            List<Account> accounts = new ArrayList<>();
            accounts.add(a);
            return new ResponseEntity<>(accounts, HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
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

    private boolean hasPermissionOverAccount(String userName) {
        return adminService.hasPermissionOverAccount(getUserFromAuth(), userName);
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
