package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Clients;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, Integer> {

}
