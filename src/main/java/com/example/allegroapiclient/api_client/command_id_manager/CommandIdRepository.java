package com.example.allegroapiclient.api_client.command_id_manager;


import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandIdRepository extends CrudRepository<CommandId, String> {
    @Override
    <S extends CommandId> S save(S entity);

    @Override
    boolean existsById(String s);

    @Override
    Optional<CommandId> findById(String s);

    @Override
    void deleteAll(Iterable<? extends CommandId> entities);

    @Override
    <S extends CommandId> Iterable<S> saveAll(Iterable<S> entities);

    @Query("SELECT * FROM COMMAND_ID WHERE STATUS='NEW'")
    List<CommandId> findIdsWithNewStatus();

    @Override
    Iterable<CommandId> findAllById(Iterable<String> strings);
}
