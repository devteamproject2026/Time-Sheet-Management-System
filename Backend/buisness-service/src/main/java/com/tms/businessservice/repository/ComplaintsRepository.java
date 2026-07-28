package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Complaints;

@Repository
public interface ComplaintsRepository extends JpaRepository<Complaints, Integer> {

}