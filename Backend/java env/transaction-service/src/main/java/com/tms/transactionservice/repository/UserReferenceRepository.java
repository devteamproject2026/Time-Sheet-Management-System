package com.tms.transactionservice.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.transactionservice.entity.UserReference;
public interface UserReferenceRepository extends JpaRepository<UserReference, Integer> { Optional<UserReference> findByUsername(String username); }
