package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Tasks;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Integer> {

}
