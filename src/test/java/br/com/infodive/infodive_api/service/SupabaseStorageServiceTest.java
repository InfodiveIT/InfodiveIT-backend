package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SupabaseStorageServiceTest {

    private SupabaseStorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new SupabaseStorageService();
    }

    @Test
    void isConfigured_WhenPropertiesEmpty_ShouldReturnFalse() {
        ReflectionTestUtils.setField(storageService, "supabaseUrl", "");
        ReflectionTestUtils.setField(storageService, "supabaseKey", "");

        assertFalse(storageService.isConfigured());
    }

    @Test
    void isConfigured_WhenPropertiesSet_ShouldReturnTrue() {
        ReflectionTestUtils.setField(storageService, "supabaseUrl", "https://xyz.supabase.co");
        ReflectionTestUtils.setField(storageService, "supabaseKey", "key-test-123");

        assertTrue(storageService.isConfigured());
    }

    @Test
    void uploadFile_WhenNotConfigured_ShouldThrowIllegalStateException() {
        ReflectionTestUtils.setField(storageService, "supabaseUrl", "");
        ReflectionTestUtils.setField(storageService, "supabaseKey", "");

        assertThrows(IllegalStateException.class, () ->
                storageService.uploadFile("test".getBytes(), "test.png", "image/png"));
    }
}
