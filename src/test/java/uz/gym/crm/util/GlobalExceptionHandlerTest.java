package uz.gym.crm.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleValidationExceptions() {

        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        FieldError fieldError = new FieldError("objectName", "field", "Field must not be empty");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        when(exception.getBindingResult()).thenReturn(bindingResult);


        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Field must not be empty", response.getBody().get("field"));
    }

    @Test
    void testHandleIllegalArgumentException() {

        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");


        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleIllegalArgumentException(exception);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument provided", response.getBody().get("error"));
    }

    @Test
    void testHandleGeneralExceptions() {

        Exception exception = new Exception("Unexpected error");


        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGeneralExceptions(exception);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred. Please contact support if the problem persists.", response.getBody().get("error"));
    }
}
