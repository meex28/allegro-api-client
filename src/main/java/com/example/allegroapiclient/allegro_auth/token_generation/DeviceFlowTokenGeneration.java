package com.example.allegroapiclient.allegro_auth.token_generation;

import com.example.allegroapiclient.allegro_auth.AllegroAppRepository;
import com.example.allegroapiclient.allegro_auth.AllegroAuthApiService;
import com.example.allegroapiclient.entities.AllegroApp;
import com.example.allegroapiclient.exceptions.DeviceFlowTokenPending;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DeviceFlowTokenGeneration {
    private final AllegroAuthApiService authApiService;
    private final AllegroAppRepository repository;
    private final Logger logger = LoggerFactory.getLogger(DeviceFlowTokenGeneration.class);

    @Autowired
    public DeviceFlowTokenGeneration(AllegroAuthApiService authApiService, AllegroAppRepository allegroAppRepository) {
        this.authApiService = authApiService;
        this.repository = allegroAppRepository;
    }

    public JSONObject initializeTokenGeneration(AllegroApp app){
        return authApiService.prepareGenerationTokenForUserWithDeviceFlow(
                app.getClientId(), app.getClientSecret(), app.isSandbox());
    }

    @Async
    public void generate(AllegroApp app, String deviceCode, int interval){
        boolean isAuthPending = true;

        while (isAuthPending){
            try {
                JSONObject response = authApiService.requestForTokenForUserDeviceFlow(app.getClientId(),
                        app.getClientSecret(),
                        app.isSandbox(),
                        deviceCode);

                String tokenForUser = response.getString("access_token");
                String refreshToken = response.getString("refresh_token");
                app.setTokenForUser(tokenForUser);
                app.setRefreshToken(refreshToken);
                repository.save(app);
                isAuthPending = false;
            }catch (DeviceFlowTokenPending e){
                String error = e.getError();
                logger.info("Token pending: "+error);
                try{
                    TimeUnit.SECONDS.sleep(interval);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
