package com.example.allegroapiclient.api_client.command_id_manager;

import com.example.allegroapiclient.api_client.offer.OffersModificationAllegroApiDao;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
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
        List<CommandId> updatedIds = getProcessingIds()
                .stream()
                .filter(id -> id.getUsername().equals(username))
                .filter(id -> !id.getStatus().equals(Status.FAIL)
                        && !id.getStatus().equals(Status.SUCCESS)
                        && !id.getStatus().equals(Status.PARTLY_SUCCESS))
                .map(id -> new CommandId[]{id, updateSingleId(id, token)})
                .filter(ids -> !ids[0].getStatus().equals(ids[1].getStatus()))
                .map(ids -> ids[1])
                .collect(Collectors.toList());

        repository.saveAll(updatedIds);
        return updatedIds.stream().map(CommandId::getUuid).collect(Collectors.toList());
    }

    private CommandId updateSingleId(CommandId id, Token token){
        try{
            JSONObject taskCount = modificationsDao.commandSummary(id, token).getJSONObject("taskCount");
            int total = taskCount.getInt("total");
            int failed = taskCount.getInt("failed");
            int success = taskCount.getInt("success");

            if(total == success)
                id.setStatus(Status.SUCCESS);
            else if(total == failed)
                id.setStatus(Status.FAIL);
            else if(total == failed + success)
                id.setStatus(Status.PARTLY_SUCCESS);
            else if(total > failed + success)
                id.setStatus(Status.NEW);
        }catch (HttpClientErrorException exception){
            if(exception.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                // if uuid is not found on Allegro DB then delete from our DB
                if(!id.getStatus().equals(Status.CREATED))
                    repository.deleteById(id.getUuid());
            }else{
                //TODO: save log
            }
        }
        return id;
    }

    private List<CommandId> getProcessingIds(){
        return repository.findIdsWithNewStatus();
    }
}
