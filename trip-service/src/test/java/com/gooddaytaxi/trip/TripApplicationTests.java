package com.gooddaytaxi.trip;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TripApplicationTests {

    @Test
    @Disabled("CI 환경에서 DB 연결 불필요")
    void contextLoads() {
    }
}
