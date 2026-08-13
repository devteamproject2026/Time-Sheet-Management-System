package com.tms.transactionservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

/**
 * Read-only account reference. Auth Service remains the only owner of users.
 */
@Entity
@Immutable
@Table(name = "users")
@Getter
public class UserReference {
	@Id
	@Column(name = "user_id")
	private Integer userId;
	private String username;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	private String role;
	@Column(name = "account_status")
	private String accountStatus;
	@Column(name = "approval_status")
	private String approvalStatus;
}
