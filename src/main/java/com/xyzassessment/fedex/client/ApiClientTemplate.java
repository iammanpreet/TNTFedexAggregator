package com.xyzassessment.fedex.client;

import java.util.List;

import com.xyzassessment.fedex.exception.GlobalException;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.xyzassessment.fedex.exception.ApiException;

public abstract class ApiClientTemplate<T> {

    private final String url;
    private final RestTemplate restTemplate;

    public ApiClientTemplate(RestTemplate restTemplate, String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    protected abstract Logger getLogger();

    protected abstract ParameterizedTypeReference<T> getResponseType();

    protected String buildUrl(List<String> parameters) {
        return String.format(getUrl() + "?q=" + String.join(",", parameters));
    }

    protected HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    protected String getUrl() {
        return this.url;
    }

    public T callApi(List<String> parameters) {
        String apiUrl = buildUrl(parameters);
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    apiUrl,
                    getHttpMethod(),
                    null,
                    getResponseType()
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                GlobalException.handleHttpStatusError(getLogger(), response.getStatusCode(), apiUrl);
                throw new ApiException("API {} call failed with status code: {} ", apiUrl, response.getStatusCode());
            }
        } catch (ApiException e) {
            GlobalException.handleApiException(getLogger(), apiUrl, e);
            throw e;
        } catch (HttpClientErrorException e) {
            GlobalException.handleHttpClientError(getLogger(), e.getStatusCode(), apiUrl);
            throw e;
        } catch (Exception e) {
            GlobalException.handleGenericException(getLogger(), apiUrl, e);
            throw e;
        }
    }
}
