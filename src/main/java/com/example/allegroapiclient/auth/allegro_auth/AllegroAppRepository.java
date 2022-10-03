package com.example.allegroapiclient.auth.allegro_auth;

import com.example.allegroapiclient.auth.entities.AllegroApp;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT * FROM ALLEGRO_APP WHERE ENDPOINT = :endpoint")
    Optional<AllegroApp> findByEndpoint(@Param(("endpoint")) String endpoint);

    @Query("SELECT COUNT(CLIENT_ID) FROM ALLEGRO_APP WHERE USERNAME = :username")
    int countByUsername(@Param("username") String username);
}
