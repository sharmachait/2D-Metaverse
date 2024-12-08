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
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user){
        try {
            userService.findByUsername(user.getUsername());
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Username already exists");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(errorResponse);
        }
        catch (Exception e) {
            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setPassword(user.getPassword());
            String auths = newUser.getRole().toString()+",";
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(auths);
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
            String jwt;
            try{
                jwt = JwtProvider.generateToken(auth);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null,false,"Unauthorized",null));
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setStatus(true);
            authResponse.setMessage("User registered successfully");
            User savedUser = userService.save(newUser);
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
                        .body(new AuthResponse(null,false,"Unauthorized",null));
            }

            String jwt;
            try{
                jwt = JwtProvider.generateToken(auth);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null,false,"Unauthorized",null));
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setStatus(true);
            authResponse.setMessage("Logged in successfully");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(authResponse);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null,false,"Unauthorized",null));
        }
    }

}
