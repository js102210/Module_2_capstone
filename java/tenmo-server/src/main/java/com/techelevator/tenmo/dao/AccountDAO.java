package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;


    public interface AccountDAO {

        BigDecimal findBalanceForUser(String userName);
        boolean validateTransfer(Long fromId, Long toId, BigDecimal amtToTransfer);
        Transfer sendMoney (User fromUser, User toUser, BigDecimal amtToTransfer);
        Long getIdOfBiggestAcctForUser(User user);

    }







//
////
////        AccountDAO findByAccount(String account);
////
////        int findIdByUsername(String username);
////
////        boolean create(String username, String password);