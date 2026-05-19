import apis.AuthUtils;
import apis.OutboundFlows;
import com.shaft.tools.io.ReportManager;
import io.qameta.allure.Step;
import utils.DateUtils;
import com.shaft.driver.SHAFT;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class OutboundTests {
    private SHAFT.API apiDriver;
    private SHAFT.TestData.JSON usersDetailsTestData;
    String soEmail,soPassword;
    private static final String baseURL = System.getProperty("baseURL");

    @BeforeClass
    public void beforeClass(){
        usersDetailsTestData = new SHAFT.TestData.JSON("UserCredentials.Json");
        soEmail = usersDetailsTestData.getTestData("StoreOrder_email");
        soPassword = usersDetailsTestData.getTestData("StoreOrder_password");
    }

    @BeforeMethod
    public void beforeMethod(){

    }

    @Test
    public void createRequest() throws MalformedURLException {
        ReportManager.log("Step 1: Login as SO user");
        AuthUtils authentication = new AuthUtils();
        String token =authentication.login(soEmail,soPassword);

        ReportManager.log("Step 2: Create Outbound order");
        apiDriver = new SHAFT.API(baseURL);
        apiDriver.addHeader("authorization","Bearer "+token);
        OutboundFlows outboundObject=new OutboundFlows(apiDriver);
        outboundObject.getWarehouse().getInventoryItems()
                .createOutbound(DateUtils.generateOutboundDeliveryDate(), 1);

        SHAFT.Validations.assertThat().object(outboundObject.getOutboundOrder().businessKey()).isNotNull();
    }







    @AfterMethod
    public void afterMethod(){
        apiDriver = null;
    }
}
