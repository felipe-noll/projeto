package com.example.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
    "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
    "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
    "spring.mail.host=localhost",
    "spring.mail.port=3025"
})
class AuthApplicationTests {
    @Test
    void contextLoads() {
    }
}
