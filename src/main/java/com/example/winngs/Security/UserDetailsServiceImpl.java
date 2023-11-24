package com.example.winngs.Security;

import com.example.winngs.Repository.UserRepo;
import com.example.winngs.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            User user=userRepo.findByUsername(username);
            Set<GrantedAuthority> authorities=user.getRole().stream()
                    .map((role)->new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toSet());
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);
        }catch(UsernameNotFoundException e){
            throw new UsernameNotFoundException("User not found");
        }



    }
}
