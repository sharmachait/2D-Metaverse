package com.sharmachait.PrimaryBackend.service;

//import com.sharmachait.PrimaryBackend.models.entity.User;
import com.sharmachait.PrimaryBackend.models.entity.Role;
import com.sharmachait.PrimaryBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    com.sharmachait.PrimaryBackend.models.entity.User user = userRepository.findByUsername(username);
    if (user == null)
      throw new UsernameNotFoundException(username);

    List<GrantedAuthority> authorities = new ArrayList<>();
    Role role = user.getRole();
    authorities.add(new SimpleGrantedAuthority(role.toString()));
    return new User(user.getUsername(), user.getPassword(), authorities);
  }
}
