package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;


@RestController
@PreAuthorize("isAuthenticated()")
public class AccountsController {
    private AccountDAO dao;

public AccountsController(AccountDAO dao){

    this.dao=dao;
}

//get all accounts for a user
    @RequestMapping(path= "/users/account", method = RequestMethod.GET)
    public List<Account> getAllAccountsForUser(Principal p){
    System.out.println(p.getName()+ " requested all current active accounts");
   return dao.findAllForUser(p.toString());
//return null;
    }


}


