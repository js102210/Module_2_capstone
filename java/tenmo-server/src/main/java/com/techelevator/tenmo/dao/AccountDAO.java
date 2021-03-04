package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;


public interface AccountDAO {

        BigDecimal findBalanceForUser(String userName);
        boolean validateTransfer(Long fromId, Long toId, BigDecimal amtToTransfer);
        Transfer sendMoney (Long fromUserId, Long toUserId, BigDecimal amtToTransfer);
        Long getIdOfBiggestAcctForUser(Long userId);
        BigDecimal getSingleAccountBalance(Long userId);
        Map<Transfer, String> getTransactionsForUser(Long userId);

    }







//
////
////        AccountDAO findByAccount(String account);
////
////        int findIdByUsername(String username);
////
////        boolean create(String username, String password);