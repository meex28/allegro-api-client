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
