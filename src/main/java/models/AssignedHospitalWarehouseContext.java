package models;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Default hospital and supplying warehouse data from AssignedHospitalsWithSupplyingWarehouses.
 */
public record AssignedHospitalWarehouseContext(
        String hospitalId,
        String hospitalCode,
        String hospitalName,
        String hospitalInventoryId,
        String nupcoId,
        boolean hospitalIsNupcoCustody,
        String transactionId,
        String defaultHospitalId,
        String warehouseInventoryId,
        String plantCode,
        String sLocCode,
        String plantName,
        String storageLocationName,
        String warehouseDisplayName,
        boolean warehouseIsNupcoCustody
) {
    public static AssignedHospitalWarehouseContext fromResponse(JsonNode root) {
        JsonNode payload = root.path("data").path("assignedHospitalsWithSupplyingWarehouses");
        JsonNode edges = payload.path("connection").path("edges");

        for (JsonNode edge : edges) {
            JsonNode hospital = edge.path("node");
            if (!hospital.path("isDefault").asBoolean(false)) {
                continue;
            }

            JsonNode warehouse = resolveDefaultWarehouse(hospital.path("supplyingWarehouses"));
            if (warehouse == null) {
                throw new IllegalStateException("No supplying warehouse found for default hospital");
            }

            return new AssignedHospitalWarehouseContext(
                    textOrNull(hospital, "hospitalId"),
                    textOrNull(hospital, "hospitalCode"),
                    textOrNull(hospital, "hospitalName"),
                    textOrNull(hospital, "inventoryId"),
                    textOrNull(hospital, "nupcoId"),
                    hospital.path("isNupcoCustody").asBoolean(false),
                    textOrNull(payload, "transactionId"),
                    textOrNull(payload, "defaultHospitalId"),
                    textOrNull(warehouse, "inventoryId"),
                    textOrNull(warehouse, "plantCode"),
                    textOrNull(warehouse, "sLocCode"),
                    textOrNull(warehouse, "plantName"),
                    textOrNull(warehouse, "storageLocationName"),
                    textOrNull(warehouse, "name"),
                    warehouse.path("isNupcoCustody").asBoolean(false)
            );
        }

        throw new IllegalStateException("No hospital with isDefault=true found in response");
    }

    private static JsonNode resolveDefaultWarehouse(JsonNode supplyingWarehouses) {
        if (supplyingWarehouses.isMissingNode() || !supplyingWarehouses.isArray() || supplyingWarehouses.isEmpty()) {
            return null;
        }

        for (JsonNode warehouse : supplyingWarehouses) {
            if (warehouse.path("isDefault").asBoolean(false)) {
                return warehouse;
            }
        }

        return supplyingWarehouses.get(0);
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }
}
