package com.tms.authservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.tms.authservice.dto.LoginRequest;
import com.tms.authservice.dto.LoginResponse;
import com.tms.authservice.service.AuthService;
import com.tms.authservice.service.AuthenticatedLogin;

import jakarta.servlet.http.Cookie;

class AuthControllerCookieTest {

    @Test
    void successfulLoginStoresTokenOnlyInHttpOnlyCookie() {
        AuthService authService = mock(AuthService.class);
        LoginResponse safeResponse = LoginResponse.builder()
                .userId(2)
                .username("Juned")
                .role("HR_HEAD")
                .message("Login successful")
                .build();
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthenticatedLogin(safeResponse, "signed-jwt"));

        AuthController controller = new AuthController(authService);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        var response = controller.login(new LoginRequest(), servletResponse);
        Cookie jwtCookie = servletResponse.getCookie("jwt");

        assertThat(response.getBody()).isSameAs(safeResponse);
        assertThat(jwtCookie).isNotNull();
        assertThat(jwtCookie.getValue()).isEqualTo("signed-jwt");
        assertThat(jwtCookie.isHttpOnly()).isTrue();
        assertThat(jwtCookie.getPath()).isEqualTo("/");
        assertThat(jwtCookie.getAttribute("SameSite")).isEqualTo("Lax");

        // A future change must not accidentally make the JWT readable by React.
        assertThat(Arrays.stream(LoginResponse.class.getDeclaredFields())
                .map(field -> field.getName()))
                .doesNotContain("token");
    }
}
