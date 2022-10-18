package com.example.allegroapiclient.auth.allegro_auth.token_generation;

import com.example.allegroapiclient.auth.allegro_auth.AllegroAppRepository;
import com.example.allegroapiclient.auth.allegro_auth.AllegroAuthApiService;
import com.example.allegroapiclient.auth.entities.AllegroApp;
import com.example.allegroapiclient.auth.exceptions.DeviceFlowTokenPending;
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
        logger.info(String.format("Initialize token generation (device flow) for app: %s", app));
        return authApiService.prepareGenerationTokenForUserWithDeviceFlow(
                app.getClientId(), app.getClientSecret(), app.isSandbox());
    }

    @Async
    public void generate(AllegroApp app, String deviceCode, int interval){
        logger.info(String.format(
                "Started requests to get token for user (device flow). Device code: %s. App: %s", deviceCode, app));
        boolean isAuthPending = true;

        while (isAuthPending){
            try {
                JSONObject response = authApiService.requestForTokenForUserDeviceFlow(app.getClientId(),
                        app.getClientSecret(),
                        app.isSandbox(),
                        deviceCode);

                logger.info(String.format("Successfully generated token for user (device flow) for app: %s", app));
                String tokenForUser = response.getString("access_token");
                String refreshToken = response.getString("refresh_token");
                app.setTokenForUser(tokenForUser);
                app.setRefreshToken(refreshToken);
                repository.save(app);
                isAuthPending = false;
            }catch (DeviceFlowTokenPending e){
                String error = e.getError();
                logger.info(String.format("Token pending for app: %s. Response: %s", app, error));
                try{
                    TimeUnit.SECONDS.sleep(interval);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
