package com.example.allegroapiclient;

import com.example.allegroapiclient.api_client.utils.OfferBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OfferBuilderTests {
    @Test
    void name(){
        assert OfferBuilder.get("test-name").build().toString().equals("{\"name\":\"test-name\"}");
        assert OfferBuilder.get().name("test-name").build().toString().equals("{\"name\":\"test-name\"}");
    }

    @Test
    void buildingDescription(){
        assert OfferBuilder.get("test-desc")
                .descriptionTextItem("aaa")
                .singleDescriptionTextItem("bbbb")
                .descriptionImageItem("url")
                .descriptionTextItem("ccc")
                .build().toString()
                .equals("{\"name\":\"test-desc\",\"description\":{\"sections\":[{\"items\":[{\"type\":\"TEXT\"," +
                        "\"content\":\"aaa\"}]},{\"items\":[{\"type\":\"TEXT\",\"content\":\"bbbb\"},{\"type\":" +
                        "\"IMAGE\",\"url\":\"url\"}]},{\"items\":[{\"type\":\"TEXT\",\"content\":\"ccc\"}]}]}}\n");
    }


}
