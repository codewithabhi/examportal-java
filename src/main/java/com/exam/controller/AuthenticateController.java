package com.exam.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.exam.config.JwtUtils;
import com.exam.entities.JWTRequest;
import com.exam.entities.JWTResponse;
import com.exam.entities.User;
import com.exam.service.impl.UserDetailserviceImpl;

@RestController
@CrossOrigin("*")
public class AuthenticateController {
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailserviceImpl userDetailservice;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@PostMapping("/generate-token")
	public ResponseEntity<?> generateToken(@RequestBody JWTRequest request) throws Exception
	{
		String token="";
		
		try {
			Authenticate(request.getUsername(), request.getPassword());
			
			
		} catch (UsernameNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("exception in Auth controller");
			e.printStackTrace();
			
			throw new Exception("User not found"+e.getMessage());
		}
		UserDetails userDetails= userDetailservice.loadUserByUsername(request.getUsername());
		 token=jwtUtils.generateToken(userDetails);
		return ResponseEntity.ok(new JWTResponse(token));
		
	}
	
	public void Authenticate(String username, String password) throws Exception
	{
		
		try {
			System.out.println("Auth controller try block");
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} 
		 catch (DisabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception("User Disabled" +e.getMessage());
			}
		catch (BadCredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UsernameNotFoundException("Invalid Credentials" +e.getMessage());
		}
	}
	@GetMapping("/current-user")
	public User getCurrentUser(Principal principal)
	{
		return (User)this.userDetailservice.loadUserByUsername(principal.getName());
	}

}
