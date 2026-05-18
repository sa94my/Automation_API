package models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class CreateOutboundPayloadBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String COMMENT = "-";
    private static final String ORDER_NOTE = "-";
    private static final int ORDER_TYPE = 1;
    private static final String MUTATION = """
            mutation CreateOutbound($input: RequestOrderDataModelInput!) {
              createOutboundRequestCreateTaskSubmit_V1(requestOrderInput: $input) {
                status
                data
                errors {
                  message
                  __typename
                }
                __typename
              }
            }
            """;

    private CreateOutboundPayloadBuilder() {
    }

    public static String build(
            AssignedHospitalWarehouseContext warehouse,
            SelectedInventoryItem item,
            String orderRequestCode,
            String deliveryDate,
            int requestedQuantity
    ) {
        try {
            ObjectNode root = OBJECT_MAPPER.createObjectNode();
            root.put("operationName", "CreateOutbound");

            ObjectNode input = root.putObject("variables").putObject("input");
            buildOrderInformation(input.putObject("orderInformation"), warehouse, item, orderRequestCode, deliveryDate);
            buildRequesterInformation(input.putObject("requesterInformation"), warehouse);
            buildItemsInformation(input.putArray("itemsInformation"), item, requestedQuantity);

            root.put("query", MUTATION);
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build CreateOutbound request body", e);
        }
    }

    private static void buildOrderInformation(
            ObjectNode orderInformation,
            AssignedHospitalWarehouseContext warehouse,
            SelectedInventoryItem item,
            String orderRequestCode,
            String deliveryDate
    ) {
        orderInformation.put("orderRequestCode", orderRequestCode);
        orderInformation.put("shipmentLocation", warehouse.hospitalName());
        orderInformation.put("comment", COMMENT);
        orderInformation.put("orderType", ORDER_TYPE);
        orderInformation.put("orderNote", ORDER_NOTE);
        orderInformation.set("shipmentOrganizationId", OBJECT_MAPPER.createObjectNode());
        orderInformation.put("shipmentOrganizationNupcoId", parseNupcoId(warehouse.nupcoId()));
        orderInformation.put("deliveryDate", deliveryDate);
        orderInformation.put("plantCode", warehouse.plantCode());
        orderInformation.put("sLoc", warehouse.sLocCode());
        orderInformation.put("nupcoDepartmentId", nullToEmpty(item.nupcoDepartmentId()));
        orderInformation.put("classification", item.classificationName());
        orderInformation.put("classificationId", item.classificationId());
    }

    private static void buildRequesterInformation(
            ObjectNode requesterInformation,
            AssignedHospitalWarehouseContext warehouse
    ) {
        requesterInformation.put("requesterInventoryName", nullToEmpty(warehouse.warehouseDisplayName()));
        requesterInformation.put("requesterInventoryCorrelationID", warehouse.warehouseInventoryId());
        requesterInformation.put("sourceInventoryName", warehouse.hospitalName());
        requesterInformation.put("sourceInventoryCorrelationID", warehouse.hospitalInventoryId());
        requesterInformation.put("organizationNupcoId", warehouse.nupcoId());
        requesterInformation.put("isNupcoCustody", warehouse.hospitalIsNupcoCustody());
        requesterInformation.put("customerCode", nullToEmpty(warehouse.hospitalCode()));
    }

    private static void buildItemsInformation(
            ArrayNode itemsInformation,
            SelectedInventoryItem item,
            int requestedQuantity
    ) {
        ObjectNode lineItem = itemsInformation.addObject();
        lineItem.put("nUPCOCode", item.genericCode());
        lineItem.put("genericName", item.genericName());
        lineItem.put("unitOfMeasure", item.unitOfMeasure());
        lineItem.put("stockQuantity", item.stockQuantity());
        lineItem.put("availableQuantity", item.availableQuantity());
        lineItem.set("min", OBJECT_MAPPER.createObjectNode());
        lineItem.set("max", OBJECT_MAPPER.createObjectNode());
        lineItem.put("requestedQuantity", requestedQuantity);
        lineItem.set("remainingFromMax", OBJECT_MAPPER.createObjectNode());
    }

    private static int parseNupcoId(String nupcoId) {
        if (nupcoId == null || nupcoId.isBlank()) {
            throw new IllegalStateException("Hospital nupcoId is required for CreateOutbound");
        }
        return Integer.parseInt(nupcoId);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
