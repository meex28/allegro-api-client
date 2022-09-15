package com.example.allegroapiclient;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllegroAppRepository extends CrudRepository<AllegroApp, String> {
    @Override
    <S extends AllegroApp> S save(S entity);

    @Override
    Optional<AllegroApp> findById(String s);

    @Override
    void deleteById(String s);

    @Override
    void deleteAll();
}
