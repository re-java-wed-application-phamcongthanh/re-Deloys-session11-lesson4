package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DemoApplicationTests {

    @Test
    @DisplayName("Kiểm tra Spring Application Context load thành công")
    void contextLoads() {
        assertTrue(true, "Application context must load successfully");
    }

    @Test
    @DisplayName("Unit Test mẫu kiểm tra logic tính toán")
    void sampleUnitTest() {
        int expected = 42;
        int actual = 20 + 22;
        assertEquals(expected, actual, "Math calculation should match expected value");
    }
}
