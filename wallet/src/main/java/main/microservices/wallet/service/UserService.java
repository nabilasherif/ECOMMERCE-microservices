package main.microservices.wallet.service;

import main.microservices.wallet.model.User;
import main.microservices.wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        Optional<User> userOptional= userRepository.findUserByEmail(user.getEmail());

        if (userOptional.isPresent()) {
            throw new IllegalStateException("Email already in use");
        }

        userRepository.save(user);
    }

    public User login(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalStateException("Invalid email or password"));
    }

}
