package com.tms.businessservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    /**
     * Finds the current database user from the verified JWT username.
     */
    Optional<Users> findByUsername(String username);
}
