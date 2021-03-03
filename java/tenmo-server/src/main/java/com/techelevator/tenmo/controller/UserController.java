package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;
import org.apache.catalina.users.MemoryUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;


@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    private UserDAO userDAO;


    public UserController(UserDAO dao){ userDAO = dao; }


    //method to get users accounts
    @RequestMapping(path= "/users", method = RequestMethod.GET)
    public List<User> getAllUserAccounts() {
        return userDAO.findAll();
    }

    //method to show balance
//    @RequestMapping(path= "/users")




    //method for user to send money
//    @RequestMapping(path= "")





    //method to see transfers sent/received





    //method to view details of transfer after providing transfer id










































}
