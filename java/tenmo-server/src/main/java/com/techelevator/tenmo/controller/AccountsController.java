package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;


@RestController
@PreAuthorize("isAuthenticated()")
public class AccountsController {
    private AccountDAO dao;

public AccountsController(AccountDAO dao){

    this.dao = dao;
}


//get all accounts for a user
    @RequestMapping(path= "/users/totalbalance", method = RequestMethod.GET)
    public BigDecimal getTotalBalanceForUser(Principal p){
    System.out.println(p.getName() + " requested their total balance: ");
    return dao.findBalanceForUser(p.getName());
    }

    public String currentUserName(Principal principal){
        return principal.getName();
    }

    // make a send money transfer
  /*  @RequestMapping(path = "/user/sendmoney/{id}/{balance}", method = RequestMethod.PUT)
    public boolean sendMoney(Principal p, @PathVariable Long toUserId, @PathVariable BigDecimal amtToTransfer){
    dao.sendMoney(p.getName(), )
    } */


}


