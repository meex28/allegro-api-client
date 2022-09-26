package com.example.allegroapiclient;

import com.example.allegroapiclient.allegro_auth.AllegroAuthApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AllegroApiClientApplicationTests {
    @Autowired
    AllegroAuthApiService allegroAuthApiService;

    @Test
    void contextLoads() {
    }
}
