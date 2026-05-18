import apis.AuthUtils;
import apis.OutboundFlows;
import com.shaft.driver.SHAFT;
import com.shaft.tools.io.ReportManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class OutboundTests {
    private SHAFT.API apiDriver;
    private SHAFT.TestData.JSON usersDetailsTestData;
    String soEmail,soPassword;
    private static final String baseURL=System.getProperty("baseURL");

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

//        AuthUtils authentication = new AuthUtils();
//        String token =authentication.login("store-order@kingfahd.com","Nup@2070");
        apiDriver=new SHAFT.API(baseURL);
        apiDriver.addHeader("authorization","Bearer "+"eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIiwidHlwIjoiSldUIiwiY3R5IjoiSldUIn0..oruQT7UBkZkAXK1cMIjtqw.Cye0e9SKw5PqVFVwLIlcP_vZQ_VPFtu2MmaC8aPz3wVVfVt1f53BEN-VNt6efGK0tqTqX_0uvFodJLE4qJ9_k-gQgdeI9op3RpZfOSdkW83MmEs_V6PTlQXT-fPY7S6FO1LPgfbmhbQ1TaBLrt98EH7zC4AZK8x9Qo1qCErMQinBupPDpDXiID3dgUw-0HUBzfk09X8Msqwr-qGxvJd800Q8mVI2gPtrY-_pwTQ3SWDR6vrRHVX6rnFD20F_LGtDc6e6AWPnUyrwt3fQ38H4VKsKBU-5NNKTsCpEXjjmWLtQ6-Ps2UWSMxVYxVMPhm1ZfJpv0IC1G0UVS-GiMTzPSpMAwj1vgs6Y_h2CiU9GTmft2YwTB-ImH8PVzSVyUei_XrbG9sz55FBCRhEvpDLhZU85CZ127Jdax8P7sxYxEEgAoV-ISkp-MMEDBf0e_I6OaYmJpglUgR7hYV9_nMczRrsNHUm8ry_U4a0I7pSq-1ShxaUa7SPt0BJrQuw2zwx0y6zeOpzdH8nErEc2qSU-MoByIqR-Fw8i8v-pzR9ttSR5jlBNgGKk5Kue511iTKSvGTmPD_CFIUZGI0gPAzoerjkfTkGJrqSbpsnSUQTTyQWUvoGbX7EZa-7ikKTDzqhETqlqOwVyaaIfZdtway1dIJtUTp9njlzr3Q9uJWWNGm92XD9vhv-8bDdidgWfcmBlugmYHDaB0PTYZ9vPOH6ADC1aIWjM_gk1z5arJeDrOiaVjU_LXIeBsz_hrt2YwswMdNkEtuEmX1qHXQTfqZue1Zv5irxRW5u3H0KnaTXSQr0dmh2EEDgge1ChfdVcfbnZ197d76yhKR35j8vB6cihCfVL80K8ueopMQuNX9H5dnST7BP7nMDKUc55YSEa5CUyxrRvPyCGQ98LKf4ACa82MlGbk_al7MdY2Ar8BDXtlwFK62VU3Wn2cDVLyyigLDc-CwYbW-p3F4SZUp-S7a3Fxe7rojlQc53OPjupY_jc8tBBCYAeNslZy9VGQRW0T06EeVgfK_1N261dlgU_RFxEQc2OjLIbh2WN_pZkMb6LoI_njoSNN7g616GzLHZi3ZM_aRM3rrCHCekp_Vil-yyhPDiQgyjuHtTmhq0LGpkZwulYuyL1YNG47oVcFE1KMxAQ6tBqomkXdRGSvRvv46AkhnclGLyzXaDcNonQyLih1bwCxYl-8YyPCJXp3B5TsIN8Wq5nGKKU5iDRrpaR0AVeTlJ8otMF7moFd19bNbB36_SCm74e9Vd-4iH4owaiog8XUKgi1paR8Zy54JUrwTY26qAlPYK0xXI_iS0WMY99JjGiSz4h6u9cohWMT53fnFjco5oHZGg-ePUon_ldhYdDvlIiVpF6t_XKVh1_9v3rPs7TZbIqZqdOSD9ZBdapVmoo1KNT_SuGWZWzLozLGlaL27fVumUvTMCg5qxNtSAg_V5N5_dPhk9yu1cQ5das3SOZTNBJbJsZHk8zbgQAZhAbJcoWjwhcCLMf-nl9H0joTLAmoNdWFWBumAg46x4HnltUehzgKPf_ucuIkgXXgME78b-MAeRTOiGpzGn6GN4hSywi9XTmzWZg-oMwf6r-uuLmqaS2AOgabYwUK6RG_baPcLg-l2mGw16aN8D15sLYPA8raVgv2u7oNwD-FGCtQNbk6iu680qCSftnBSJyiaXJOogZ6sEgf9Iy9W91Qc9Y5mDOSIfzKn87S9F9ASy7dH-DPeILL1PNkSs7LByTgJIP9wedhEvy9HE6mih2k2xWCHFpjca3yhkkhxRQpnATYnaD4WE--v7xQl35vQscQBR9PwKZWCOrzuxvezY8w_-mz2qZl9LB5v08ObPewqObCfjNSqIb_BPNub5hXLLaJ5dfq9sKgOLv2Zjb5c_5XImEFxZC5LADRQ6Rnb12W8L-qDbHTj73ys9qyvBl35XSdhNhakAmQ91KHKg6_KPYENd9zb-41-Qf-32Aqn9IDF7qV5upHcUjeABhQQJuM7PSPSBxpZyzTLtB84EYAN1xDyLtvdieDO5hqr-6Pw2WGZQutTZZvAd3boe8CxPFx2Y-nOkkHw2mCkYiIih0ZgJ1eus-8M1J7qZcduQKRQULmryep1co.Z_h5Vc05J2-udCU0H5Gdu3mLWIBLxm7PJkKsLdqEzYQ");
        OutboundFlows outboundObject=new OutboundFlows(apiDriver);
        outboundObject.getWarehouse().getInventoryItems().createOutbound("2026-05-19",1);

    }







    @AfterMethod
    public void afterMethod(){
        apiDriver = null;
    }
}
