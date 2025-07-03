package com.example.easyplan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest; // DataJpaTest 사용한다면, 아니면 삭제
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
// 이 어노테이션을 추가하거나 수정합니다.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EasyPlanApplicationTests {

    @Test
    void contextLoads() {
        // 이 테스트는 애플리케이션 컨텍스트가 성공적으로 로드되는지 확인합니다.
    }

}