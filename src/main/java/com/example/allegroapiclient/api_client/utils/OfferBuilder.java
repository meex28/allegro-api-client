package com.example.allegroapiclient.api_client.utils;

import com.example.allegroapiclient.api_client.exceptions.NoSuchProductException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class OfferBuilder {
    private final JSONObject offer;

    public OfferBuilder() {
        this.offer = new JSONObject();
    }

    public OfferBuilder(JSONObject jsonObject) {
        this.offer = jsonObject;
    }

    public static OfferBuilder get(){
        return new OfferBuilder();
    }

    public static OfferBuilder get(String name){
        OfferBuilder offerBuilder = new OfferBuilder();
        offerBuilder.offer.put("name", name);
        return offerBuilder;
    }

    public static OfferBuilder get(JSONObject json){
        return new OfferBuilder(json);
    }

    public JSONObject build(){
        return offer;
    }

    public OfferBuilder additionalServices(String id){
        JSONObject additionalServices = new JSONObject()
                .put("id", id);
        offer.put("additionalServices", additionalServices);
        return this;
    }

    public OfferBuilder afterSalesServices(String impliedWarranty, String returnPolicy, String warranty){
        return this.impliedWarranty(impliedWarranty)
                .returnPolicy(returnPolicy)
                .warranty(warranty);
    }

    public OfferBuilder impliedWarranty(String id){
        if(offer.isNull("additionalServices"))
            offer.put("additionalServices", new JSONObject());

        offer.getJSONObject("additionalServices")
                .put("impliedWarranty", id);

        return this;
    }

    public OfferBuilder returnPolicy(String id){
        if(offer.isNull("additionalServices"))
            offer.put("additionalServices", new JSONObject());

        offer.getJSONObject("additionalServices")
                .put("returnPolicy", id);

        return this;
    }

    public OfferBuilder warranty(String id){
        if(offer.isNull("additionalServices"))
            offer.put("additionalServices", new JSONObject());

        offer.getJSONObject("additionalServices")
                .put("warranty", id);

        return this;
    }

    public OfferBuilder name(String name){
        offer.put("name", name);
        return this;
    }

    // add single attachment to existing attachments
    public OfferBuilder attachment(String id){
        if(offer.isNull("attachments"))
            offer.put("attachments", new JSONArray());
        offer.append("attachments", new JSONObject().put("id", id));
        return this;
    }

    public OfferBuilder category(String id){
        offer.put("category", new JSONObject().put("id", id));
        return this;
    }

    public OfferBuilder contact(String id){
        offer.put("contact", new JSONObject().put("id", id));
        return this;
    }

    public OfferBuilder customParameter(String name, List<String> values){
        if(offer.isNull("customParameters"))
            offer.put("customParameters", new JSONArray());
        JSONArray jsonArrayValues = new JSONArray(values);
        offer.append("customParameters",
                new JSONObject().put("name", name).put("values", jsonArrayValues));
        return this;
    }

    public OfferBuilder delivery(String additionalInfo, String handlingTime, String shippingRatesId){
        JSONObject delivery = new JSONObject()
                .put("additionalInfo", additionalInfo)
                .put("handlingTime", handlingTime)
                .put("shippingRates", new JSONObject().put("id", shippingRatesId));
        offer.put("delivery", delivery);
        return this;
    }

    public OfferBuilder delivery(String handlingTime, String shippingRatesId){
        JSONObject delivery = new JSONObject()
                .put("handlingTime", handlingTime)
                .put("shippingRates", new JSONObject().put("id", shippingRatesId));
        offer.put("delivery", delivery);
        return this;
    }

    private void addDescription(){
        if(offer.isNull("description")){
            JSONObject sections = new JSONObject().put("sections", new JSONArray());
            offer.put("description", sections);
        }
    }

    private OfferBuilder addDescriptionItem(JSONObject item){
        JSONArray sections = offer.getJSONObject("description").getJSONArray("sections");

        if(sections.length() == 0){
            JSONObject items = new JSONObject().put("items", new JSONArray());
            items.getJSONArray("items").put(item);
            sections.put(items);
        }else{
            JSONArray lastItems = sections.getJSONObject(sections.length()-1).getJSONArray("items");
            if(lastItems.length() == 1){
                lastItems.put(item);
            }else{
                return addSingleDescriptionItem(item);
            }
        }
        return this;
    }

    private OfferBuilder addSingleDescriptionItem(JSONObject item){
        JSONObject items = new JSONObject().put("items", new JSONArray());
        items.getJSONArray("items").put(item);
        offer.getJSONObject("description").getJSONArray("sections").put(items);
        return this;
    }

    public OfferBuilder descriptionTextItem(String content){
        addDescription();
        final String itemTypeText = "TEXT";
        JSONObject item = new JSONObject()
                .put("type", itemTypeText)
                .put("content", content);
        return addDescriptionItem(item);
    }

    public OfferBuilder descriptionImageItem(String url){
        if(offer.isNull("description")){
            JSONObject sections = new JSONObject().put("sections", new JSONArray());
            offer.put("description", sections);
        }

        final String itemTypeImage = "IMAGE";
        JSONObject item = new JSONObject()
                .put("type", itemTypeImage)
                .put("url", url);
        return addDescriptionItem(item);
    }

    public OfferBuilder singleDescriptionTextItem(String content){
        addDescription();
        final String itemTypeText = "TEXT";
        JSONObject item = new JSONObject()
                .put("type", itemTypeText)
                .put("content", content);
        return addSingleDescriptionItem(item);
    }

    public OfferBuilder singleDescriptionImageItem(String url){
        if(offer.isNull("description")){
            JSONObject sections = new JSONObject().put("sections", new JSONArray());
            offer.put("description", sections);
        }

        final String itemTypeImage = "IMAGE";
        JSONObject item = new JSONObject()
                .put("type", itemTypeImage)
                .put("url", url);
        return addSingleDescriptionItem(item);
    }

    public OfferBuilder discounts(String id){
        offer.put("discounts", new JSONObject()
                .put("wholesalePriceList", new JSONObject()
                        .put("id", id)));
        return this;
    }

    public OfferBuilder external(String id){
        offer.put("external", new JSONObject()
                .put("id", id));
        return this;
    }

    public OfferBuilder fundraisingCampaign(String id){
        offer.put("fundraisingCampaign", new JSONObject()
                .put("id", id));
        return this;
    }

    public OfferBuilder image(String url){
        if(offer.isNull("images"))
            offer.put("images", new JSONArray());
        offer.getJSONArray("images").put(new JSONObject().put("url", url));
        return this;
    }

    public OfferBuilder images(List<String> urls){
        urls.forEach(this::image);
        return this;
    }

    public OfferBuilder location(String city, String countryCode, String postCode, String province){
        JSONObject location = new JSONObject()
                .put("city", city)
                .put("countryCode", countryCode)
                .put("postCode", postCode)
                .put("province", province);
        offer.put("location", location);
        return this;
    }

    public OfferBuilder parameterRangeValue(String id, String from, String to){
        if(offer.isNull("parameters"))
            offer.put("parameters", new JSONArray());
        JSONObject parameter = new JSONObject()
                .put("id", id)
                .put("rangeValue", new JSONObject()
                        .put("from", from)
                        .put("to", to));
        offer.getJSONArray("parameters").put(parameter);
        return this;
    }

    public OfferBuilder parameterValues(String id, List<String> values){
        if(offer.isNull("parameters"))
            offer.put("parameters", new JSONArray());
        JSONObject parameter = new JSONObject()
                .put("id", id)
                .put("values", new JSONArray(values));
        offer.getJSONArray("parameters").put(parameter);
        return this;
    }

    public OfferBuilder parameterValuesIds(String id, List<String> valuesIds){
        if(offer.isNull("parameters"))
            offer.put("parameters", new JSONArray());
        JSONObject parameter = new JSONObject()
                .put("id", id)
                .put("valuesIds", new JSONArray(valuesIds));
        offer.getJSONArray("parameters").put(parameter);
        return this;
    }

    public enum Invoice{
        VAT, VAT_MARGIN, WITHOUT_VAT, NO_INVOICE
    }

    public OfferBuilder payments(Invoice invoice){
        offer.put("payments", new JSONObject().put("invoice", invoice.toString()));
        return this;
    }

    public OfferBuilder product(String id){
        offer.put("product", new JSONObject().put("id", id));
        return this;
    }

    public OfferBuilder promotion(boolean bold, boolean departmentPage, boolean emphasized,
                                  boolean emphasizedHighlightBoldPackage, boolean highlight){
        promotionBold(bold);
        promotionDepartmentPage(departmentPage);
        promotionEmphasized(emphasized);
        promotionEmphasizedHighlightBoldPackage(emphasizedHighlightBoldPackage);
        promotionHighlight(highlight);
        return this;
    }

    public OfferBuilder promotionBold(boolean isActive){
        if(offer.isNull("promotion"))
            offer.put("promotion", new JSONObject());
        offer.getJSONObject("promotion").put("bold", isActive);
        return this;
    }

    public OfferBuilder promotionDepartmentPage(boolean isActive){
        if(offer.isNull("promotion"))
            offer.put("promotion", new JSONObject());
        offer.getJSONObject("promotion").put("departmentPage", isActive);
        return this;
    }

    public OfferBuilder promotionEmphasized(boolean isActive){
        if(offer.isNull("promotion"))
            offer.put("promotion", new JSONObject());
        offer.getJSONObject("promotion").put("emphasized", isActive);
        return this;
    }

    public OfferBuilder promotionEmphasizedHighlightBoldPackage(boolean isActive){
        if(offer.isNull("promotion"))
            offer.put("promotion", new JSONObject());
        offer.getJSONObject("promotion").put("emphasizedHighlightBoldPackage", isActive);
        return this;
    }

    public OfferBuilder promotionHighlight(boolean isActive){
        if(offer.isNull("promotion"))
            offer.put("promotion", new JSONObject());
        offer.getJSONObject("promotion").put("highlight", isActive);
        return this;
    }

    public OfferBuilder publication(String duration, String startingAt, PublicationStatus status, boolean republish){
        publicationDuration(duration);
        publicationStartingAt(startingAt);
        publicationStatus(status);
        publicationRepublish(republish);
        return this;
    }

    public OfferBuilder publicationDuration(String duration){
        if(offer.isNull("publication"))
            offer.put("publication", new JSONObject());
        offer.getJSONObject("publication").put("duration", duration);
        return this;
    }

    public OfferBuilder publicationStartingAt(String startingAt){
        if(offer.isNull("publication"))
            offer.put("publication", new JSONObject());
        offer.getJSONObject("publication").put("startingAt", startingAt);
        return this;
    }

    public enum PublicationStatus{
        INACTIVE, ACTIVATING, ACTIVE, ENDED
    }

    public OfferBuilder publicationStatus(PublicationStatus status){
        if(offer.isNull("publication"))
            offer.put("publication", new JSONObject());
        offer.getJSONObject("publication").put("publicationStatus", status.toString());
        return this;
    }

    public OfferBuilder publicationRepublish(boolean republish){
        if(offer.isNull("publication"))
            offer.put("publication", new JSONObject());
        offer.getJSONObject("publication").put("republish", republish);
        return this;
    }

    public enum SellingModeFormat{
        BUY_NOW, AUCTION, ADVERTISEMENT
    }

    public OfferBuilder sellingModeFormat(SellingModeFormat format){
        if(offer.isNull("sellingMode"))
            offer.put("sellingMode", new JSONObject());
        offer.getJSONObject("sellingMode").put("format", format.toString());
        return this;
    }

    public OfferBuilder sellingModePrice(String amount, String currency){
        if(offer.isNull("sellingMode"))
            offer.put("sellingMode", new JSONObject());
        JSONObject price = new JSONObject()
                .put("amount", amount)
                .put("currency", currency);
        offer.getJSONObject("sellingMode").put("price", price);
        return this;
    }

    public OfferBuilder sellingModeMinimalPrice(String amount, String currency){
        if(offer.isNull("sellingMode"))
            offer.put("sellingMode", new JSONObject());
        JSONObject minimalPrice = new JSONObject()
                .put("amount", amount)
                .put("currency", currency);
        offer.getJSONObject("sellingMode").put("minimalPrice", minimalPrice);
        return this;
    }

    public OfferBuilder sellingModeStartingPrice(String amount, String currency){
        if(offer.isNull("sellingMode"))
            offer.put("sellingMode", new JSONObject());
        JSONObject startingPrice = new JSONObject()
                .put("amount", amount)
                .put("currency", currency);
        offer.getJSONObject("sellingMode").put("startingPrice", startingPrice);
        return this;
    }

    public OfferBuilder sellingModeNetPrice(String amount, String currency){
        if(offer.isNull("sellingMode"))
            offer.put("sellingMode", new JSONObject());
        JSONObject netPrice = new JSONObject()
                .put("amount", amount)
                .put("currency", currency);
        offer.getJSONObject("sellingMode").put("netPrice", netPrice);
        return this;
    }

    public OfferBuilder tax(String id, String rate, String subject, String exemption){
        taxId(id);
        taxRate(rate);
        taxSubject(subject);
        taxExemption(exemption);
        return this;
    }

    public OfferBuilder taxId(String id){
        if(offer.isNull("tax"))
            offer.put("tax", new JSONObject());
        offer.getJSONObject("tax").put("id", id);
        return this;
    }

    public OfferBuilder taxRate(String rate){
        if(offer.isNull("tax"))
            offer.put("tax", new JSONObject());
        offer.getJSONObject("tax").put("rate", rate);
        return this;
    }

    public OfferBuilder taxSubject(String subject){
        if(offer.isNull("tax"))
            offer.put("tax", new JSONObject());
        offer.getJSONObject("tax").put("subject", subject);
        return this;
    }

    public OfferBuilder taxExemption(String exemption){
        if(offer.isNull("tax"))
            offer.put("tax", new JSONObject());
        offer.getJSONObject("tax").put("exemption", exemption);
        return this;
    }

    public OfferBuilder sizeTable(String id){
        offer.put("sizeTable", new JSONObject().put("id", id));
        return this;
    }

    public enum StockUnit{
        UNIT, PAIR, SET
    }

    public OfferBuilder stock(int available, StockUnit unit){
        JSONObject stock = new JSONObject()
                .put("available", available)
                .put("unit", unit.toString());
        offer.put("stock", stock);
        return this;
    }

    public OfferBuilder tecdocSpecification(String id){
        offer.put("tecdocSpecification", new JSONObject().put("id", id));
        return this;
    }

    public OfferBuilder b2b(boolean b2b){
        offer.put("b2b", new JSONObject().put("buyableOnlyByBusiness", b2b));
        return this;
    }

    public enum MessageToSellerMode{
        OPTIONAL, HIDDEN
    }

    public OfferBuilder messageToSellerSetting(MessageToSellerMode mode){
        offer.put("messageToSellerSettings", new JSONObject().put("mode", mode.toString()));
        return this;
    }

    public OfferBuilder language(String language){
        offer.put("language", language);
        return this;
    }

    public enum ProductIdType{
        GTIN, MPN
    }

    public OfferBuilder addProduct(String name, String categoryId, String productId,
                                   ProductIdType productIdType, List<String> images, int quantity){
        if(offer.isNull("productSet"))
            offer.put("productSet", new JSONArray());

        JSONObject product = new JSONObject()
                .put("name", name)
                .put("category", new JSONObject().put("id", categoryId))
                .put("id", productId)
                .put("idType", productIdType)
                .put("images", new JSONArray(images));

        JSONObject quantityJSON = new JSONObject()
                .put("value", quantity);

        offer.getJSONArray("productSet").put(
                new JSONObject()
                        .put("product", product)
                        .put("quantity", quantityJSON)
        );

        return this;
    }


    private JSONObject getProductsFromProductsSet(String productId){
        JSONArray products = offer.getJSONArray("productSet");
        for(int i = 0; i<products.length(); i++){
            JSONObject product = products.getJSONObject(i).getJSONObject("product");
            if(product.getString("id").equals(productId))
                return product;
        }
        //TODO: exception
        return null;
    }

    private OfferBuilder addProductParameter(String productId, JSONObject parameter){
        if(offer.isNull("productSet"))
            offer.put("productSet", new JSONArray());

        JSONObject product = getProductsFromProductsSet(productId);

        if(product == null)
            throw new NoSuchProductException();

        if(product.isNull("parameters"))
            product.put("parameters", new JSONArray());

        product.getJSONArray("parameters").put(parameter);

        return this;
    }

    public OfferBuilder addProductParameterRangeValue(String productId, String id, String name, String from, String to){
        JSONObject parameter = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("rangeValue", new JSONObject()
                        .put("from", from)
                        .put("to", to));

        return addProductParameter(productId, parameter);
    }

    public OfferBuilder addProductParameterValues(String productId, String id, String name, List<String> values){
        JSONObject parameter = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("values", new JSONArray(values));

        return addProductParameter(productId, parameter);
    }

    public OfferBuilder addProductParameterValuesIds(String productId, String id, String name, List<String> valuesIds){
        JSONObject parameter = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("valuesIds", new JSONArray(valuesIds));

        return addProductParameter(productId, parameter);
    }
}
