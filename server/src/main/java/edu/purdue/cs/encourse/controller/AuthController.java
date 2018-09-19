package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.impl.AccountServiceImpl;
import edu.purdue.cs.encourse.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
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
            Account account = new ObjectMapper().readValue(json, Account.class);
            result = adminService.addAccount(account);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccounts() {
        return new ResponseEntity<>(accountService.retrieveAllAccounts(), HttpStatus.FOUND);
    }

}
