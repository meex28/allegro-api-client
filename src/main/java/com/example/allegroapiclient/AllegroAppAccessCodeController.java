package com.example.allegroapiclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apps/code")
public class AllegroAppAccessCodeController {
    private final AllegroAppService service;

    @Autowired
    public AllegroAppAccessCodeController(AllegroAppService service) {
        this.service = service;
    }

    @GetMapping("/{endpoint}")
    public void passCode(@RequestParam String code, @PathVariable String endpoint){
        System.out.println(endpoint);
        System.out.println(code);
    }

}
