package edu.purdue.cs.encourse.controller;
import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.auth.Authenticator;
import edu.purdue.cs.encourse.auth.Sender;
import edu.purdue.cs.encourse.auth.TokenStore;
import edu.purdue.cs.encourse.domain.Account;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
  public String signin () {
    return "signin";
  }
  
  @PostMapping("/signin")
  public String signin (@RequestParam("username") String aUsername) {
    
    // verify that the user is in the database.
    String email = verifyUser(aUsername);

    // send sign-in email
    String token = tokenStore.create(aUsername);
    sender.send(email, token);
    
    return "login_link_sent";
  }
  
  @GetMapping("/signin/{token}")
  public String signin (@RequestParam("uid") String aUid, @PathVariable("token") String aToken) {
    try {
      authenticator.authenticate(aUid, aToken);
      return "redirect:/";
    }
    catch (BadCredentialsException aBadCredentialsException) {
      return "invalid_login_link";
    }
  }

  private String verifyUser(String username) {
    List<Account> accounts = accountRepository.findAll();
    for(Account account : accounts) {
      System.out.println("Account Username: " + account.getUsername());
      if(account.getUsername().equals(username)) {
        return account.getEduEmail();
      }
    }

    return null;
  }
}
