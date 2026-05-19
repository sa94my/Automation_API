package utils;

import com.shaft.driver.SHAFT;
import io.restassured.response.Response;

import java.util.List;

/**
 * Validates GraphQL error collections after {@link SHAFT.API#perform()}.
 * Successful runs use {@code null} or an empty list ({@code []}) for errors.
 */
public final class GraphQlAssertions {

    private GraphQlAssertions() {
    }

    public static void assertTopLevelErrorsEmpty(SHAFT.API api) {
        assertErrorsEmpty(api, "errors");
    }

    public static void assertErrorsEmpty(SHAFT.API api, String errorsJsonPath) {
        Response response = api.getResponse();
        Object errors = response.jsonPath().get(errorsJsonPath);

        if (errors == null) {
            return;
        }

        if (errors instanceof List<?> errorList) {
            if (errorList.isEmpty()) {
                return;
            }
            throw new AssertionError(
                    "Expected no GraphQL errors at '" + errorsJsonPath + "' but got: " + errorList
            );
        }

        api.assertThatResponse()
                .extractedJsonValue(errorsJsonPath)
                .isEqualTo("[]")
                .withCustomReportMessage("GraphQL errors at " + errorsJsonPath + " must be empty")
                .perform();
    }
}
