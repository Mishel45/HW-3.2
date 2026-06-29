package org.springframework.boot.test.web.client;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

public class TestRestTemplate {
    private final RestTemplate restTemplate = new RestTemplate();

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType) {
        return restTemplate.postForEntity(url, request, responseType);
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType) {
        try {
            return restTemplate.getForEntity(url, responseType);
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).build();
        }
    }

    public <T> ResponseEntity<T> exchange(String url, org.springframework.http.HttpMethod method, org.springframework.http.HttpEntity<?> requestEntity, Class<T> responseType) {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }
}
