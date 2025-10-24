/**
 * Created by camoutech
 * Date :16/10/2024
 * Time :02:29
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/profile")
    public ResponseEntity<User> profilehandler(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/profile")
    public ResponseEntity<User> updateProfile(@RequestHeader("Authorization") String jwt, 
                                            @RequestBody User userUpdate) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        
        // Mettre à jour les champs modifiables
        if (userUpdate.getFullName() != null) {
            user.setFullName(userUpdate.getFullName());
        }
        if (userUpdate.getMobile() != null) {
            user.setMobile(userUpdate.getMobile());
        }
        if (userUpdate.getBio() != null) {
            user.setBio(userUpdate.getBio());
        }
        
        // Sauvegarder les modifications
        User updatedUser = userService.updateUser(user);
        
        return ResponseEntity.ok(updatedUser);
    }
}
