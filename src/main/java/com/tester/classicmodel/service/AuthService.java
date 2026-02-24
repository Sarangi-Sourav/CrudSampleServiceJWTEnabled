package com.tester.classicmodel.service;

import com.tester.classicmodel.config.JwtTokenProvider;
import com.tester.classicmodel.dto.LoginDTO;
import com.tester.classicmodel.dto.SignInDTO;
import com.tester.classicmodel.exception.InvalidCredentialsException;
import com.tester.classicmodel.exception.UserCreationFailedException;
import com.tester.classicmodel.model.Users;
import com.tester.classicmodel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login (LoginDTO loginDto){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.username(),
                    loginDto.password()
            ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);
            return token;
        } catch (Exception ex){
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    public SignInDTO createNewUser(Users user) throws UserCreationFailedException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            Users createdUser = userRepository.save(user);
            return new SignInDTO(createdUser.getName(), createdUser.getUsername(), createdUser.getEmail());
        }
        catch(Exception ex){
            throw new UserCreationFailedException("Unable to create the account, pls retry..\n", ex.getMessage());
        }
    }
}
