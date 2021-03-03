package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDAO implements AccountDAO{
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Account> findAllForUser(String user) {
        String sql = "SELECT * FROM accounts\n" +
                "        WHERE user_id = (SELECT user_id FROM users WHERE username = ?);";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user);
        List<Account> accountResults = new ArrayList<>();
        while(results.next()){
            Account a = mapRowToAccount(results);
            accountResults.add(a);
        }
        return accountResults;
    }

    private Account mapRowToAccount(SqlRowSet result){
       Account a = new Account();
       a.setAccount_id(result.getLong("account_id"));
       a.setUser_id(result.getLong("user_id"));
       a.setBalance(result.getBigDecimal("balance"));

       return a;
    }
}
