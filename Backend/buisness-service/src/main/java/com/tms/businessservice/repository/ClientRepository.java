package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Client;

/**
 * Provides standard database operations for Client entities.
 *
 * JpaRepository already supplies save, findAll, findById, deleteById and other
 * common methods, so basic CRUD does not require handwritten SQL.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

}
