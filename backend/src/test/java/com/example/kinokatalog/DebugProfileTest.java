package com.example.kinokatalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

@SpringBootTest
@ActiveProfiles("test")
class DebugProfileTest {

    @Autowired
    Environment env;

    @Test
    void printActiveProfiles() {
        System.out.println("=========== ACTIVE PROFILES ===========");
        System.out.println(Arrays.toString(env.getActiveProfiles()));
        System.out.println("========================================");
    }
}
