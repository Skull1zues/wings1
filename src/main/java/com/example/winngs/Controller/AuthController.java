package com.example.winngs.Controller;

import com.example.winngs.Payload.LoginRequest;
import com.example.winngs.Payload.SignupRequest;
import com.example.winngs.Repository.RoleRepo;
import com.example.winngs.Repository.UserRepo;
import com.example.winngs.Security.jwt.JwtUtils;
import com.example.winngs.models.ERole;
import com.example.winngs.models.Role;
import com.example.winngs.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    RoleRepo roleRepo;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token=jwtUtils.generateToken(authentication);
        return new ResponseEntity<>("token="+token, HttpStatus.OK);

    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest){
        if(userRepo.existsByUsername(signupRequest.getUsername()))
            return ResponseEntity.badRequest().body("Username already exist");
        if(userRepo.existsByEmail(signupRequest.getEmail()))
            return ResponseEntity.badRequest().body("Email already exist");

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepo.findByName(ERole.ROLE_USER);

            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        try{
                        Role adminRole = roleRepo.findByName(ERole.ROLE_ADMIN);
                        roles.add(adminRole);
                }catch(Exception e) {
                    throw new RuntimeException("Error: Role is not found.");
                }

                        break;
                    case "mod":
                        try {
                        Role modRole = roleRepo.findByName(ERole.ROLE_MODERATOR);
                            roles.add(modRole);
                        }catch(Exception e) {
                            throw new RuntimeException("Error: Role is not found.");
                }


                        break;
                    default:
                        try {
                            Role userRole = roleRepo.findByName(ERole.ROLE_USER);
                            roles.add(userRole);
                        }catch(Exception e) {
                            throw new RuntimeException("Error: Role is not found.");
                        }

                }
            });
        }


        User user =new User(signupRequest.getUsername(),signupRequest.getEmail(),encoder.encode(signupRequest.getPassword()),roles);
        userRepo.save(user);
        return ResponseEntity.ok("User created");

    }
}
