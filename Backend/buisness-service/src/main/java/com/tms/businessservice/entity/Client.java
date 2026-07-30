package com.tms.businessservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents one client from the clients database table.
 *
 * This class is a JPA entity, so Spring Data JPA uses it when reading and
 * writing client records in MySQL.
 */
@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "contact", length = 15)
    private String contact;

    @Column(name = "address", length = 255)
    private String address;

    // Hibernate fills this field when a new Client is saved.
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
