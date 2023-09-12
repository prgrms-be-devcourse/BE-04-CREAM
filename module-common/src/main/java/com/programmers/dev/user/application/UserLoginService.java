package com.programmers.dev.user.application;


import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserLoginService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String email, String credentials)  {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CreamException(ErrorCode.INVALID_LOGIN_INFO));

        user.checkPassword(passwordEncoder, credentials);

        return user;
    }
}
