package com.andy.tempoapp.service.internal;



import com.andy.tempoapp.entity.Subscription;
import com.andy.tempoapp.entity.User;
import com.andy.tempoapp.repository.SubscriptionRepository;
import com.andy.tempoapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public UserService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;

    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }


}