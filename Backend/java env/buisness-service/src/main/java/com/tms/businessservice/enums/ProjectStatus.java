package com.tms.businessservice.enums;

/**
 * Values allowed by the projects.status ENUM column in the shared SQL file.
 */
public enum ProjectStatus {

    // Project work is currently continuing.
    ACTIVE,

    // All planned work for the Project has finished.
    COMPLETED,

    // Work is temporarily paused and may continue later.
    ON_HOLD
}
