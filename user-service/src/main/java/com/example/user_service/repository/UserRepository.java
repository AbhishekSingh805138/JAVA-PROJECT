package com.example.user_service.repository;
import com.example.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByUsername(String username);
}
