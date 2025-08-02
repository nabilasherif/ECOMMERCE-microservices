package main.microservices.wallet.controller;

import main.microservices.wallet.middleware.JwtService;
import main.microservices.wallet.model.User;
import main.microservices.wallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static main.microservices.wallet.middleware.JwtService.generateToken;

@RestController
@RequestMapping(path = "api/wallet")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(final UserService userService, JwtService jwtUtil) {
        this.userService = userService;
    }

    @PostMapping(path = "signup")
    public ResponseEntity<String> registerNewUser(@RequestBody User user) {
        try {
            userService.addUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            User loggedUser = userService.login(body.get("email"), body.get("password"));
            String token = generateToken(loggedUser.getId());
            return ResponseEntity.ok(Map.of(
                    "token", token
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Token generation failed", "details", e.getMessage()));
        }
    }

}
