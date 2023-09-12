package com.programmers.dev.healthcheck;

import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class HealthCheck {

    private final static Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    private final UserRepository userRepository;

    public HealthCheck(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthCheck() {
        logger.info("logger-test");

        List<User> all = userRepository.findAll();
        logger.info("all-size={}", all.size());
        
        return ResponseEntity.ok("CREAM order-server is ACTIVE");
    }
}
