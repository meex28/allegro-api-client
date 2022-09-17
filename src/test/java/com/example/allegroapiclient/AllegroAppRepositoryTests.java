package com.example.allegroapiclient;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AllegroAppRepositoryTests {
    @Autowired
    AllegroAppRepository repository;

    @BeforeAll
    public void clearDatabase(){
        repository.deleteAll();
    }

    @Test
    public void saveEntity(){
        String id = "saving-id";
        AllegroApp app = new AllegroApp(id, "secret", false, "username");
        app.setNew(true);
        repository.save(app);
        entityPresent(id);
    }

    @Test
    public void findById(){
        String id = "find-id";
        AllegroApp app = new AllegroApp(id, "secret", false, "username");
        app.setNew(true);
        repository.save(app);
        Optional<AllegroApp> findApp = repository.findById(id);
        assert findApp.isPresent();
    }

    @Test
    public void updateEntity(){
        String id = "update-id";
        AllegroApp newApp = new AllegroApp(id, "secret", false, "username");
        newApp.setNew(true);
        repository.save(newApp);
        Optional<AllegroApp> app = repository.findById(id);
        assert app.isPresent();
        app.get().setClientSecret("updated-secret");
        repository.save(app.get());
        Optional<AllegroApp> updatedApp = repository.findById(id);
        assert updatedApp.isPresent();
        assert updatedApp.get().getClientSecret().equals("updated-secret");
    }

    @Test
    public void deleteEntity(){
        String id = "delete-id";
        AllegroApp app = new AllegroApp(id, "secret", false, "username");
        app.setNew(true);
        repository.save(app);
        entityPresent(id);
        repository.deleteById(id);
        entityNotPresent(id);
    }

    @Test
    public void findByEndpoint(){
        String id = "endpoint-id";
        AllegroApp app = new AllegroApp(id, "secret", false, "username");
        String endpoint = "test-endpoint";
        app.setEndpoint(endpoint);
        app.setNew(true);
        repository.save(app);
        entityPresent(id);
        Optional<AllegroApp> foundApp = repository.findByEndpoint(endpoint);
        assert foundApp.isPresent();
        assert foundApp.get().getEndpoint().equals(endpoint);
    }

    @Test
    void countByUsername(){
        AllegroApp app1 = new AllegroApp();
        app1.setClientId("1");
        app1.setUsername("user1");
        app1.setNew(true);
        AllegroApp app2 = new AllegroApp();
        app2.setClientId("2");
        app2.setUsername("user1");
        app2.setNew(true);
        AllegroApp app3 = new AllegroApp();
        app3.setClientId("3");
        app3.setUsername("user2");
        app3.setNew(true);
        repository.save(app1);
        repository.save(app2);
        repository.save(app3);

        assert repository.countByUsername("user1") == 2;
        assert repository.countByUsername("user2") == 1;
        assert repository.countByUsername("user") == 0;
    }

    public void entityNotPresent(String id){
        Optional<AllegroApp> findByIdApp = repository.findById(id);
        assert findByIdApp.isEmpty();
    }

    public void entityPresent(String id){
        Optional<AllegroApp> findByIdApp = repository.findById(id);
        assert findByIdApp.isPresent();
        assert findByIdApp.get().getClientId().equals(id);
    }
}
