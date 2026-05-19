package apis;

import com.shaft.api.RestActions;
import com.shaft.driver.SHAFT;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.xml.config.XmlPathConfig;
import io.restassured.response.Response;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AuthUtils {

    private SHAFT.API apiDriver;
    private static final String ssoUrl = System.getProperty("SSO_URL");
    private static final String baseURL = System.getProperty("baseURL");

    @Step("Login to portal")
    public String login(String username, String password) throws MalformedURLException {
        apiDriver = new SHAFT.API("");
        Map<String, Object> params = Map.of(
                "pf.username", username,
                "pf.pass", password
        );

        RestAssured.config = RestAssuredConfig.config()
                .redirect(RedirectConfig.redirectConfig()
                        .followRedirects(false)
                );

        Response response = apiDriver.get(baseURL + Endpoints.login).setTargetStatusCode(303).perform().getResponse();
        String loginFollowUrl=response.getHeader("Location");

        response = apiDriver.get(loginFollowUrl).enableUrlEncoding(false).perform().getResponse();

        String sso_loginId= response.htmlPath().getString("**.find { it.name() == 'form' }.@action");

        response = apiDriver.post(ssoUrl + sso_loginId).setParameters(params, RestActions.ParametersType.FORM).perform().getResponse();

        String loginResponse, acsURL, saml, relay;

        acsURL = response.htmlPath().getString("**.find { it.name() == 'form' }.@action");
        saml = response.htmlPath().getString("**.find { it.@name == 'SAMLResponse' }.@value");
        relay = response.htmlPath().getString("**.find { it.@name == 'RelayState' }.@value");


         params = Map.of(
                "RelayState", relay,
                "SAMLResponse", saml
        );

        System.setProperty("rest.followRedirects", "false");
         apiDriver.post(acsURL).setParameters(params, RestActions.ParametersType.FORM)
                 .setTargetStatusCode(303)
                 .perform().getResponseBody();



        apiDriver.get(baseURL + Endpoints.getToken).perform();

        return apiDriver.getResponse().jsonPath().get("accessToken");

    }



}
