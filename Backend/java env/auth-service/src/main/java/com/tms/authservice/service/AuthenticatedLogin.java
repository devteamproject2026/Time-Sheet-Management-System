package com.tms.authservice.service;

import com.tms.authservice.dto.LoginResponse;

/**
 * Internal login result used only between Auth Service and its controller.
 * The token is separated from LoginResponse so Jackson can never expose it in
 * the JSON body sent to browser JavaScript.
 */
public record AuthenticatedLogin(LoginResponse response, String token) {
}
