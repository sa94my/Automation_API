package apis;

import models.AssignedHospitalWarehouseContext;
import models.OutboundOrder;
import models.SelectedInventoryItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaft.driver.SHAFT;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.GraphQlAssertions;

import java.time.LocalDate;
public class OutboundFlows {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SHAFT.API apiDriver;
    private AssignedHospitalWarehouseContext warehouseContext;
    private SelectedInventoryItem selectedInventoryItem;
    private OutboundOrder outboundOrder;


    public OutboundFlows(SHAFT.API apiDriver) {
        this.apiDriver = apiDriver;
    }

    public AssignedHospitalWarehouseContext getWarehouseContext() {
        return warehouseContext;
    }

    public SelectedInventoryItem getSelectedInventoryItem() {
        return selectedInventoryItem;
    }

    public OutboundOrder getOutboundOrder() {
        return outboundOrder;
    }

    public String getInventoryId() {
        apiDriver.post(Endpoints.inventory).setRequestBody(Queries.GET_INVENTORY_ID()).perform();
        return "";
    }

    @Step("Get user inventories and warehouses")
    public OutboundFlows getWarehouse() {
        Response response = apiDriver.post(Endpoints.inventory)
                .setRequestBody(Queries.Get_INVENTORY_AND_WAREHOUSE())
                .setContentType(ContentType.JSON)
                .perform()
                .getResponse();

        GraphQlAssertions.assertTopLevelErrorsEmpty(apiDriver);
        warehouseContext = AssignedHospitalWarehouseContext.fromResponse(parseResponse(response));

        return this;
    }

    @Step("Get inventory items list for extracted warehouse and select first valid item")
    public OutboundFlows getFirstValidInventoryItem() {
        if (warehouseContext == null) {
            throw new IllegalStateException("Call getWarehouse() before getInventoryItems()");
        }
        /* if API returns no items while some orders exist on the UI it would be caused by
         modification of Queries.GET_INVENTORY_ITEMS ->filters -> AVAILABLE_GENERIC_QUANTITY
         which is accessed by minimumAvailableQuantity
         */
        Response response = apiDriver.post(Endpoints.inventory)
                .setRequestBody(Queries.GET_INVENTORY_ITEMS(
                        warehouseContext.warehouseInventoryId(),
                        "",
                        LocalDate.now().plusDays(1).toString(),
                        "100") )
                .setContentType(ContentType.JSON)
                .perform()
                .getResponse();

        GraphQlAssertions.assertErrorsEmpty(apiDriver, "data.query_0_4.errors");
        selectValidInventoryItem(response);

        return this;
    }

    @Step("Get specific inventory item")
    public OutboundFlows getSpecificInventoryItem(String searchKey) {
        if (warehouseContext == null) {
            throw new IllegalStateException("Call getWarehouse() before getInventoryItems()");
        }

        Response response = apiDriver.post(Endpoints.inventory)
                .setRequestBody(Queries.GET_INVENTORY_ITEMS(
                        warehouseContext.warehouseInventoryId(),
                        searchKey,
                        LocalDate.now().plusDays(1).toString(),
                        "0") )
                .setContentType(ContentType.JSON)
                .perform()
                .getResponse();

        GraphQlAssertions.assertErrorsEmpty(apiDriver, "data.query_0_4.errors");
        selectValidInventoryItem(response);

        return this;
    }

    @Step("Select first valid inventory item from list inventory response")
    public SelectedInventoryItem selectValidInventoryItem(Response response) {
        JsonNode firstItem = SelectedInventoryItem.firstItemFromResponse(parseResponse(response));
        selectedInventoryItem = SelectedInventoryItem.fromListInventoryItem(firstItem);
        return selectedInventoryItem;
    }

    @Step("Create outbound request")
    public OutboundFlows createOutbound(String deliveryDate, int requestedQuantity) {

        if (warehouseContext == null) {
            throw new IllegalStateException("Call getWarehouse() before createOutbound()");
        }
        if (selectedInventoryItem == null) {
            throw new IllegalStateException("Call getInventoryItems() before createOutbound()");
        }

        outboundOrder = OutboundOrder.from(warehouseContext, selectedInventoryItem, deliveryDate, requestedQuantity);

        String businessKey = apiDriver.post(Endpoints.outbound)
                .setRequestBody(Queries.CREATE_OUTBOUND(outboundOrder))
                .setContentType(ContentType.JSON)
                .perform()
                .getResponseJSONValue("data.createOutboundRequestCreateTaskSubmit_V1.data");

        GraphQlAssertions.assertErrorsEmpty(apiDriver, "data.createOutboundRequestCreateTaskSubmit_V1.errors");
        outboundOrder = outboundOrder.withBusinessKey(businessKey);


        return this;
    }

    private JsonNode parseResponse(Response response) {
        try {
            return OBJECT_MAPPER.readTree(response.getBody().asString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response body", e);
        }
    }
}

