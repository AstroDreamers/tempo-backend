package com.andy.tempoapp.service.internal;



import com.andy.tempoapp.entity.Subscription;
import com.andy.tempoapp.entity.User;
import com.andy.tempoapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }


    @Transactional
    public void addSubscription(Long userId, Subscription subscription) {
        User chosenUser = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found with id: " + userId)
        );

        chosenUser.getSubscriptions().add(subscription);
        chosenUser.setHasSubscriptions(true);
        subscription.setUser(chosenUser);
        userRepository.save(chosenUser);
    }

    @Transactional
    public void removeSubscription(Long userId, Subscription subscription) {
        User chosenUser = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found with id: " + userId)
        );

        if (!chosenUser.getSubscriptions().contains(subscription)) {
            throw new IllegalArgumentException("Subscription not found for the user");
        }

        chosenUser.getSubscriptions().remove(subscription);
        subscription.setUser(null); // Break the relationship

        if (chosenUser.getSubscriptions().isEmpty()) {
            chosenUser.setHasSubscriptions(false);
        }
        userRepository.save(chosenUser);
    }
}