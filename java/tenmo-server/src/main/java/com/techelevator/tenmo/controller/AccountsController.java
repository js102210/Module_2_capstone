package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.security.Principal;


@RestController
@PreAuthorize("isAuthenticated()")
public class AccountsController {
    private AccountDAO accountDAO;
    private UserDAO userDAO;



public AccountsController(AccountDAO accountDAO, UserDAO userDAO){

    this.accountDAO = accountDAO;
    this.userDAO= userDAO;
}

//



//get all accounts for a user
    @RequestMapping(path= "/users/totalbalance", method = RequestMethod.GET)
    public BigDecimal getTotalBalanceForUser(Principal p){
    System.out.println(p.getName() + " requested their total balance: ");
    return accountDAO.findBalanceForUser(p.getName());
    }

    public String currentUserName(Principal principal){
        return principal.getName();
    }

    // make a send money transfer
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/user/sendmoney/{toUserId}/{amtToTransfer}", method = RequestMethod.PUT)
    public boolean sendMoney(Principal p, @PathVariable() Long toUserId , @PathVariable BigDecimal amtToTransfer){
        Transfer t = null;
    try {
        Long id = getCurrentUserId( p);
        t=  accountDAO.sendMoney(id, toUserId, amtToTransfer);
    }catch(RestClientResponseException e){
        return false;
    }
    return t != null;
    }
    //get user ID method
    private Long getCurrentUserId(Principal principal) {
        return userDAO.findByUsername(principal.getName()).getId();
    }
}

//
//    private AccountDAO accountDAO;
//    private UserDAO userDAO;
//    private transferDAO transferDAO;
//
//public AccountsController(AccountDAO, accountDAO, UserDAO userDAO, TransferDAO transferDAO) {
//    this.accountDAO = accountDAO;
//    this.userDAO = userDAO;
//    this.transferDAO = transferDAO;
//}
//@RequestMapping(value = "/balance", method = RequestMethod.GET)
//    public BigDecimal getBalance(Principal principal) throws UsernameNotFoundException {
//    Long userId = getCurrentUserId(principal);
//    return accountDAO.getAccountByUserId(userId).getBalance();
//}
//
//
//private Long getCurrentUserId(Principal principal){return userDAO.findByUserName(Principal.getName().getId());
//
//}
//}
//
//
