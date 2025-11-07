package com.example.instructions.controller;

import com.example.instructions.service.TradeService;
import com.example.instructions.store.InMemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeControllerTest {

    private TradeService tradeService;
    private InMemoryStore store;
    private TradeController controller;

    @BeforeEach
    void setUp() {
        tradeService = mock(TradeService.class);
        store = mock(InMemoryStore.class);
        controller = new TradeController(tradeService, store);
    }

    @Test
    void testUpload_NullFileName_ShouldReturnInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", null, "text/csv", "data".getBytes());
        ResponseEntity<?> response = controller.upload(file);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid file", response.getBody());
    }

    @Test
    void testUpload_BlankFileName_ShouldReturnInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "", "text/csv", "data".getBytes());
        ResponseEntity<?> response = controller.upload(file);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid file", response.getBody());
    }

    @Test
    void testUpload_UnsupportedFileType_ShouldReturnUnsupportedFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());
        ResponseEntity<?> response = controller.upload(file);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Unsupported file type", response.getBody());
    }

    @Test
    void testUpload_ProcessingError_ShouldReturnErrorMessage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        when(tradeService.processData(file)).thenThrow(new RuntimeException("Failed to process"));
        ResponseEntity<?> response = controller.upload(file);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((String) response.getBody()).contains("Processing error: Failed to process"));
    }

    @Test
    void testUpload_Success_ShouldReturnProcessedCount() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        when(tradeService.processData(file)).thenReturn(5);
        ResponseEntity<?> response = controller.upload(file);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("processed"));
        assertEquals(5, ((Map<?, ?>) response.getBody()).get("processed"));
    }
}
