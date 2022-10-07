package com.example.allegroapiclient;

import com.example.allegroapiclient.api_client.utils.OfferBuilder;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

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

    @Test
    void productSet(){
        JSONObject offer = OfferBuilder.get("test")
                .addProduct("product", "1", "2", OfferBuilder.ProductIdType.GTIN,
                        Collections.singletonList("image"), 2)
                .addProductParameterRangeValue("2", "p1", "pn1", "1", "10")
                .addProductParameterValues("2", "p2", "pn2", Collections.singletonList("3"))
                .addProductParameterValuesIds("2", "p3", "pn3", Collections.singletonList("4"))
                .build();
        System.out.println(offer);
    }
}
