package com.fedex.client;

import java.util.List;

import com.fedex.exception.GlobalException;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fedex.exception.ApiException;

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

    /**
     * All the external clients and extended by this ApiTemplate so that the generic behaviour of the client remains here
     * And step by step execution will happen for all the external clients.
     * It becomes easier to add new client by just extending this template and override the required methods.
     * */
    public T callApi(List<String> parameters) {
        //build url to invoke external client
        String apiUrl = buildUrl(parameters);
        try {
            // Trigger the rest template call
            ResponseEntity<T> response = restTemplate.exchange(
                    apiUrl,
                    getHttpMethod(),
                    null,
                    getResponseType()
            );
            //check response code
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {

                // if response if not successful, then throw custom exception
                GlobalException.handleHttpStatusError(getLogger(), response.getStatusCode(), apiUrl);
                throw new ApiException("API {} call failed with status code: {} ", apiUrl, response.getStatusCode());
            }
        } catch (ApiException e) {
            //Global Exception handler handling the api exception
            GlobalException.handleApiException(getLogger(), apiUrl, e);
            throw e;
        } catch (HttpClientErrorException e) {
            //Global Exception handler handling the HttpClientErrorException
            GlobalException.handleHttpClientError(getLogger(), e.getStatusCode(), apiUrl);
            throw e;
        } catch (Exception e) {
            //Global Exception handler handling the rest of the exception
            GlobalException.handleGenericException(getLogger(), apiUrl, e);
            throw e;
        }
    }
}
