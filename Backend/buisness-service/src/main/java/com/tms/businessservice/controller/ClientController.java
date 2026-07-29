package com.tms.businessservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * ADMIN and HR_HEAD can create, read and update Clients. Permanent deletion is
 * limited to ADMIN because it is the highest-risk operation. MANAGER and
 * EMPLOYEE do not need direct Client access in the current project flow.
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
     * Allowed roles: ADMIN and HR_HEAD.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
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
     * Allowed roles: ADMIN and HR_HEAD.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Integer clientId,
            @Valid @RequestBody ClientRequest request) {

        return ResponseEntity.ok(
                clientService.updateClient(clientId, request));
    }

    /**
     * DELETE /api/business/clients/{clientId}
     *
     * Deletes one existing Client.
     * Returns HTTP 204 No Content after successful deletion.
     * Allowed role: ADMIN only.
     *
     * Deleting a Client can affect related Projects, so HR_HEAD is deliberately
     * not allowed to perform this permanent operation.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Integer clientId) {

        clientService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }
}
