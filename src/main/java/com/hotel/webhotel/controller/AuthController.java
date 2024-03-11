package com.hotel.webhotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.webhotel.Security.jwt.JwtUtils;
import com.hotel.webhotel.Security.user.UserDetail;
import com.hotel.webhotel.exception.UserAlreadyExistException;
import com.hotel.webhotel.model.User;
import com.hotel.webhotel.request.LoginRequest;
import com.hotel.webhotel.response.JwtRespone;
import com.hotel.webhotel.service.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Register Successful!");
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    public ResponseEntity<?> authenticationUser(@Valid @RequestBody LoginRequest request){
        Authentication authentication = 
        authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForUser(authentication);
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        List<String> roles = userDetail.getAuthorities()
                                        .stream().map(GrantedAuthority::getAuthority)
                                        .toList();
        return ResponseEntity.ok(new JwtRespone(
            userDetail.getId(),
            userDetail.getEmail(),
            jwt,
            roles
        ));
    }   

}
