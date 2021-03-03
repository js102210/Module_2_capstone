package com.techelevator.tenmo.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class Transfer {



    private Long transferId;
    private Long fromAcctId;
    private Long toAcctId;
    private Long transferStatusId;
    private Long transferTypeId;

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
