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
    public boolean sendMoney(Principal p, @PathVariable() Long toUserId , @PathVariable BigDecimal amtToTransfer){
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

    @RequestMapping(path = "/user/alltransfers", method = RequestMethod.GET)
    public List<Transfer> getAllTransfersForUser(Principal p){
        return transferDAO.getTransfersForUser(getCurrentUserId(p));
    }

    @RequestMapping(path = "/transferdetails", method = RequestMethod.GET)
    public Transfer getDetailsForTransfer(@RequestParam Long transferId){
        return transferDAO.getDetailsForTransfer(transferId);
    }



    private Long getCurrentUserId(Principal principal) {
        return userDAO.findByUsername(principal.getName()).getId();
    }
}
