package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Timesheets;

@Repository
public interface TimesheetsRepository extends JpaRepository<Timesheets, Integer> {

}
