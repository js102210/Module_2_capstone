package com.techelevator.tenmo.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.tomcat.util.json.JSONParser;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class Transfer {



    private Long transferId;
    private Long fromAcctId;
    private Long toAcctId;
    private Long transferStatusId;
    private Long transferTypeId;
    //this property is not represented in the db, but is determined at runtime in relation to the Principal when their personal transaction report is accessed
    //this means it will be null whenever transfer is viewed elsewhere
    private String typeOfTransferToUser;

    public String getTypeOfTransferToUser() {
        return typeOfTransferToUser;
    }

    public void setTypeOfTransferToUser(String typeOfTransferToUser) {
        this.typeOfTransferToUser = typeOfTransferToUser;
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

    public static String toJSONStr(Transfer transfer) {
        String jsonstr = null;
      try{ jsonstr = jsonConverter.writeValueAsString(transfer);
      } catch (JsonProcessingException e){
          System.out.println("ope");
      }
        return jsonstr;
    }





}
