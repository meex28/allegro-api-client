package com.example.allegroapiclient.auth.allegro_auth;

import com.example.allegroapiclient.auth.dto.AllegroAppDTO;
import com.example.allegroapiclient.auth.dto.Token;
import com.example.allegroapiclient.auth.exceptions.InvalidClientIdException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/apps")
public class AllegroAppController {
    private final AllegroAppService service;
    private final Logger logger = LoggerFactory.getLogger(AllegroAppController.class);
    @Autowired
    public AllegroAppController(AllegroAppService service) {
        this.service = service;
    }

    @GetMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getToken(@RequestParam String clientId){
        logger.info(String.format("Client (id: %s) requested token.", clientId));
        try{
            Token token = service.getToken(clientId);
            JSONObject responseBody = new JSONObject(token.toStringJSON());
            return ResponseEntity.ok(responseBody.toString());
        }catch (InvalidClientIdException e){
            JSONObject responseBody = new JSONObject()
                    .put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody.toString());
        }
    }

    @GetMapping(value = "/endpoint", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getEndpoint(@RequestParam String username){
        logger.info(String.format("Requested endpoint for user: %s", username));
        String endpoint = service.generateEndpoint(username);
        JSONObject responseBody = new JSONObject();
        responseBody.put("endpoint", endpoint);
        return responseBody.toString();
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> addNewApp(@RequestBody AllegroAppDTO app){
        logger.info(String.format("Client requested to add new app: %s", app));
        try{
            service.addNewApp(app.getClientId(),
                    app.getClientSecret(),
                    app.isSandbox(),
                    app.getUsername(),
                    app.getEndpoint(),
                    app.getAuthFlowType());

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
        logger.info(String.format("Client (id: %s) requested token generation.", clientId));
        String url;
        try{
            service.generateTokenForApplication(clientId);
            url = service.getAuthURL(clientId);
        }catch (InvalidClientIdException e){
            logger.info(String.format("Client (id: %s) requested token generation - invalid id.", clientId));
            JSONObject responseBody = new JSONObject()
                    .put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody.toString());
        }

        JSONObject responseBody = new JSONObject()
                .put("authorizeUrl", url);
        return ResponseEntity.ok(responseBody.toString());
    }
}
