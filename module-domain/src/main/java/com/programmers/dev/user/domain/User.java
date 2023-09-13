package com.programmers.dev.user.domain;

import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "NICKNAME", nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(name = "ACCOUNT", nullable = false)
    private Long account;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE")
    private UserRole userRole;

    protected User() { }

    public User(String email, String password, String nickname, Long account, Address address, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.account = account;
        this.address = address;
        this.userRole = userRole;
    }

    public void deposit(Long money) {
        this.account += money;
    }

    public void withdraw(Long money) {
        validate(money);
        this.account -= money;
    }

    private void validate(Long money) {
        if (this.account - money < 0) {
            throw new CreamException(ErrorCode.INSUFFICIENT_ACCOUNT_MONEY);
        }
    }

    public void checkPassword(PasswordEncoder passwordEncoder, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, this.password)) {
            throw new CreamException(ErrorCode.INVALID_LOGIN_INFO);
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public Long getAccount() {
        return account;
    }

    public Address getAddress() {
        return address;
    }

    public UserRole getUserRole() {
        return userRole;
    }
}
