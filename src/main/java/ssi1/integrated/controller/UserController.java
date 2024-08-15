package ssi1.integrated.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.services.UserService;
import ssi1.integrated.user_account.User;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<User>getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/user")
    public User getUserByName(@RequestBody User inputUser){
        return userService.getUser(inputUser.getName());
    }
}
