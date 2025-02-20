package com.base.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.base.demo.dto.LoginDto;
import com.base.demo.dto.RegisterDto;
import com.base.demo.entity.User;
import com.base.demo.exception.UserAlreadyExistsException;
import com.base.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Transactional
  public User authenticate(LoginDto loginDto) {
    User user = userRepository.findByEmail(loginDto.getEmail());
    
    if (user == null || 
      !passwordEncoder.matches(loginDto.getPassword(), user.getPassHash())){
      throw new AuthenticationException("User not found") {};
    }

    return user;
  }

  @Transactional
  public User register(RegisterDto registerDto) {
    User user = userRepository.findByEmail(registerDto.getEmail());
    if (user != null) {
      throw new UserAlreadyExistsException(registerDto.getEmail());
    }
    
    user = registerDto.toUser(passwordEncoder);
    userRepository.save(user);

    return user;
  }

  
}
