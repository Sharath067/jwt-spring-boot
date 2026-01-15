package com.ecommerce.winz.repository;

import com.ecommerce.winz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailOrUserName(String email, String userName);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
}
