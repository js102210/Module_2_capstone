package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

   //constructors
    private long account_id;
    private long user_id;
    private BigDecimal balance;

//getter/setter account id
    public long getAccount_id() { return account_id; }
    public void setAccount_id(long account_id) { this.account_id = account_id; }

    //getter/setter account id
    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }
    //getter/setter balance
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Account(){


    }


}

