package com.tester.classicmodel.controller;

import com.tester.classicmodel.dto.AuthResponseDTO;
import com.tester.classicmodel.dto.LoginDTO;
import com.tester.classicmodel.dto.SignInDTO;
import com.tester.classicmodel.exception.UserCreationFailedException;
import com.tester.classicmodel.model.Users;
import com.tester.classicmodel.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> helloAdmin(){
        return ResponseEntity.ok("Hello Admin");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<String> helloUser(){
        return ResponseEntity.ok("Hello User");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login (@RequestBody LoginDTO loginDTO){
        String token = authService.login(loginDTO);
        AuthResponseDTO authResponseDTO =  new AuthResponseDTO(token);
        return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/signin")
//    @ExceptionHandler(UserCreationFailedException.class)
    public ResponseEntity<SignInDTO> createNewUser(@RequestBody Users user){
        // can validate the username and email and also the password for the strength from BE as well
        // but will skip it for this one
        SignInDTO createdUser =  authService.createNewUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

}
