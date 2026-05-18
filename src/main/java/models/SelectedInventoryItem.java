package models;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Inventory line item selected for outbound request (from listInventory).
 */
public record SelectedInventoryItem(
        String inventoryId,
        String genericCode,
        String genericName,
        String classificationName,
        int classificationId,
        String unitOfMeasure,
        long stockQuantity,
        long availableQuantity,
        long availableGenericQuantity,
        Integer min,
        Integer max,
        Integer remainingFromMaxQuantity,
        String plantCode,
        String plantName,
        String sloc,
        String nupcoDepartmentId
) {
    public static SelectedInventoryItem fromListInventoryItem(JsonNode item) {
        if (item == null || item.isMissingNode() || item.isNull()) {
            throw new IllegalStateException("Inventory item node is missing");
        }

        return new SelectedInventoryItem(
                textOrNull(item, "inventoryId"),
                textOrNull(item, "genericCode"),
                textOrNull(item, "genericName"),
                textOrNull(item, "classificationName"),
                item.path("classificationId").asInt(0),
                textOrNull(item, "unitOfMeasure"),
                item.path("stockQuantity").asLong(0),
                item.path("availableQuantity").asLong(0),
                item.path("availableGenericQuantity").asLong(0),
                intOrNull(item, "min"),
                intOrNull(item, "max"),
                intOrNull(item, "remainingFromMaxQuantity"),
                textOrNull(item, "plantCode"),
                textOrNull(item, "plantName"),
                textOrNull(item, "sloc"),
                textOrNull(item, "nupcoDepartmentId")
        );
    }

    public static JsonNode firstItemFromResponse(JsonNode root) {
        JsonNode items = root.path("data").path("query_0_4").path("data");
        if (!items.isArray() || items.isEmpty()) {
            throw new IllegalStateException("No inventory items returned from listInventory");
        }
        return items.get(0);
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private static Integer intOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        return value.asInt();
    }
}
