package com.example.allegroapiclient.api_client.command_id_manager;

import com.example.allegroapiclient.api_client.offer.OffersModificationAllegroApiDao;
import com.example.allegroapiclient.api_client.utils.BasicUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommandIdService {
    private final CommandIdRepository repository;
    private final OffersModificationAllegroApiDao modificationsDao;
    private final Logger logger = LoggerFactory.getLogger(CommandIdService.class);

    @Autowired
    public CommandIdService(CommandIdRepository repository, OffersModificationAllegroApiDao modificationsDao) {
        this.repository = repository;
        this.modificationsDao = modificationsDao;
    }

    public String generateCommandId(String username, Type type){
        String uuid = generate();

        while (!isUnique(uuid)){
            uuid = generate();
        }

        CommandId newCommandId = new CommandId(uuid, username, new Date(), Status.CREATED, type, true);
        repository.save(newCommandId);

        return uuid;
    }

    private String generate(){return UUID.randomUUID().toString();}

    private boolean isUnique(String uuid){return !repository.existsById(uuid);}

    public List<String> updateStatuses(String username, Token token){
        logger.info(String.format("Update asynchronous requests statuses with command id. For user: %s", username));
        List<CommandId> updatedIds = getProcessingIds(username).stream()
                .filter(id -> updateSingleId(id, token))
                .collect(Collectors.toList());

        repository.saveAll(updatedIds);
        return updatedIds.stream().map(CommandId::getUuid).collect(Collectors.toList());
    }

    private boolean updateSingleId(CommandId id, Token token){
        boolean isUpdated = false;
        try{
            JSONObject taskCount = modificationsDao.commandSummary(id, token).getJSONObject("taskCount");
            int total = taskCount.getInt("total");
            int failed = taskCount.getInt("failed");
            int success = taskCount.getInt("success");

            if(total == success){
                id.setStatus(Status.SUCCESS);
                isUpdated = true;
            }
            else if(total == failed){
                id.setStatus(Status.FAIL);
                isUpdated = true;
            }
            else if(total == failed + success){
                id.setStatus(Status.PARTLY_SUCCESS);
                isUpdated = true;
            }
            else if(total > failed + success){
                id.setStatus(Status.NEW);
                isUpdated = true;
            }
        }catch (HttpClientErrorException exception){
            if(exception.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                // if uuid is not found on Allegro DB and older than 12h
                // then delete from our DB
                if(!id.getStatus().equals(Status.CREATED))
                    repository.deleteById(id.getUuid());
                else if(id.getStatus().equals(Status.CREATED)
                        && id.getCreated().before(BasicUtils.getCurrentTimeSubHours(12)))
                    repository.deleteById(id.getUuid());
            }else{
                //TODO: save log
            }
        }
        return isUpdated;
    }

    private List<CommandId> getProcessingIds(String username){
        return repository.findNotFinishedRequests(username);
    }
}
