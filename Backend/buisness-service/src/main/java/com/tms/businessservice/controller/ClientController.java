package com.tms.businessservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.businessservice.dto.request.ClientRequest;
import com.tms.businessservice.dto.response.ClientResponse;
import com.tms.businessservice.service.ClientService;

import jakarta.validation.Valid;

/**
 * REST APIs for managing Clients.
 *
 * Base URL: /api/business/clients
 *
 * These endpoints are intentionally kept thin: the controller handles HTTP,
 * while ClientService contains the business and database logic.
 *
 * HR_HEAD performs normal Client creation and updates. ADMIN and HR_HEAD can
 * read Client records. Permanent deletion is intentionally not exposed by the
 * current requirements. MANAGER and EMPLOYEE do not need direct Client-
 * management access in the current project flow.
 */
@RestController
@RequestMapping("/api/business/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * POST /api/business/clients
     *
     * Creates one new Client from a JSON request body.
     * Returns HTTP 201 Created with the saved Client and generated ID.
     * Allowed role: HR_HEAD only.
     *
     * Client creation is a normal HR business operation. ADMIN supervises and
     * audits the system instead of entering daily Client data.
     */
    @PreAuthorize("hasRole('HR_HEAD')")
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody ClientRequest request) {

        ClientResponse createdClient = clientService.createClient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdClient);
    }

    /**
     * GET /api/business/clients
     *
     * Returns every Client currently stored in the clients table.
     * Allowed roles: ADMIN and HR_HEAD.
     *
     * HR uses the list for daily work, while ADMIN has read-only access for
     * supervision and auditing.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {

        return ResponseEntity.ok(clientService.getAllClients());
    }

    /**
     * GET /api/business/clients/{clientId}
     *
     * Returns one Client using its numeric ID.
     * Returns HTTP 404 when the ID does not exist.
     * Allowed roles: ADMIN and HR_HEAD.
     *
     * HR uses this for daily work, while ADMIN has read-only access for
     * supervision and auditing.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClientById(
            @PathVariable Integer clientId) {

        return ResponseEntity.ok(clientService.getClientById(clientId));
    }

    /**
     * PUT /api/business/clients/{clientId}
     *
     * Replaces the editable details of an existing Client.
     * Returns the updated Client or HTTP 404 when the ID does not exist.
     * Allowed role: HR_HEAD only.
     *
     * HR maintains Client business details. ADMIN retains read access for
     * supervision but cannot change normal Client information.
     */
    @PreAuthorize("hasRole('HR_HEAD')")
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Integer clientId,
            @Valid @RequestBody ClientRequest request) {

        return ResponseEntity.ok(
                clientService.updateClient(clientId, request));
    }

}
