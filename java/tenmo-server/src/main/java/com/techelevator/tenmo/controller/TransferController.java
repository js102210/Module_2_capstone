package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
    private UserDAO userDAO;
    private TransferDAO transferDAO;

    public TransferController(UserDAO userDAO, TransferDAO transferDAO){
        this.userDAO = userDAO;
        this.transferDAO = transferDAO;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/user/sendmoney/{toUserId}/{amtToTransfer}", method = RequestMethod.PUT)
    public boolean sendMoney(Principal p, @PathVariable() Long toUserId , @PathVariable() BigDecimal amtToTransfer){
        Transfer t = null;
        try {
            Long id = getCurrentUserId( p);
            t=  transferDAO.sendMoney(id, toUserId, amtToTransfer);
        }catch(RestClientResponseException e){
            System.out.println("this is the exception false");
            return false;
        }
        System.out.println("transfer successful");
        return t != null;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "user/requestmoney/{fromUserId}/{amtToTransfer}", method = RequestMethod.POST)
    public boolean requestMoney(Principal p, @PathVariable() Long fromUserId, @PathVariable() BigDecimal amtToTransfer){
        Transfer t = null;
        try {
            Long id = getCurrentUserId(p);
            t = transferDAO.requestMoney(id, fromUserId, amtToTransfer);
        } catch(RestClientResponseException e){
            System.out.println("this is the exception false");
            return false;
        }
        System.out.println("request successful");
        return t != null;
    }

    @RequestMapping(path = "/user/alltransfers", method = RequestMethod.GET)
    public List<Transfer> getAllTransfersForUser(Principal p){
        return transferDAO.getTransfersForUser(getCurrentUserId(p));
    }

    @RequestMapping(path = "/transferdetails", method = RequestMethod.GET)
    public Transfer getDetailsForTransfer(@RequestParam Long transferId){
        return transferDAO.getDetailsForTransfer(transferId);
    }

    @RequestMapping(path = "/user/transferrequest/{transferId}/{toUserId}/approved/amtToTransfer", method = RequestMethod.PUT)
    public boolean approveTransfer(Principal p, @PathVariable Long transferId, @PathVariable Long toUserId, @PathVariable BigDecimal amtToTransfer){
        if(!toUserId.equals(getCurrentUserId(p))){
          transferDAO.acceptOrRejectPendingRequest(toUserId, getCurrentUserId(p), amtToTransfer, transferId, true );
          return true;
        } else{
            return false;
        }
    }

    @RequestMapping(path = "/user/transferrequest/{transferId}/{toUserId}/denied/amtToTransfer", method = RequestMethod.PUT)
    public boolean deniedTransfer(Principal p, @PathVariable Long transferId, @PathVariable Long toUserId, @PathVariable BigDecimal amtToTransfer){
        if(!toUserId.equals(getCurrentUserId(p))){
            transferDAO.acceptOrRejectPendingRequest(toUserId, getCurrentUserId(p), amtToTransfer, transferId, false );
            return true;
        } else{
            return false;
        }
    }



    private Long getCurrentUserId(Principal principal) {
        return userDAO.findByUsername(principal.getName()).getId();
    }
}
