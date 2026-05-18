import apis.AuthUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class LoginTests {

    @BeforeClass
    public void beforeClass(){

    }

    @BeforeMethod
    public void beforeMethod(){

    }

    @Test
    public void validateSuccessfulLogin() throws MalformedURLException {
        AuthUtils authentication = new AuthUtils();
        authentication.login("store-order@kingfahd.com","Nup@2070");
    }
}
