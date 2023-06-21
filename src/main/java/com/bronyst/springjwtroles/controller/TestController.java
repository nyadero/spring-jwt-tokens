package com.bronyst.springjwtroles.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
//    publicly available content to all users
    @GetMapping("/all-content")
    public String allContent(){
        return "Viewing content available to all users";
    }

//    user content available to user, moderator, admin
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userContent(){
        return "Viewing this content because you are either a user, moderator or admin";
    }

//    moderator content
    @GetMapping("/moderator")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorContent(){
        return "Viewing this content because you are a moderator";
    }

//  admin content
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminContent(){
        return "Viewing this content because you are an admin";
    }

}
