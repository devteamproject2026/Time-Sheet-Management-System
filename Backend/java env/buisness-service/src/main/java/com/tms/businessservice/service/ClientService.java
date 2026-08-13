package com.tms.businessservice.service;

import java.util.List;

import com.tms.businessservice.dto.request.ClientRequest;
import com.tms.businessservice.dto.response.ClientResponse;

/**
 * Defines the business operations available for Clients.
 *
 * The controller added in the next step will call this interface instead of
 * accessing the database repository directly.
 */
public interface ClientService {

    ClientResponse createClient(ClientRequest request);

    List<ClientResponse> getAllClients();

    ClientResponse getClientById(Integer clientId);

    ClientResponse updateClient(Integer clientId, ClientRequest request);
}
