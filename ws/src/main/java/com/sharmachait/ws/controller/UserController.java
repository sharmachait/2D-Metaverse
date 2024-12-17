package com.sharmachait.ws.controller;

import com.sharmachait.ws.model.User;
import com.sharmachait.ws.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @MessageMapping("/user/addUser")//app/user/addUser
    @SendTo("/user/topic")
    public User addUser(@Payload User user){
        return userService.saveUser(user);
    }

    @MessageMapping("/user/disconnectUser")//app/user/disconnectUser
    @SendTo("/user/topic")
    public User disconnectUser(@Payload User user){
        return userService.disconnect(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getConnectedUsers(){
         return ResponseEntity.ok(userService.getOnlineUsers());
    }
}
