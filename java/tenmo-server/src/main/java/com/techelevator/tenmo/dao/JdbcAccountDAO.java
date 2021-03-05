package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class  JdbcAccountDAO implements AccountDAO {
    private JdbcTemplate jdbcTemplate;
    public JdbcAccountDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public BigDecimal findBalanceForUser(String userName) {
        String sql = "SELECT SUM(balance) FROM accounts\n" +
                "WHERE user_id = (SELECT user_id FROM users WHERE username = ?);";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userName);
        BigDecimal result = null;
        if(results.next()){
            result = results.getBigDecimal("sum");
        }
        return result;
    }



    //method to get single account balance for purpose of transfer
    @Override
    public BigDecimal getSingleAccountBalance(Long accountId){
        String sql = "SELECT balance FROM accounts WHERE account_id = ?;";
        BigDecimal balance = null;
        SqlRowSet result= jdbcTemplate.queryForRowSet(sql, accountId);
        if(result.next()){
            balance= result.getBigDecimal("balance");
        }
        return balance;
    }



    @Override
    public Long getIdOfBiggestAcctForUser(Long userId) {
        String sql = "SELECT MAX(balance), account_id FROM accounts WHERE user_id = ? GROUP BY accounts.account_id ORDER BY MAX(balance) DESC LIMIT 1";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        Long id = null;
        if (result.next()){
            id = result.getLong("account_id");
        }
        return id;
    }


    private Account mapRowToAccount(SqlRowSet result){
       Account a = new Account();
       a.setAccount_id(result.getLong("account_id"));
       a.setUser_id(result.getLong("user_id"));
       a.setBalance(result.getBigDecimal("balance"));

       return a;
    }

    private Transfer mapRowToTransfer(SqlRowSet result){
        Transfer t = new Transfer();
        t.setTransferId(result.getLong("transfer_id"));
        t.setTransferTypeId(result.getLong("transfer_type_id"));
        t.setTransferStatusId(result.getLong("transfer_status_id"));
        t.setFromAcctId(result.getLong("account_from"));
        t.setToAcctId(result.getLong("account_to"));
        t.setAmtOfTransfer(result.getBigDecimal("amount"));
        return t;
    }
}
