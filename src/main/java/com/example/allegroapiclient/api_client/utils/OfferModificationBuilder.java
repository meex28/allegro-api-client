package com.example.allegroapiclient.api_client.utils;

import org.json.JSONObject;

public class OfferModificationBuilder {
    private final JSONObject modification;

    public OfferModificationBuilder(){
        this.modification = new JSONObject();
    }

    public OfferModificationBuilder(JSONObject modification) {
        this.modification = modification;
    }

    public OfferModificationBuilder(String modification){
        this.modification = new JSONObject(modification);
    }

    public JSONObject build(){return modification;}

    public static OfferModificationBuilder get(){
        return new OfferModificationBuilder();
    }

    public OfferModificationBuilder additionalServiceGroup(String id){
        modification.put("additionalServicesGroup", new JSONObject().put("id", id));
        return this;
    }

    public OfferModificationBuilder delivery(String id){
        modification.put("delivery", new JSONObject()
                .put("shippingRates", new JSONObject().put("id", id)));
        return this;
    }

    public OfferModificationBuilder discounts(String id){
        modification.put("discounts", new JSONObject()
                .put("wholesalePriceList", new JSONObject().put("id", id)));
        return this;
    }

    public OfferModificationBuilder location(String city, String countryCode, String postCode, String province){
        modification.put("location", new JSONObject()
                .put("city", city)
                .put("countryCode", countryCode)
                .put("postCode", postCode)
                .put("province", province));
        return this;
    }

    public OfferModificationBuilder payments(Invoice invoice, String taxPercentage){
        modification.put("payments", new JSONObject()
                .put("invoice", invoice.toString())
                .put("tax", new JSONObject().put("percentage", taxPercentage)));
        return this;
    }

    private OfferModificationBuilder addPromotion(String type, boolean value){
        if(modification.isNull("promotion"))
            modification.put("promotion", new JSONObject());
        modification.getJSONObject("promotion").put(type, value);
        return this;
    }

    public OfferModificationBuilder promotionBold(boolean isPromotion){
        return addPromotion("bold", isPromotion);
    }

    public OfferModificationBuilder promotionDepartmentPage(boolean isPromotion){
        return addPromotion("departmentPage", isPromotion);
    }

    public OfferModificationBuilder promotionEmphasized(boolean isPromotion){
        return addPromotion("emphasized", isPromotion);
    }

    public OfferModificationBuilder promotionEmphasizedHighlightBoldPackage(boolean isPromotion){
        return addPromotion("emphasizedHighlightBoldPackage", isPromotion);
    }

    public OfferModificationBuilder promotionHighlight(boolean isPromotion){
        return addPromotion("highlight", isPromotion);
    }

    public OfferModificationBuilder sizeTable(String id){
        modification.put("sizeTable", new JSONObject().put("id", id));
        return this;
    }

    public OfferModificationBuilder publication(String duration, boolean durationUnlimited){
        modification.put("publication", new JSONObject()
                .put("duration", duration)
                .put("durationUnlimited", durationUnlimited));
        return this;
    }

    public OfferModificationBuilder publication(boolean durationUnlimited){
        modification.put("publication", new JSONObject()
                .put("durationUnlimited", durationUnlimited));
        return this;
    }
}
