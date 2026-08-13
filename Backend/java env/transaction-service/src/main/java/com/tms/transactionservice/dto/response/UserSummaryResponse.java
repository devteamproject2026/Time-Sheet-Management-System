package com.tms.transactionservice.dto.response;

/** Safe user identity used by dropdowns and Transaction responses. */
public record UserSummaryResponse(
        Integer userId,
        String username,
        String fullName) {}
