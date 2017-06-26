package thesis.tg.com.s_cloud.data.from_local;

/**
 * Created by CKLD on 6/25/17.
 */

public interface MockData {
    String jsonuser = "{\n" +
            "            \"id\": \"0\",\n" +
            "            \"email\": \"testing@gmail.com\",\n" +
            "            \"password\": \"12345678\",\n" +
            "            \"birthday\": \"20/07/2017\",\n" +
            "            \"fullname\": \"Scloud User\",\n" +
            "            \"job\": \"Software Engineer\",\n" +
            "            \"country\": \"Vietnam\"\n" +
            "        }";


    String auth_resp = "{\n" +
            "            \"status\": \"success\",\n" +
            "            \"message\": \"Successfully registered.\",\n" +
            "            \"auth_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXkiOm51bGwsImlhdCI6MTQ5Nzc1NjgyOCwic3ViIjoxLCJleHAiOjE0OTc4NDMyMjh9.sFC_YVY47XMeA-Jw_StOfFxeZCvLbAFtnGwaH0udma4\"\n" +
            "        }";


    String login = "{\n" +
            "            \"email\": \"testing@gmail.com\",\n" +
            "            \"password\": \"12345678\",\n" +
            "            \"mac_address\": \"12:32:45:5F:3C:12\"\n" +
            "        }";

    String root_request = "{\n" +
            "                \"mac_address\": \"32:23:43:4F:2S:5D\",\n" +
            "                \"os\": \"macOS Sierra\",\n" +
            "                \"backup_key\": \"adsfasdf123\",\n" +
            "                \"modulus\": \"123123123124123\",\n" +
            "                \"exponent\": \"adfasdfasdf123123\",\n" +
            "                \"encrypted_modulus\": \"123123asdf123\",\n" +
            "                \"encrypted_exponent\": \"asdfasdf123123\"\n" +
            "            }";
}
