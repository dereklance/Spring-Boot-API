package com.smoothstack.orchestrator.controller;

import com.smoothstack.orchestrator.entity.User;
import com.smoothstack.orchestrator.security.SecurityUtils;
import com.smoothstack.orchestrator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserService userService;

    private final String URL = "http://user-service/users";

    @GetMapping("/admin/search")
    ResponseEntity<User[]> adminUserSearch (
    @RequestParam(value = "email", required = false) String email,
    @RequestParam(value = "firstName", required = false) String firstName,
    @RequestParam(value = "lastName", required = false) String lastName,
    @RequestParam(value = "userId", required = false) Long userId,
    @RequestParam(value = "findall", required = false) boolean findAll) {
        String searchString = URL + "?";
        if (findAll)
            searchString += "findall=true";
        else if (userId != null) {
            searchString += "userId=" + userId;
        } else if (email != null) {
            searchString += "email=" + email;
        } else if (firstName != null && lastName != null)
            searchString += "firstName=" + firstName + "?lastName=" + lastName;
        else if(firstName != null)
            searchString += "firstName=" + firstName;
        else if(lastName != null)
            searchString += "lastName=" + lastName;
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        System.out.println();
        RequestEntity<Void> request = RequestEntity.get(searchString)
                .accept(MediaType.APPLICATION_JSON).build();
        return restTemplate.exchange(request, User[].class);
    }

    @PostMapping("/admin/add")
    ResponseEntity<User> createSpecialUser(@RequestBody User user, Authentication auth) {
        RequestEntity<User> request = RequestEntity.post(URL)
                .accept(MediaType.APPLICATION_JSON)
                .body(user);
        return restTemplate.exchange(request, User.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable long id, Authentication auth) {
        RequestEntity<Void> request = RequestEntity.get(URL + "/" + id)
                .header("user-id", auth.getPrincipal().toString())
                .header("user-role", SecurityUtils.getRole(auth))
                .accept(MediaType.APPLICATION_JSON).build();
        return restTemplate.exchange(request, User.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, Authentication auth) {
        RequestEntity<Void> request = RequestEntity.delete(URL + "/" + id)
                .header("user-id", auth.getPrincipal().toString())
                .header("user-role", SecurityUtils.getRole(auth))
                .accept(MediaType.APPLICATION_JSON).build();;
        return restTemplate.exchange(request, String.class);
    }

    @PutMapping("")
    public ResponseEntity<String> updateUser(@RequestBody User user, Authentication auth) {
        RequestEntity<User> request = RequestEntity.put(URL)
                .header("user-id", auth.getPrincipal().toString())
                .header("user-role", SecurityUtils.getRole(auth))
                .accept(MediaType.APPLICATION_JSON)
                .body(user);
        return restTemplate.exchange(request, String.class);
    }


}
