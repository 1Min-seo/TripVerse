package com.example.easyplan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.easyplan.service.GeminiService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class EasyPlanApplicationTests {

    @MockBean
    private GeminiService geminiService;

    @Test
    void contextLoads() {
    }

}
