package com.example.allegroapiclient;

import com.example.allegroapiclient.api_client.command_id_manager.*;
import com.example.allegroapiclient.api_client.offer.OffersModificationAllegroApiDao;
import com.example.allegroapiclient.api_client.utils.OfferModificationBuilder;
import com.example.allegroapiclient.auth.allegro_auth.AllegroAppService;
import com.example.allegroapiclient.auth.dto.Token;
import com.example.allegroapiclient.auth.exceptions.InvalidClientIdException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class CommandIdAndBatchModTests {
    @Autowired
    CommandIdService commandIdService;

    @Autowired
    CommandIdRepository commandIdRepository;

    @Autowired
    AllegroAppService allegroAppService;

    @Autowired
    OffersModificationAllegroApiDao offersModificationAllegroApiDao;

    @Test
    public String generate(){
        String uuid = commandIdService.generateCommandId(TestTokens.clientId, Type.MODIFICATION);
        Optional<CommandId> commandIdOptional = commandIdRepository.findById(uuid);
        assert commandIdOptional.isPresent();

        CommandId commandId = commandIdOptional.get();
        System.out.println(commandId);
        return commandId.getUuid();
    }

    @Test
    public void modifyPrice() throws InvalidClientIdException, JSONException {
        String uuid = commandIdService.generateCommandId(TestTokens.username, Type.PRICE);
        Token token = allegroAppService.getToken(TestTokens.clientId);
        List<String> offers = List.of("7693396259", "7692033855");

        String returnedUuid = offersModificationAllegroApiDao.increaseOffersPrice(uuid, "10", "PLN",
                offers, token);

        System.out.println(offersModificationAllegroApiDao.changePriceCommandSummary(returnedUuid, token)
                .toString(2));
        System.out.println(offersModificationAllegroApiDao.changePriceDetailedReport(returnedUuid, token)
                .toString(2));
    }

    @Test
    public void modifyQuantity() throws InvalidClientIdException, JSONException{
        String uuid = commandIdService.generateCommandId(TestTokens.username, Type.QUANTITY);
        Token token = allegroAppService.getToken(TestTokens.clientId);
        List<String> offers = List.of("7693396259", "7692033855");

        String returnedUuid = offersModificationAllegroApiDao.modifyOffersGainQuantity(uuid, "10", offers, token);

        System.out.println(offersModificationAllegroApiDao.changeQuantityCommandSummary(returnedUuid, token)
                .toString(2));
        System.out.println(offersModificationAllegroApiDao.changeQuantityDetailedReport(returnedUuid, token)
                .toString(2));
    }

    @Test
    public void modifyOffer() throws InvalidClientIdException, JSONException{
        String uuid = commandIdService.generateCommandId(TestTokens.username, Type.PUBLICATION);
        Token token = allegroAppService.getToken(TestTokens.clientId);
        List<String> offers = List.of("7693396259", "7692033855");
        JSONObject modification = OfferModificationBuilder.get()
                .promotionBold(true)
                .build();

        String returnedUuid = offersModificationAllegroApiDao.modifyOffers(uuid, modification, offers, token);

        System.out.println(offersModificationAllegroApiDao.modificationCommandSummary(returnedUuid, token)
                .toString(2));
        System.out.println(offersModificationAllegroApiDao.modificationCommandDetailedReport(returnedUuid, token)
                .toString(2));
    }

    @Test
    public void getReportModifyOffer() throws InvalidClientIdException, JSONException{
        String uuid = "89a4aa6d-3340-4084-844d-6ea9017e621a";
        Token token = allegroAppService.getToken(TestTokens.clientId);
        System.out.println(offersModificationAllegroApiDao.modificationCommandDetailedReport(uuid, token).toString(2));
    }

    @Test
    public void getProcessingIds() throws InvalidClientIdException{
        Token token = allegroAppService.getToken(TestTokens.clientId);
        List<CommandId> ids = List.of(
                new CommandId(UUID.randomUUID().toString(), TestTokens.username, new Date(),
                        Status.CREATED, Type.PUBLICATION, true),
                new CommandId(UUID.randomUUID().toString(), TestTokens.username, new Date(),
                        Status.NEW, Type.PRICE, true),
                new CommandId(UUID.randomUUID().toString(), TestTokens.username, new Date(),
                        Status.NEW, Type.QUANTITY, true)
                );

        commandIdRepository.saveAll(ids);

        List<CommandId> processingIds = commandIdRepository.findIdsWithNewStatus();

        processingIds.forEach(System.out::println);
    }

    @Test
    public void getReportSummary() throws InvalidClientIdException, InterruptedException, JSONException {
        Token token = allegroAppService.getToken(TestTokens.clientId);
        String uuid = commandIdService.generateCommandId(token.username(), Type.PRICE);
        List<String> offers = List.of("7693396259", "7692033855");

        offersModificationAllegroApiDao.modifyOffersFixedPrice(uuid, "200", "PLN", offers, token);

        Thread.sleep(5000);
        Optional<CommandId> commandIdOptional = commandIdRepository.findById(uuid);
        assert commandIdOptional.isPresent();
        CommandId commandId = commandIdOptional.get();
        System.out.println(offersModificationAllegroApiDao.commandSummary(commandId, token).toString(2));
        System.out.println(offersModificationAllegroApiDao.commandReport(commandId, token).toString(2));
    }

    @Test
    public void updateStatuses() throws InvalidClientIdException, InterruptedException {
        Token token = allegroAppService.getToken(TestTokens.clientId);
        List<String> offersAll = List.of("7693396259", "7692033855", "7692030180");
        List<String> offers1 = List.of("7693396259", "7692030180");
        List<String> offers2 = List.of("7693396259", "7692033855");

        String uuid = commandIdService.generateCommandId(token.username(), Type.PRICE);
        offersModificationAllegroApiDao.increaseOffersPrice(uuid, "10", "PLN", offersAll, token);

        uuid = commandIdService.generateCommandId(token.username(), Type.PRICE);
        offersModificationAllegroApiDao.increaseOffersPrice(uuid, "300", "PLN", offers2, token);

        uuid = commandIdService.generateCommandId(token.username(), Type.QUANTITY);
        offersModificationAllegroApiDao.modifyOffersGainQuantity(uuid, "10", offers1, token);

        Thread.sleep(5000);

        commandIdService.updateStatuses(token.username(), token);
    }
}
