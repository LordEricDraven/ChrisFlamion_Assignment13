package com.coderscampus.assignment13.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.Address;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.service.AccountService;
import com.coderscampus.assignment13.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/register")
	public String getCreateUser (ModelMap model) {
		model.put("user", new User());
		return "register";
	}
	
	@PostMapping("/register")
	public String postCreateUser (User user) {
		System.out.println(user);
		userService.saveUser(user);
		return "redirect:/register";
	}
	
	@GetMapping("/users")
	public String getAllUsers (ModelMap model, HttpServletResponse response) throws IOException {
		Set<User> users = userService.findAll();
		model.put("users", users);
		if (users.size() == 1) {
			User user = users.iterator().next();
			response.sendRedirect("/users/" + user.getUserId());
			return null;
		}
		return "users";
	}
	@GetMapping("/users/{userId}")
	public String getOneUser (ModelMap model, @PathVariable Long userId) {
		User user = userService.findById(userId);
		if(user.getAddress() == null) {
			user.setAddress(new Address());
		}
		model.put("users", Arrays.asList(user));
		model.put("user", user);
		model.put("address", user.getAddress());
		return "users";
	}
	
	@PostMapping("/users/{userId}")
	public String updateUser (@PathVariable Long userId, User user) {
		User existingUser = userService.findById(userId);
		if(existingUser != null) {
			existingUser.setUsername(user.getUsername());
			if(user.getPassword() != null && !user.getPassword().isEmpty()) {
				existingUser.setPassword(user.getPassword());
			}
			existingUser.setName(user.getName());
			
			Address address = user.getAddress();
			if (address != null) {
				if(existingUser.getAddress() == null) {
					address.setUser(existingUser);
					existingUser.setAddress(address);
				}
				else {
					Address existingAddress = existingUser.getAddress();
					existingAddress.setAddressLine1(address.getAddressLine1());
					existingAddress.setAddressLine2(address.getAddressLine2());
					existingAddress.setCity(address.getCity());
					existingAddress.setRegion(address.getRegion());
					existingAddress.setCountry(address.getCountry());
					existingAddress.setZipCode(address.getZipCode());
				}
			}
			userService.saveUser(existingUser);
		}
		return "redirect:/users/" + userId;
	}

	
//
//	public String postOneUser (User user) {
//		userService.saveUser(user);
//		return "redirect:/users/"+user.getUserId();
//	}
	
	@PostMapping("/users/{userId}/delete")
	public String deleteOneUser (@PathVariable Long userId) {
		userService.delete(userId);
		return "redirect:/users";
	}
	
	@PostMapping("/users/{userId}/accounts")
	public String CreateNewAccount(@PathVariable Long userId, @RequestParam String account) {
		System.out.println("Creating new account " + account + " for user " + userId);
		Account newAccount = new Account();
		newAccount.setAccountName(account);
		userService.createNewAccount(userId, newAccount);
		return "redirect:/users/" + userId;
	}
	
	@GetMapping("/users/{userId}/accounts/{accountId}")
	public String getAccount(@PathVariable Long userId, @PathVariable Long accountId, Model model) {
		User user = userService.findById(userId);
		Account account = accountService.findById(accountId);
		model.addAttribute("user", user);
		model.addAttribute("account", account);
		return "accounts";
	}
	
	@PostMapping("/users/{userId}/accounts/{accountId}")
	public String updateAccount(@PathVariable Long userId, @PathVariable Long accountId, @RequestParam String accountName) {
		Account account = accountService.findById(accountId);
		account.setAccountName(accountName);
		accountService.save(account);;
		return "redirect:/users/" + userId;
	}
}
