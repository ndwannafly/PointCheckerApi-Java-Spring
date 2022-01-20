package com.example.wp.repository;

import com.example.wp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    int countUserByLogin(String login);
    int countUserByLoginAndPassword(String login, String password);
    User getUserByLogin(String login);
}
