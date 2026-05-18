package apis;

import models.AssignedHospitalWarehouseContext;
import models.CreateOutboundPayloadBuilder;
import models.SelectedInventoryItem;

public class Queries {

    public static String GET_INVENTORY_ID() {
        return """
                {
                  "query": "query orgsList($inventoryType: InventoryType!) {\\n  organizations(inventoryType: $inventoryType) {\\n    nupcoId\\n    name\\n    inventoryId\\n    isNupcoCustody\\n    departments {\\n      id\\n      name\\n      inventoryId\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n",
                  "variables": {
                    "inventoryType": "ONHAND_INVENTORY"
                  }
                }""";
    }

    public static String Get_INVENTORY_AND_WAREHOUSE() {
        return """
                {
                  "operationName": "AssignedHospitalsWithSupplyingWarehouses",
                  "variables": {
                    "includeWarehouses": true,
                    "cursorPagination": {
                      "first": 7
                    }
                  },
                  "query": "query AssignedHospitalsWithSupplyingWarehouses($filter: AssignedHospitalFilterInput, $includeWarehouses: Boolean, $cursorPagination: CursorPaginationInput) {\\n  assignedHospitalsWithSupplyingWarehouses(\\n    filter: $filter\\n    includeWarehouses: $includeWarehouses\\n    cursorPagination: $cursorPagination\\n  ) {\\n    connection {\\n      edges {\\n        node {\\n          hospitalId\\n          hospitalCode\\n          hospitalName\\n          isDefault\\n          inventoryId\\n          nupcoId\\n          supplyingWarehouses {\\n            inventoryId\\n            plantCode\\n            sLocCode\\n            plantName\\n            storageLocationName\\n            name\\n            isDefault\\n            isNupcoCustody\\n            __typename\\n          }\\n          __typename\\n        }\\n        cursor\\n        __typename\\n      }\\n      pageInfo {\\n        hasNextPage\\n        hasPreviousPage\\n        startCursor\\n        endCursor\\n        __typename\\n      }\\n      __typename\\n    }\\n    totalCount\\n    defaultHospitalId\\n    transactionId\\n    __typename\\n  }\\n}\\n"
                }""";
    }

    // Could be expanded/enhanced to use for filtering by classification OR searching for an order if needed
    public static String GET_INVENTORY_ITEMS(String inventoryId, String expiryDate) {
        return """
                {
                  "operationName": "listInventory",
                  "variables": {
                    "inventoryId": "%s",
                    "organizationType": "INVENTORY_ID",
                    "skip": 0,
                    "take": 7,
                    "columnName": "AVAILABLE_GENERIC_QUANTITY",
                    "orderType": "DESC",
                    "searchColumns": "WAREHOUSE_INVENTORY",
                    "searchKey": "",
                    "execludedGenerics": [],
                    "filters": [
                      {
                        "columnName": "CLASSIFICATION_ID",
                        "value": "",
                        "operatorType": "EQUAL"
                      },
                      {
                        "columnName": "EXPIRY_DATE",
                        "value": "%s",
                        "operatorType": "MORE_THAN"
                      },
                      {
                        "columnName": "AVAILABLE_GENERIC_QUANTITY",
                        "value": "10",
                        "operatorType": "MORE_THAN"
                      }
                    ]
                  },
                  "query": "query listInventory($inventoryId: String!, $organizationType: OrganizationSearch!, $skip: Int!, $take: Int!, $columnName: InventoryItemsFilterColumns!, $orderType: OrderTypeEnum!, $searchColumns: InventoryType!, $searchKey: String!, $execludedGenerics: [String!], $filters: [FilterDataModelOfInventoryItemsFilterColumnsInput!]) {\\n  query_0_4(\\n    organizationType: {typeColumn: $organizationType, id: $inventoryId}\\n    sortingAndFiltering: {filters: $filters, order: {columnName: $columnName, orderType: $orderType}, pager: {skip: $skip, take: $take}}\\n    execludedGenerics: $execludedGenerics\\n    search: {searchKey: $searchKey, searchColumns: $searchColumns}\\n  ) {\\n    errors\\n    status\\n    data {\\n      count\\n      inventoryId\\n      genericCode\\n      genericName\\n      classificationName\\n      classificationId\\n      unitOfMeasure\\n      stockQuantity\\n      availableQuantity\\n      bookedQuantity\\n      bookedGenericQuantity\\n      availableGenericQuantity\\n      min\\n      max\\n      plantCode\\n      plantName\\n      sloc\\n      nupcoDepartmentId\\n      remainingFromMaxQuantity\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }""".formatted(inventoryId, expiryDate);
    }

    public static String CREATE_OUTBOUND(
            AssignedHospitalWarehouseContext warehouse,
            SelectedInventoryItem item,
            String orderRequestCode,
            String deliveryDate,
            int requestedQuantity
    ) {
        return CreateOutboundPayloadBuilder.build(
                warehouse,
                item,
                orderRequestCode,
                deliveryDate,
                requestedQuantity
        );
    }
}
