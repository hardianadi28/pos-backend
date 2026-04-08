package com.retail.pos.core.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraceIdFilterTest {

    private TraceIdFilter traceIdFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        traceIdFilter = new TraceIdFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldGenerateTraceIdWhenHeaderIsMissing() throws ServletException, IOException {
        when(request.getHeader("X-Trace-Id")).thenReturn(null);

        doAnswer(invocation -> {
            String traceId = MDC.get("trace_id");
            assertNotNull(traceId, "Trace ID should be in MDC");
            assertFalse(traceId.isEmpty());
            return null;
        }).when(filterChain).doFilter(request, response);

        traceIdFilter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq("X-Trace-Id"), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReuseTraceIdWhenHeaderIsPresent() throws ServletException, IOException {
        String existingTraceId = "test-trace-id";
        when(request.getHeader("X-Trace-Id")).thenReturn(existingTraceId);

        doAnswer(invocation -> {
            assertEquals(existingTraceId, MDC.get("trace_id"), "Trace ID in MDC should match header");
            return null;
        }).when(filterChain).doFilter(request, response);

        traceIdFilter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader("X-Trace-Id", existingTraceId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldClearMdcAfterRequest() throws ServletException, IOException {
        traceIdFilter.doFilterInternal(request, response, filterChain);
        assertNull(MDC.get("trace_id"));
    }
}
