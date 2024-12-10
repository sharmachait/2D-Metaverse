package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.config.jwt.JwtProvider;
import com.sharmachait.PrimaryBackend.models.dto.LoginDto;
import com.sharmachait.PrimaryBackend.models.entity.User;
import com.sharmachait.PrimaryBackend.models.response.AuthResponse;
import com.sharmachait.PrimaryBackend.service.user.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Data
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    PasswordEncoder passwordEncoder;
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginDto user){
//        if(user.getUsername() == null|| user.getUsername().isBlank()||user.getPassword().isEmpty()){
//            AuthResponse errorResponse = new AuthResponse();
//            errorResponse.setMessage("Username cannot be null or empty");
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(errorResponse);
//        }
        try {
            userService.findByUsername(user.getUsername());
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Username already exists");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(errorResponse);
        } catch (IllegalArgumentException e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
        }
        catch (NoSuchElementException e) {
            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser.setRole(user.getRole());
            String auths = newUser.getRole().toString()+",";
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(auths);
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
            newUser = userService.save(newUser);
            String jwt;
            try{
                jwt = JwtProvider.generateToken(auth, newUser.getId());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null,false,"Unauthorized",null,null));
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setStatus(true);
            authResponse.setMessage("User registered successfully");
            authResponse.setUserId(newUser.getId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(authResponse);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto user) throws Exception {
        try{
            User savedUser = userService.findByUsername(user.getUsername());
            Authentication auth;
            try{
                auth = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUsername(),
                                user.getPassword())
                );
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null,false,"Unauthorized",null, savedUser.getId()));
            }

            String jwt;
            try{
                jwt = JwtProvider.generateToken(auth, savedUser.getId());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null,false,"Unauthorized",null, null));
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setStatus(true);
            authResponse.setMessage("Logged in successfully");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponse);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null,false,"Unauthorized",null,null));
        }
    }

}
