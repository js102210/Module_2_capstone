package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    boolean validateTransfer(Long fromId, Long toId, BigDecimal amtToTransfer);
    Transfer sendMoney (Long fromUserId, Long toUserId, BigDecimal amtToTransfer);
    Long getIdOfBiggestAcctForUser(Long userId);
    List<Transfer> getTransfersForUser(Long userId);
    Transfer getDetailsForTransfer(Long transferId);
    Transfer requestMoney(Long fromUserId, Long toUserId, BigDecimal amtToTransfer);
}
