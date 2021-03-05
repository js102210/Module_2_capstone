package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final RestTemplate restTemplate= new RestTemplate();
    private String BASE_URL;
    public static String AUTH_TOKEN= "";

    public UserService(String url){
        this.BASE_URL=url;
    }

    //method to get all users and their ids
    public User[] getAllUsers() {
        User[] allUsers = null;
        allUsers = restTemplate.exchange(BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
        return allUsers;
    }




    //Creates new Http Entity with the Bearer Auth token header
    private HttpEntity makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
