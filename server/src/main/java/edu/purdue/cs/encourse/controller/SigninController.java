package edu.purdue.cs.encourse.controller;
import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.auth.Authenticator;
import edu.purdue.cs.encourse.auth.Sender;
import edu.purdue.cs.encourse.auth.TokenStore;
import edu.purdue.cs.encourse.domain.Account;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class SigninController {
  
  private final TokenStore tokenStore;

  private final AccountRepository accountRepository;
  
  private final Sender sender;
  
  private final Authenticator authenticator;
  
  public SigninController (TokenStore aTokenStore, Sender aSender, Authenticator aAuthenticator, AccountRepository aAccountRepository){
    tokenStore = aTokenStore;
    sender = aSender;
    authenticator = aAuthenticator;
    accountRepository = aAccountRepository;
  }

  @GetMapping("/signin")
  public ResponseEntity<?> signin ()
  {
    JSONObject json = new JSONObject();
    json.put("signin", 1);
    return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
  }
  
  @PostMapping(value = "/signin", produces = "application/json")
  public ResponseEntity<?> signin (@RequestParam("username") String aUsername) {

    // verify that the user is in the database.
    String email = verifyUser(aUsername);

    if (email == null) {
        // Verification failed
        System.out.println("Invalid User");

        JSONObject json = new JSONObject();
        json.put("signin", 0);
        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }

    // send sign-in email
    String token = tokenStore.create(aUsername);
    sender.send(aUsername, email, token);

    JSONObject json = new JSONObject();
    json.put("signin", 2);
    return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
  }
  
  @GetMapping("/signin/{token}")
  public ResponseEntity<?> signin (@RequestParam("uid") String aUid, @PathVariable("token") String aToken) {
    try {
      System.out.println(aUid + " " + aToken);
      authenticator.authenticate(aUid, aToken);
      JSONObject json = new JSONObject();
      json.put("signin", 3);
      return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }
    catch (BadCredentialsException aBadCredentialsException) {
      JSONObject json = new JSONObject();
      json.put("signin", 0);
      return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }
  }

  private String verifyUser(String username) {
    List<Account> accounts = accountRepository.findAll();
    for(Account account : accounts) {
      if(account.getUsername().equals(username)) {
        return account.getEduEmail();
      }
    }

    return null;
  }
}
