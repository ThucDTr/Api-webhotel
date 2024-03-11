package com.hotel.webhotel.service.ipml;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel.webhotel.exception.UserAlreadyExistException;
import com.hotel.webhotel.model.Role;
import com.hotel.webhotel.model.User;
import com.hotel.webhotel.repository.RoleRepository;
import com.hotel.webhotel.repository.UserRepository;
import com.hotel.webhotel.service.IUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceipml implements IUserService{

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
       if(userRepository.existsByEmail(user.getEmail())){
        throw new UserAlreadyExistException(user.getEmail() + "already exists");
       }

       user.setPassword(passwordEncoder.encode(user.getPassword()));
       Role userRole = roleRepository.findByName("ROLE_USER").get();
       user.setRoles(Collections.singletonList(userRole));
       return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void removeUser(String email) {
        User theUser = getUser(email);
        if(theUser != null){

            userRepository.deleteByEmail(email);
        }
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not fuond"));
    }
     
}
