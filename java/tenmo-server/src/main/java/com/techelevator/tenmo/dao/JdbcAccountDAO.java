package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean validateTransfer(Long fromUserId, Long toUserId, BigDecimal amtToTransfer) {
        //find the biggest acct balance under fromUser's id
        String sql = "SELECT MAX(balance) FROM accounts\n" +
                "WHERE user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, fromUserId);
        //if there isn't an acct for that user id, startingbalance should stay null
        BigDecimal startingBalance = null;
        if (result.next()){
            startingBalance = result.getBigDecimal("max");
        }
        //make sure the user receiving the transfer has an account
        String sql2 = "SELECT * FROM accounts WHERE user_id = ?;";
        boolean toUserHasAcct = false;
        if (jdbcTemplate.queryForRowSet(sql, toUserId).next()){
            toUserHasAcct = true;
        }
        //if to user has acct, starting balance isn't null (fromUser has acct), and fromUser has one acct with enough money
        //to cover the transfer return true, otherwise false
        if (startingBalance != null && toUserHasAcct) {
            return startingBalance.compareTo(amtToTransfer) >= 0;
        }
        else {
            return false;
        }
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

    //normally we would split this into different methods, but the fact that this needs to be as close to one 'transaction'
    //as possible inclines me to do it all in one method so it either succeeds or fails as a whole

    //thinking about making this take acct ids instead
    public Transfer sendMoney(Long fromUserId, Long toUserId, BigDecimal amtToTransfer) {
        //TODO consider creating a custom exception for this to throw
       boolean isAllowed = validateTransfer(fromUserId, toUserId, amtToTransfer);
       if (isAllowed){
           //determine which account to draw from
           Long fromAcctId = getIdOfBiggestAcctForUser(fromUserId);
           Long toAcctId = getIdOfBiggestAcctForUser(toUserId);
           //find the balance of the two seperate accounts and calculate balance post transfer
           BigDecimal balanceOfFromAccount = getSingleAccountBalance(fromAcctId);
           BigDecimal balanceOfToAccount = getSingleAccountBalance(toAcctId);
           BigDecimal updatedBalanceOfFromAccount = balanceOfFromAccount.subtract(amtToTransfer);
           BigDecimal updatedBalanceOfToAccount = balanceOfToAccount.add(amtToTransfer);
           //reduce the amt of fromUser balance TODO add select statement to get balance
           String sql = "UPDATE accounts " +
                   "SET balance = ? " + "WHERE account_id = ?; " ;
           jdbcTemplate.update(sql, updatedBalanceOfFromAccount, fromAcctId);
           //increase the amt of toUser balance
           String sql2 = "UPDATE accounts " +
                   "SET balance = ? " +
                   "WHERE account_id = ?; ";
           jdbcTemplate.update(sql2, updatedBalanceOfToAccount, toAcctId);
           //log the transfer in transfer table as a Send with status Approved
           String sql3 = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                   "VALUES (2, 2, ?, ?, ?) RETURNING transfer_id;";
           Long newId = jdbcTemplate.queryForObject(sql3, Long.class, fromAcctId, toAcctId, amtToTransfer);
           //instantiate new Transfer object in memory
           Transfer transfer = new Transfer();
           transfer.setAmtOfTransfer(amtToTransfer);
           transfer.setTransferTypeId(2L);
           transfer.setTransferStatusId(2L);
           transfer.setFromAcctId(fromAcctId);
           transfer.setToAcctId(toAcctId);
           transfer.setTransferId(newId);
           return transfer;
       } else {
           return  null;
       }
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
}
