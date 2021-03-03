package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.util.List;


    public interface AccountDAO {

        List<Account> findAllForUser(String user);

    }







//
////
////        AccountDAO findByAccount(String account);
////
////        int findIdByUsername(String username);
////
////        boolean create(String username, String password);