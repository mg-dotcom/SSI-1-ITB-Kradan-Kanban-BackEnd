package ssi1.integrated.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.services.UserService;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/login")
public class UserController {
    @Autowired
    private UserService userService;

//    @GetMapping("")
//    public List<User>getAllUsers(){
//        return userService.getAllUsers();
//    }
//
//    @GetMapping("/user")
//    public User getUserByName(@RequestBody User inputUser){
//        return userService.getUser(inputUser.getUsername());
//    }

//    @GetMapping("")
//    public UserDTO getUserByUsername(@RequestBody User inputUser){
//        return userService.getUser(inputUser.getUsername());
//    }

    @GetMapping("")
    public boolean verifyPassword(@RequestBody User inputUser){
        UserDTO userDTO=userService.getUser(inputUser.getUsername());
        return userService.verifyPassword(inputUser.getPassword(),userDTO.getPassword());
    }
}
