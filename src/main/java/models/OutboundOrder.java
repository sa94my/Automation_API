package models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

/**
 * Data required to create an outbound order and the business key returned on success.
 */
public record OutboundOrder(
        String orderRequestCode,
        String deliveryDate,
        int requestedQuantity,
        String hospitalName,
        int shipmentOrganizationNupcoId,
        String plantCode,
        String sLocCode,
        String nupcoDepartmentId,
        String classificationName,
        int classificationId,
        String requesterInventoryName,
        String requesterInventoryCorrelationId,
        String sourceInventoryCorrelationId,
        String organizationNupcoId,
        boolean isNupcoCustody,
        String customerCode,
        String genericCode,
        String genericName,
        String unitOfMeasure,
        long stockQuantity,
        long availableQuantity,
        Integer min,
        Integer max,
        Integer remainingFromMax,
        String businessKey
) {
    public static OutboundOrder from(
            AssignedHospitalWarehouseContext warehouse,
            SelectedInventoryItem item,
            String deliveryDate,
            int requestedQuantity
    ) {
        return new OutboundOrder(
                UUID.randomUUID().toString(),
                deliveryDate,
                requestedQuantity,
                warehouse.hospitalName(),
                parseNupcoId(warehouse.nupcoId()),
                warehouse.plantCode(),
                warehouse.sLocCode(),
                nullToEmpty(item.nupcoDepartmentId()),
                item.classificationName(),
                item.classificationId(),
                nullToEmpty(warehouse.warehouseDisplayName()),
                warehouse.warehouseInventoryId(),
                warehouse.hospitalInventoryId(),
                warehouse.nupcoId(),
                warehouse.hospitalIsNupcoCustody(),
                nullToEmpty(warehouse.hospitalCode()),
                item.genericCode(),
                item.genericName(),
                item.unitOfMeasure(),
                item.stockQuantity(),
                item.availableQuantity(),
                item.min(),
                item.max(),
                item.remainingFromMaxQuantity(),
                null
        );
    }



    public OutboundOrder withBusinessKey(String businessKey) {
        return new OutboundOrder(
                orderRequestCode,
                deliveryDate,
                requestedQuantity,
                hospitalName,
                shipmentOrganizationNupcoId,
                plantCode,
                sLocCode,
                nupcoDepartmentId,
                classificationName,
                classificationId,
                requesterInventoryName,
                requesterInventoryCorrelationId,
                sourceInventoryCorrelationId,
                organizationNupcoId,
                isNupcoCustody,
                customerCode,
                genericCode,
                genericName,
                unitOfMeasure,
                stockQuantity,
                availableQuantity,
                min,
                max,
                remainingFromMax,
                businessKey
        );
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
