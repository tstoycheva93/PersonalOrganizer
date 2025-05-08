package app.user.service;

import app.exception.*;
import app.security.AuthUser;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionType;
import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.utils.DateUtils;
import app.web.dto.EditUserRequestByAdmin;
import app.web.dto.RegisterRequest;
import app.web.dto.UserRequest;
import jakarta.transaction.Transactional;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

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
        if (!user.getUsername().equals(userRequest.getUsername())) {
            checkForExistingUsername(userRequest.getUsername());
            user.setUsername(userRequest.getUsername());
        }
        if (!user.getEmail().equals(userRequest.getEmail())) {
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
        if (passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(userRequest.getConfirmPassword()));
            userRepository.save(user);
            return;
        }
        throw new PasswordDoesNotMatch("The passwords does not match");
    }

    public int countTotalUsers() {
        return userRepository.findAll().size();
    }


    public int getPremiumUsers() {
        int count = 0;
        for (User user : userRepository.findAll()) {
            if (user.getSubscription().getType().equals(SubscriptionType.PREMIUM)) {
                count++;
            }
        }
        return count;
    }

    public List<User> topUsers() {
        return userRepository.findTop3BySubscriptionTypeOrderBySubscriptionCountDesc(SubscriptionType.PREMIUM);
    }

    public void updateSubscriptionCountIfPremium(User user) {
        if (user.getSubscription().getType().equals(SubscriptionType.PREMIUM)) {
            user.setSubscriptionCount(user.getSubscriptionCount() + 1);
            userRepository.save(user);
        }
    }


    public void editUserByAdmin(EditUserRequestByAdmin userRequest) {
        User user = getById(userRequest.getUserId());
        if(userRequest.getStatus()==null){
            throw new UserStatusException("Must set user status!");
        }
        if(userRequest.getSubscriptionType()==null){
            throw new UserSubsException("Must set subscription!");
        }
        if (userRequest.getStatus().equals("active")) {
            user.setActive(true);
        } else {
            user.setActive(false);
        }
        user.getSubscription().setType(userRequest.getSubscriptionType());
        if (userRequest.getSubscriptionType().equals(SubscriptionType.PREMIUM)) {
            user.setSubscriptionCount(user.getSubscriptionCount() + 1);
        }
        userRepository.save(user);

    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getUsersByStatusList(boolean isActive) {
        return getAll().stream()
                .filter(user -> user.isActive() == isActive)
                .toList();
    }

    public int getActiveUsersCount() {
        return getAll().stream()
                .filter(User::isActive)
                .toList()
                .size();
    }

    public Map<String, Integer> getChartInfoForUsers(String growth) {
        switch (growth) {
            case "week" -> {
                return getChartInfoForOneWeek();
            }
            case "month" -> {
                YearMonth month = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth());
                List<DateUtils> weeks = getWeeksByISO(month);
                return getStringIntegerMap(weeks);
            }
            case "year" -> {
                List<DateUtils> dateUtils = getForMonth();
                return getStringIntegerMap(dateUtils);
            }
        }
        return getChartInfoForOneWeek();
    }

    private Map<String, Integer> getChartInfoForOneWeek() {
        Map<String, Integer> chartInfo = new LinkedHashMap<>();
        int days = 0;
        while (days < 7) {
            LocalDate today = LocalDate.now();
            today = today.minusDays(days);
            List<User> orders = getUsersByDay(today.atStartOfDay(), today.atTime(23, 59, 59));
            chartInfo.put(today.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), orders.size());
            days++;
        }
        return chartInfo;
    }

    private List<User> getUsersByDay(LocalDateTime start, LocalDateTime end) {
        return userRepository.findAllByCreatedAtBetween(start, end);
    }

    private List<DateUtils> getWeeksByISO(YearMonth month) {
        List<DateUtils> dateUtils = new ArrayList<>();
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d").localizedBy(Locale.ENGLISH);
        while (!start.isAfter(end)) {
            LocalDate weekEnd = start.with(DayOfWeek.SUNDAY);
            if (weekEnd.isAfter(end)) weekEnd = end;
            String label;
            if (start.getDayOfMonth() == 1) {
                label = "Begin: - " + weekEnd.format(formatter);
            } else if (weekEnd.equals(end)) {
                label = start.format(formatter) + " - End";
            } else {
                label = start.format(formatter) + " - " + weekEnd.format(formatter);
            }
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = weekEnd.atTime(23, 59, 59);
            dateUtils.add(new DateUtils(label, startDateTime, endDateTime));
            start = weekEnd.plusDays(1);
        }

        return dateUtils;
    }

    private Map<String, Integer> getStringIntegerMap(List<DateUtils> dateUtils) {
        Map<String, Integer> chartInfo = new LinkedHashMap<>();
        for (DateUtils month : dateUtils) {
            List<User> orders = getUsersByDay(month.getStartDateTime(), month.getEndDateTime());
            chartInfo.put(month.getLabel(), orders.size());
        }
        return chartInfo;
    }

    private List<DateUtils> getForMonth() {
        List<DateUtils> dateUtils = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            LocalDate firstDay = LocalDate.of(LocalDate.now().getYear(), month, 1);
            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
            dateUtils.add(new DateUtils(
                    firstDay.getMonth().name(),
                    firstDay.atStartOfDay(),
                    lastDay.atTime(23, 59, 59)
            ));
        }
        return dateUtils;
    }

    public List<User> getRecentActivityForDay(LocalDateTime localDateTime) {
        return userRepository.findAllByCreatedAtBetween(localDateTime, localDateTime.toLocalDate().atTime(23, 59, 59));
    }

    public User getByEmail(String userSearch) {
        Optional<User> userWithEmail = userRepository.findByEmail(userSearch);
        if (userWithEmail.isEmpty()) {
            throw new EmailNotFoundException("Email not found");
        }
        return userWithEmail.get();
    }

    public void deleteUser(User user) {
        user.setActive(false);
        userRepository.save(user);
    }

    public void setNewPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public List<User> getUsers(String name, String status, String sortType, String subscriptionType) {
        List<User> users = getAll();




        if (status!=null && !status.isEmpty()) {
           boolean isActive = status.equals("active");
            if (isActive) {
                users = getUsersByStatusList(true);
            } else users = getUsersByStatusList(false);
        }
        if (name != null && (!name.isEmpty())) {
            users = users.stream()
                    .filter(u -> u.getUsername()
                            .toLowerCase()
                            .contains(name.toLowerCase())).toList();
        }
        if (subscriptionType != null && (!subscriptionType.isEmpty())) {
            SubscriptionType selectedType = SubscriptionType.valueOf(subscriptionType.toUpperCase());
            users = users.stream()
                    .filter(u -> u.getSubscription().getType().equals(selectedType))
                    .toList();
        }

        if (sortType != null && (!sortType.isEmpty())) {
            switch (sortType) {
                case "subscription":
                    users = users.stream()
                            .sorted(Comparator.comparing(u->u.getSubscription().getType()))
                            .collect(Collectors.toList());
                    break;
                case "date":
                    users = users.stream()
                            .sorted(Comparator.comparing(User::getCreatedAt))
                            .collect(Collectors.toList());
                    break;
                case "name":
                    users = users.stream()
                            .sorted(Comparator.comparing(User::getName))
                            .collect(Collectors.toList());
                    break;
            }
        }


        return users;

    }
}
