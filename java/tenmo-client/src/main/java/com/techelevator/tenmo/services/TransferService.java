package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransferService {
    private String BASE_URL;
    public static String AUTH_TOKEN = "";
    private RestTemplate restTemplate = new RestTemplate();
    public TransferService (String url){this.BASE_URL=url; }


    public boolean sendMoney (Long toUserId, BigDecimal amtToTransfer){
        return restTemplate.exchange(BASE_URL + "/user/sendmoney/" + toUserId +"/"+ amtToTransfer, HttpMethod.PUT, makeAuthEntity(), boolean.class).getBody();
    }

    public boolean requestMoney(Long fromUserId, BigDecimal amtToTransfer){
        return restTemplate.exchange(BASE_URL + "/user/requestmoney/"+ fromUserId + amtToTransfer, HttpMethod.POST, makeAuthEntity(), boolean.class).getBody();
    }

    public Transfer[] getAllTransfersForUser(){
        return restTemplate.exchange(BASE_URL + "/user/alltransfers", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
    }

    public Transfer[] showPendingTransfers(){
        //call method to get all transfers, filter out the non-pending ones and store those in a new array
        Transfer[] allTransfers = getAllTransfersForUser();
        List<Transfer> pendingTransferList = new ArrayList<>();
        for (Transfer t : allTransfers){
            if (t.getTransferStatusName().equals("Pending")){
                pendingTransferList.add(t);
            }
        }
      Transfer[] pendingTransfers = new Transfer[pendingTransferList.size()];
        pendingTransferList.toArray(pendingTransfers);
        return pendingTransfers;
    }

    public Transfer getDetailsForTransfer(int transferId){
        return restTemplate.exchange(BASE_URL + "/transferdetails?transferId=" + transferId, HttpMethod.GET, makeAuthEntity(), Transfer.class).getBody();
    }


    private HttpEntity makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
