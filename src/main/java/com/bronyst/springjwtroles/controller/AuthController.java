package com.bronyst.springjwtroles.controller;

import com.bronyst.springjwtroles.entities.ERole;
import com.bronyst.springjwtroles.entities.Role;
import com.bronyst.springjwtroles.entities.User;
import com.bronyst.springjwtroles.payload.request.LoginRequest;
import com.bronyst.springjwtroles.payload.request.SignupRequest;
import com.bronyst.springjwtroles.payload.response.JwtResponse;
import com.bronyst.springjwtroles.payload.response.MessageResponse;
import com.bronyst.springjwtroles.repository.RoleRepository;
import com.bronyst.springjwtroles.repository.UserRepository;
import com.bronyst.springjwtroles.security.jwt.JwtUtils;
import com.bronyst.springjwtroles.service.UserDetailsImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
   AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
      if (userRepository.existsByUsername(signupRequest.getUsername())){
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken"));
      }
      if (userRepository.existsByEmail(signupRequest.getEmail())){
       return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use"));
      }

//      create user account
     User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));
     Set<String> stringRoles = signupRequest.getRole();
     Set<Role> roles = new HashSet<>();
     if (stringRoles == null){
      Role userRole = roleRepository.findByRole(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role not found"));
      roles.add(userRole);
     }else{
         stringRoles.forEach(role -> {
             switch (role){
                 case "admin":
                     Role adminRole = roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Admin Role is not found"));
                     roles.add(adminRole);
                     break;

                 case "moderator":
                     Role modRole = roleRepository.findByRole(ERole.ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Error: Moderator Role is not found"));
                     roles.add(modRole);
                     break;

                 default:
                     Role userRole = roleRepository.findByRole(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: User Role is not found"));
                     roles.add(userRole);
             }
         });
     }

     user.setRoles(roles);
     userRepository.save(user);
     return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

//    signin user
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody LoginRequest loginRequest){
       Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImp userDetails = (UserDetailsImp) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

}
