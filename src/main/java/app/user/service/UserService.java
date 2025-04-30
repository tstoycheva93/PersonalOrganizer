package app.user.service;

import app.exception.EmailAlreadyExistException;
import app.exception.PasswordDoesNotMatch;
import app.exception.UsernameAlreadyExistException;
import app.security.AuthUser;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;
import app.web.dto.UserRequest;
import jakarta.transaction.Transactional;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionService subscriptionService;

    public UserService(UserRepository userRepository, ConversionService conversionService, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Wrong credentials"));
        return new AuthUser(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Wrong credentials"));
    }
    @Transactional
    public void registerUser(RegisterRequest input) {
        checkForExistingEmailAndUsername(input.getUsername(), input.getEmail());
        if (!input.getPassword().equals(input.getConfirmPassword())) {
            throw new PasswordDoesNotMatch("The passwords does not match");
        }
        User user = conversionService.convert(input, User.class)
                .toBuilder()
                .password(passwordEncoder.encode(input.getPassword()))
                .build();
        Subscription subscription = subscriptionService.createDefaultSubscription(user);
        user.setSubscription(subscription);
        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void editUser(User user, UserRequest userRequest) {
        user.setName(userRequest.getName());
        if(!user.getUsername().equals(userRequest.getUsername())) {
            checkForExistingUsername(userRequest.getUsername());
            user.setUsername(userRequest.getUsername());
        }
        if(!user.getEmail().equals(userRequest.getEmail())) {
            checkForExistingEmailAndUsername(userRequest.getEmail(), userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
        }
        user.setPhotoPath(userRequest.getPhoto());
        userRepository.save(user);
    }

    private void checkForExistingEmailAndUsername(String username, String email) {
        checkForExistingUsername(username);
        checkForExistingEmail(email);
    }

    private void checkForExistingUsername(String username) {
        Optional<User> userWithUsername = userRepository.findByUsername(username);
        if (userWithUsername.isPresent()) {
            throw new UsernameAlreadyExistException("Username is already in use");
        }
    }

    private void checkForExistingEmail(String email) {
        Optional<User> userWithEmail = userRepository.findByEmail(email);
        if (userWithEmail.isPresent()) {
            throw new EmailAlreadyExistException("Email is already in use");
        }
    }

    public void editPassword(User user, UserRequest userRequest) {
        if(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(userRequest.getConfirmPassword()));
            userRepository.save(user);
        }
        throw new PasswordDoesNotMatch("The passwords does not match");
    }
}
