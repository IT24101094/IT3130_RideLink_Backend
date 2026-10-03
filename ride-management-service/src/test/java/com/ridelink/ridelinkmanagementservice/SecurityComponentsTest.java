package com.ridelink.ridelinkmanagementservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.ridelinkmanagementservice.security.CustomAccessDeniedHandler;
import com.ridelink.ridelinkmanagementservice.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SecurityComponentsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void authenticationEntryPoint_Writes401Json() throws Exception {
        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Bad token"));

        assertEquals(401, response.getStatus());
        JsonNode json = objectMapper.readTree(response.getContentAsString());
        assertEquals(401, json.get("status").asInt());
        assertEquals("Unauthorized", json.get("error").asText());
        assertEquals("Bad token", json.get("message").asText());
        assertNotNull(json.get("timestamp"));
    }

    @Test
    void accessDeniedHandler_Writes403Json() throws Exception {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Forbidden action"));

        assertEquals(403, response.getStatus());
        JsonNode json = objectMapper.readTree(response.getContentAsString());
        assertEquals(403, json.get("status").asInt());
        assertEquals("Forbidden", json.get("error").asText());
        assertEquals("Forbidden action", json.get("message").asText());
        assertNotNull(json.get("timestamp"));
    }
}
