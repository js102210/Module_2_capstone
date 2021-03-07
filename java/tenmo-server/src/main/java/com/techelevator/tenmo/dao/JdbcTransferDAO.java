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
    public void acceptOrRejectPendingRequest(Long fromUserID, Long toUserId, BigDecimal amtToTransfer, Long transferId, boolean isAccepted) {
        boolean isAllowed = validateTransfer(fromUserID, toUserId, amtToTransfer);
        if(isAllowed && isAccepted){
            Long fromAcctId = getIdOfBiggestAcctForUser(fromUserID);
            Long toAcctId = getIdOfBiggestAcctForUser(toUserId);
            //
            BigDecimal balanceOfFromAccount = accountDAO.getSingleAccountBalance(fromAcctId);
            BigDecimal balanceOfToAccount = accountDAO.getSingleAccountBalance(toAcctId);
            BigDecimal updatedBalanceOfFromAccount = balanceOfFromAccount.subtract(amtToTransfer);
            BigDecimal updatedBalanceOfToAccount = balanceOfToAccount.add(amtToTransfer);
            //
            String sql = "UPDATE accounts " +
                    "SET balance = ? " + "WHERE account_id = ?; " ;
            jdbcTemplate.update(sql, updatedBalanceOfFromAccount, fromAcctId);
            //increase the amt of toUser balance
            String sql2 = "UPDATE accounts " +
                    "SET balance = ? " +
                    "WHERE account_id = ?; ";
            jdbcTemplate.update(sql2, updatedBalanceOfToAccount, toAcctId);
            // Set status to approved if approved
            String sql3= "UPDATE transfers\n" +
                    "SET transfer_status_id = 2\n" +
                    "WHERE transfer_id = ?; ";
            jdbcTemplate.update(sql3, transferId);
        } else{
            String sql= "UPDATE transfers\n" +
                    "SET transfer_status_id = 3\n" +
                    "WHERE transfer_id = ?; ";
        }
    }

    @Override
    public Transfer requestMoney(Long fromUserId, Long toUserId, BigDecimal amtToTransfer) {
        //determine which accounts would be drawn from so we can log the pending transfer
        Long fromAcctId = getIdOfBiggestAcctForUser(fromUserId);
        Long toAcctId = getIdOfBiggestAcctForUser(toUserId);
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (1, 1, ?, ?, ?) RETURNING transfer_id;";
        //log the transfer as pending, do not adjust balances
        Long newId = jdbcTemplate.queryForObject(sql, Long.class, fromAcctId, toAcctId, amtToTransfer);
        Transfer transfer = new Transfer();
        transfer.setAmtOfTransfer(amtToTransfer);
        transfer.setTransferTypeId(1L);
        transfer.setTransferStatusId(1L);
        transfer.setFromAcctId(fromAcctId);
        transfer.setToAcctId(toAcctId);
        transfer.setTransferId(newId);
        return transfer;
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
        String sql = "SELECT DISTINCT transfers.transfer_id, amount, transfer_statuses.transfer_status_desc, transfer_types.transfer_type_desc, " +
                "transfers.account_from, transfers.account_to, transfers.transfer_status_id, transfers.transfer_type_id,\n" +
                "(SELECT username FROM users WHERE users.user_id = (SELECT user_id FROM accounts  WHERE users.user_id = accounts.user_id AND accounts.account_id = transfers.account_from)) AS from_username,\n" +
                "(SELECT username FROM users WHERE users.user_id = (SELECT user_id FROM accounts  WHERE users.user_id = accounts.user_id AND accounts.account_id = transfers.account_to)) AS to_username\n" +
                "FROM transfers\n" +
                "INNER JOIN accounts ON accounts.account_id = transfers.account_to OR accounts.account_id = transfers.account_from\n" +
                "INNER JOIN users ON accounts.user_id = users.user_id\n" +
                "INNER JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id\n" +
                "INNER JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id\n" +
                "WHERE users.user_id = ?";
        SqlRowSet transfersFrom = jdbcTemplate.queryForRowSet(sql, userId);
        //store each sent transfer in results in list
        while (transfersFrom.next()){
            Transfer t = mapRowToTransfer(transfersFrom);
            transactionsReturned.add(t);
        }
        return transactionsReturned;
    }

    @Override
    public Transfer getDetailsForTransfer(Long transferId) {
        String sql = "SELECT DISTINCT transfers.transfer_id, amount, transfer_statuses.transfer_status_desc, transfer_types.transfer_type_desc, transfers.account_from, transfers.account_to, transfers.transfer_status_id, transfers.transfer_type_id,\n" +
                "(SELECT username FROM users WHERE users.user_id = (SELECT user_id FROM accounts  WHERE users.user_id = accounts.user_id AND accounts.account_id = transfers.account_from)) AS from_username,\n" +
                "(SELECT username FROM users WHERE users.user_id = (SELECT user_id FROM accounts  WHERE users.user_id = accounts.user_id AND accounts.account_id = transfers.account_to)) AS to_username\n" +
                "FROM transfers\n" +
                "INNER JOIN accounts ON accounts.account_id = transfers.account_to OR accounts.account_id = transfers.account_from\n" +
                "INNER JOIN users ON accounts.user_id = users.user_id\n" +
                "INNER JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id\n" +
                "INNER JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id\n" +
                "WHERE transfers.transfer_id = ?;";
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
        t.setTransferStatusName(result.getString("transfer_status_desc"));
        t.setTransferTypeName(result.getString("transfer_type_desc"));
        t.setFromUserName(result.getString("from_username"));
        t.setToUserName(result.getString("to_username"));
        return t;
    }





}
