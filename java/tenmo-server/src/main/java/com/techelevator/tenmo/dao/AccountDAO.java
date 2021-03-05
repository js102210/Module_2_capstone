package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import netscape.javascript.JSObject;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;


public interface AccountDAO {

        BigDecimal findBalanceForUser(String userName);
        Long getIdOfBiggestAcctForUser(Long userId);
        BigDecimal getSingleAccountBalance(Long userId);
    }

