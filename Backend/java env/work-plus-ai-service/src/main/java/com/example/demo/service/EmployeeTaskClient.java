package com.example.demo.service;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.demo.dto.TaskContext;
import com.example.demo.exception.AiServiceException;

@Component
public class EmployeeTaskClient {
    private final DiscoveryClient discoveryClient;

    public EmployeeTaskClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /** Finds Transaction Service via Eureka and forwards only the JWT cookie. */
    public List<TaskContext> getMyTasks(String jwtCookieHeader) {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances("transaction-service");
            if (instances.isEmpty()) {
                throw new AiServiceException(
                        "Transaction Service is not registered in Eureka. Start it and try again.");
            }

            RestClient transactionClient = RestClient.create(
                    instances.getFirst().getUri().toString());
            List<TaskContext> tasks = transactionClient.get()
                    .uri("/api/transactions/tasks/my")
                    .header(HttpHeaders.COOKIE, jwtCookieHeader)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TaskContext>>() { });
            return tasks == null ? List.of() : tasks;
        } catch (AiServiceException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new AiServiceException(
                    "Your Task data is currently unavailable. Please try again later.", exception);
        }
    }
}
