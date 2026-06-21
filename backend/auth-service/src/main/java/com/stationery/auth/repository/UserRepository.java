package com.stationery.auth.repository;

import com.stationery.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {  // JpaRepository provides CRUD operations for User entity 
    Optional<User> findByEmail(String email);
}
 