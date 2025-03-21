package com.base.demo.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.base.demo.dto.LoginDto;
import com.base.demo.dto.UserRegisterDto;
import com.base.demo.entity.User;
import com.base.demo.exception.UserAlreadyExistsException;
import com.base.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService implements UserDetailsService{
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new AuthenticationException("User not found") {};
    }
    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassHash(), new ArrayList<>());
  }

  @Transactional
  public User authenticate(LoginDto loginDto) {
    User user = userRepository.findByEmail(loginDto.getEmail());
    
    if (user == null || 
      !passwordEncoder.matches(loginDto.getPassword(), user.getPassHash())){
      throw new AuthenticationException("Wrong user or password") {};
    }

    return user;
  }

  @Transactional
  public User register(UserRegisterDto registerDto) {
    if (registerDto.getEmail() == null || registerDto.getEmail().isEmpty()
      || registerDto.getPassword() == null || registerDto.getPassword().isEmpty()
      || registerDto.getName() == null || registerDto.getName().isEmpty()) {
      throw new IllegalArgumentException("Email, password, name must be provided");
    }

    User user = userRepository.findByEmail(registerDto.getEmail());
    if (user != null) {
      throw new UserAlreadyExistsException(registerDto.getEmail());
    }
    
    user = registerDto.toUser(passwordEncoder);
    userRepository.save(user);

    return user;
  }

  public void delete(User user) {
    userRepository.delete(user);
  }

  
}
