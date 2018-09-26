package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.impl.AccountServiceImpl;
import edu.purdue.cs.encourse.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping(value="/secured")
public class AuthController {

    private final AccountServiceImpl accountService;

    private final AdminServiceImpl adminService;

    @Autowired
    public AuthController(AccountServiceImpl accountService, AdminServiceImpl adminService) {
        this.accountService = accountService;
        this.adminService = adminService;
    }

    @RequestMapping(value = "/create/account", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<?> createAccount(@RequestBody String json) {

        int result;
        System.out.println(json);
        try {
            Account a = new ObjectMapper().readValue(json, Account.class);
            result = adminService.addAccount(a.getUserID(), a.getUserName(), a.getSaltPass(), a.getFirstName(), a.getLastName(), " ", a.getMiddleInit(), a.getEduEmail());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccounts() {
        return new ResponseEntity<>(accountService.retrieveAllAccounts(), HttpStatus.FOUND);
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccount() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = ((User)securityContext.getAuthentication().getPrincipal());
        Account a = accountService.retrieveAccount(user.getUsername(), user.getPassword());
        return new ResponseEntity<>(a, HttpStatus.FOUND);
    }

}
