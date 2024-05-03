package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.utils.Utils;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    // TODO: wire in the user repository (~ 1 line)
    @Autowired
    private UserRepository userRepository;

    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@Validated @RequestBody CreateUserPayload payload, BindingResult bindingResult) {
        // TODO: Create an user entity with information given in the payload, store it in the database
        //       and return the id of the user in 200 OK response

        // get the user info from the payload
        String name = payload.getName();
        String email = payload.getEmail();

        // validate the payload
        if (!Utils.isValidRequestBodyParam(name) || !Utils.isValidRequestBodyParam(email)) {
            // payload invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // validate the payload, e.g. email format
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> {
                System.out.println("payload validation error: " + e.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // check if the username or email is already used
        if (userRepository.existsByName(name) || userRepository.existsByEmail(email)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }

        // create a new user entity according to the payload
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        // store it in the database
        userRepository.save(user);
        // response
        return ResponseEntity.status(HttpStatus.OK).body(user.getId());

    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        // TODO: Return 200 OK if a user with the given ID exists, and the deletion is successful
        //       Return 400 Bad Request if a user with the ID does not exist
        //       The response body could be anything you consider appropriate

        // check if a user with the given ID exists
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user with the given ID does not exist");
        }

        // delete the user by id
        userRepository.deleteById(userId);
        return ResponseEntity.status(HttpStatus.OK).body("user deletion is successful");
    }
}
