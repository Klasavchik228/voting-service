package com.klasavchik.voting_service.repository;

import com.klasavchik.voting_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}