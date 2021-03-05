package com.techelevator.tenmo.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

public class Transfer {
    private Long transferId;
    private Long fromAcctId;
    private Long toAcctId;
    private Long transferStatusId;
    private Long transferTypeId;
    //this property is not represented in the db, but is determined at runtime in relation to the Principal when their personal transaction report is accessed
    //this means it will be null whenever transfer is viewed elsewhere

    private String transferStatusName;
    private String transferTypeName;
    private String fromUserName;
    private String toUserName;

    public String getTransferStatusName() {
        return transferStatusName;
    }

    public void setTransferStatusName(String transferStatusName) {
        this.transferStatusName = transferStatusName;
    }

    public String getTransferTypeName() {
        return transferTypeName;
    }

    public void setTransferTypeName(String transferTypeName) {
        this.transferTypeName = transferTypeName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }


    public static ObjectMapper jsonConverter = new ObjectMapper();

    public BigDecimal getAmtOfTransfer() {
        return amtOfTransfer;

    }

    public void setAmtOfTransfer(BigDecimal amtOfTransfer) {
        this.amtOfTransfer = amtOfTransfer;
    }

    public Long getFromAcctId() {
        return fromAcctId;
    }

    public void setFromAcctId(Long fromAcctId) {
        this.fromAcctId = fromAcctId;
    }

    public Long getToAcctId() {
        return toAcctId;
    }

    public void setToAcctId(Long toAcctId) {
        this.toAcctId = toAcctId;
    }

    public Long getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(Long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public Long getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(Long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public Transfer() {
    }


    private BigDecimal amtOfTransfer;

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

}
