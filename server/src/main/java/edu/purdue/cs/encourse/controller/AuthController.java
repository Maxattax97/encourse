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
import java.util.*;
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
    @RequestMapping(value = "/modify/account", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<?> modifyAccount(@RequestParam(name = "userName") String userName,
                                                         @RequestBody String body) {
        List<String> errors = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(body);
            Iterator<Object> iter = json.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String val = (String) json.get(key);
                int result;
                if (key.contentEquals("role")) {
                    result = adminService.modifyAuthority(userName, val);
                } else {
                    result = adminService.modifyAccount(userName, key, val);
                }

                if (result != 0) {
                    errors.add("Error modifying field " + key + " with value " + val);
                }
            }
        } catch (ParseException e) {

        }
        if (errors.isEmpty()) {
            Account account = accountService.retrieveAccount(userName);
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>(errors, HttpStatus.NOT_MODIFIED);
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

        Account account;
        try {
            Account a = new ObjectMapper().readValue(json, Account.class);
            String type;
            switch (a.getRole()) {
                case Account.Roles.ADMIN:
                    type = Account.Role_Names.ADMIN;
                    break;
                case Account.Roles.PROFESSOR:
                    type = Account.Role_Names.PROFESSOR;
                    break;
                case Account.Roles.TA:
                    type = Account.Role_Names.TA;
                    break;
                case Account.Roles.STUDENT:
                default:
                    type = Account.Role_Names.STUDENT;
            }
            adminService.addAccount(a.getUserID(), a.getUserName(), a.getFirstName(), a.getLastName(), type, a.getMiddleInit(), a.getEduEmail());
            String genPassword = emailService.sendGeneratedPasswordMessage(a);
            if (password == null || password.isEmpty()) {
                password = genPassword;
            }
            adminService.addUser(a.getUserName(), password, type, false, false, false, true);
            account = accountService.retrieveAccount(a.getUserName());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccounts(@RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                       @RequestParam(name = "size", defaultValue = "10", required = false) int size,
                                                       @RequestParam(name = "sortBy", defaultValue = "userName", required = false) String sortBy) {
        List<Account> accounts = accountService.retrieveAllAccounts();
        switch (sortBy) {
            case "userName":
                accounts.sort(Comparator.comparing(Account::getUserName));
                break;
        }

        List<Account> sortedAndPagedJsonArray = new ArrayList<>();
        for (int i = (page - 1) * size; i < accounts.size(); i++) {
            if (i >= page * size) {
                break;
            }
            sortedAndPagedJsonArray.add(accounts.get(i));
        }

        JSONObject response = new JSONObject();
        response.put("content", sortedAndPagedJsonArray);
        response.put("totalPages", accounts.size() / size + 1);
        response.put("page", page);
        response.put("totalSize", accounts.size());
        response.put("size", size);
        response.put("elements", sortedAndPagedJsonArray.size());
        response.put("sortedBy", sortBy);
        response.put("last", (page >= accounts.size() / size));
        response.put("first", (page == 1));

        return new ResponseEntity<>(response, HttpStatus.OK);
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
