package com.codenames.codenames_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"app.allowed-origins=http://localhost:8080,http://10.0.2.2:8080"})
class CodenamesBackendApplicationTests {

  @Test
  void contextLoads() {}
}
