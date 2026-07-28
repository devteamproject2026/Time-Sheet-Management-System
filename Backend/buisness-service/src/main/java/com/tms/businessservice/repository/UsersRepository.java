package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

}
