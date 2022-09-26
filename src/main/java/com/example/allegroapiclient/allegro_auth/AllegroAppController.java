package com.example.allegroapiclient.allegro_auth;

import com.example.allegroapiclient.dto.AllegroAppDTO;
import com.example.allegroapiclient.exceptions.InvalidClientIdException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apps")
public class AllegroAppController {
    private final AllegroAppService service;

    @Autowired
    public AllegroAppController(AllegroAppService service) {
        this.service = service;
    }

    @GetMapping(value = "/endpoint", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getEndpoint(@RequestParam String username){
        String endpoint = service.generateEndpoint(username);
        JSONObject responseBody = new JSONObject();
        responseBody.put("endpoint", endpoint);
        return responseBody.toString();
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> addNewApp(@RequestBody AllegroAppDTO app){
        try{
            service.addNewApp(app.getClientId(),
                    app.getClientSecret(),
                    app.isSandbox(),
                    app.getUsername(),
                    app.getEndpoint());

            JSONObject responseBody = new JSONObject()
                    .put("id", app.getClientId());
            return ResponseEntity.ok(responseBody.toString());
        }catch (InvalidClientIdException e){
            JSONObject responseBody = new JSONObject()
                    .put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody.toString());
        }
    }

    @PutMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> generateTokens(@RequestParam String clientId){
        String url;
        try{
            service.generateTokenForApplication(clientId);
            url = service.getAccessCodeUrl(clientId);
        }catch (InvalidClientIdException e){
            JSONObject responseBody = new JSONObject()
                    .put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody.toString());
        }

        JSONObject responseBody = new JSONObject()
                .put("authorizeUrl", url);
        return ResponseEntity.ok(responseBody.toString());
    }
}
