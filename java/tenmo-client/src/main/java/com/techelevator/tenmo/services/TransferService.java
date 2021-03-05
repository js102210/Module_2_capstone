package com.techelevator.tenmo.services;

import org.springframework.web.client.RestTemplate;

public class TransferService {
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    public TransferService (String url){this.BASE_URL=url; }


}
