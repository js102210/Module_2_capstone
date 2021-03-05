package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO{
    private JdbcTemplate jdbcTemplate;
    private AccountDAO accountDAO;

    //note not sure this will work or if it's good practice, instantiating a DAO within another DAO, also, passing in the
    //interface for the constructor-- it has @component but this isn't a controller, so idk if it will know to pass in the right object
    //the alternative is to have the getSingleAccountBalance in the TransferDAO but that just seems wrong
    public JdbcTransferDAO(JdbcTemplate jdbcTemplate, AccountDAO accountDAO){
        this.jdbcTemplate = jdbcTemplate;
        this.accountDAO = accountDAO;
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

    @Override
    public Transfer sendMoney(Long fromUserId, Long toUserId, BigDecimal amtToTransfer) {
        //TODO consider creating a custom exception for this to throw
        boolean isAllowed = validateTransfer(fromUserId, toUserId, amtToTransfer);
        if (isAllowed){
            //determine which account to draw from
            Long fromAcctId = getIdOfBiggestAcctForUser(fromUserId);
            Long toAcctId = getIdOfBiggestAcctForUser(toUserId);
            //find the balance of the two seperate accounts and calculate balance post transfer
            BigDecimal balanceOfFromAccount = accountDAO.getSingleAccountBalance(fromAcctId);
            BigDecimal balanceOfToAccount = accountDAO.getSingleAccountBalance(toAcctId);
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

    @Override
    public List<Transfer> getTransfersForUser(Long userId) {
        List<Transfer> transactionsReturned = new ArrayList<>();
        //query for all transactions where the principal has sent money
        String sql = "SELECT *, 'Sent' as typeOfTransferToUser\n" +
                "FROM transfers\n" +
                "INNER JOIN accounts ON accounts.account_id = transfers.account_from\n" +
                "INNER JOIN users ON accounts.user_id = users.user_id\n" +
                "WHERE users.user_id = ?";
        SqlRowSet transfersFrom = jdbcTemplate.queryForRowSet(sql, userId);
        //store each sent transfer in results map with value of string "Sent"
        while (transfersFrom.next()){
            Transfer t = mapRowToTransfer(transfersFrom);
            t.setTypeOfTransferToUser("Sent");
            transactionsReturned.add(t);
        }
        //query for all transactions principal received
        String sql2 = "SELECT *, 'Received' as typeOfTransferToUser\n" +
                "FROM transfers\n" +
                "INNER JOIN accounts ON accounts.account_id = transfers.account_to\n" +
                "INNER JOIN users ON accounts.user_id = users.user_id\n" +
                "WHERE users.user_id = ?;";
        SqlRowSet transfersTo = jdbcTemplate.queryForRowSet(sql2, userId);
        //store each sent transfer in results map with value of string "Received"
        while (transfersTo.next()){
            Transfer t = mapRowToTransfer(transfersTo);
            t.setTypeOfTransferToUser("Received");
            transactionsReturned.add(t);
        }
        return transactionsReturned;
    }

    @Override
    public Transfer getDetailsForTransfer(Long transferId) {
        String sql = "SELECT * FROM transfers WHERE transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);
        Transfer t = null;
        if (result.next()){
            t = mapRowToTransfer(result);
        }
        return t;
    }

    private Transfer mapRowToTransfer(SqlRowSet result){
        Transfer t = new Transfer();
        t.setTransferId(result.getLong("transfer_id"));
        t.setTransferTypeId(result.getLong("transfer_type_id"));
        t.setTransferStatusId(result.getLong("transfer_status_id"));
        t.setFromAcctId(result.getLong("account_from"));
        t.setToAcctId(result.getLong("account_to"));
        t.setAmtOfTransfer(result.getBigDecimal("amount"));
       // t.setTypeOfTransferToUser(result.getString("typeOfTransferToUser"));
        return t;
    }



}
