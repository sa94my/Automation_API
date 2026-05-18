package apis;

import models.AssignedHospitalWarehouseContext;
import models.SelectedInventoryItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shaft.driver.SHAFT;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class OutboundFlows {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SHAFT.API apiDriver;
    private AssignedHospitalWarehouseContext warehouseContext;
    private SelectedInventoryItem selectedInventoryItem;
    private Response createOutboundResponse;

    public OutboundFlows(SHAFT.API apiDriver) {
        this.apiDriver = apiDriver;
    }

    public AssignedHospitalWarehouseContext getWarehouseContext() {
        return warehouseContext;
    }

    public SelectedInventoryItem getSelectedInventoryItem() {
        return selectedInventoryItem;
    }

    public Response getCreateOutboundResponse() {
        return createOutboundResponse;
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

        warehouseContext = AssignedHospitalWarehouseContext.fromResponse(parseResponse(response));
        return this;
    }

    @Step("Get inventory items list for extracted warehouse")
    public OutboundFlows getInventoryItems() {
        if (warehouseContext == null) {
            throw new IllegalStateException("Call getWarehouse() before getInventoryItems()");
        }

        Response response = apiDriver.post(Endpoints.inventory)
                .setRequestBody(Queries.GET_INVENTORY_ITEMS(
                        warehouseContext.warehouseInventoryId(),
                        LocalDate.now().plusDays(1).toString()))
                .setContentType(ContentType.JSON)
                .perform()
                .getResponse();

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
        //deliverydate format yyyy-mm-dd
        if (warehouseContext == null) {
            throw new IllegalStateException("Call getWarehouse() before createOutbound()");
        }
        if (selectedInventoryItem == null) {
            throw new IllegalStateException("Call getInventoryItems() before createOutbound()");
        }

        String orderRequestCode = UUID.randomUUID().toString();
        Map<String, Object> requestBody = toRequestBodyMap(Queries.CREATE_OUTBOUND(
                warehouseContext,
                selectedInventoryItem,
                orderRequestCode,
                deliveryDate,
                requestedQuantity));

        createOutboundResponse = apiDriver.post(Endpoints.outbound)
                .setRequestBody(requestBody)
                .setContentType(ContentType.JSON)
                .perform()
                .getResponse();

        return this;
    }

    private JsonNode parseResponse(Response response) {
        try {
            return OBJECT_MAPPER.readTree(response.getBody().asString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response body", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toRequestBodyMap(ObjectNode payload) {
        return OBJECT_MAPPER.convertValue(payload, Map.class);
    }
}

