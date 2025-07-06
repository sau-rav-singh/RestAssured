package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class JiraTest {

    private static final String BASE_URI = "https://restassuredjira.atlassian.net";
    private static final String AUTH_HEADER = "Basic c2luZ2guc2F1cmF2QGljbG91ZC5jb206QVRBVFQzeEZmR0YwLUlaekVlUVBpQTJuWGFBQ3hwN1ZWY2pxckhZZmpGaDNha0FjbzJRd0JvdS1SdFNHbTc0bzAyUmRXcFZnVHJMbk9JdnZQck5rT3NLTDVWdnlMR2JRTElVYXAyN0tmeDNmLXA3X1RMMl9ZcDFwcFliVE03ZFUzbWpnRHMzZHVYcnFRbHZqZ2Z0cnotd00tcjNLSDN6ejlPMDFDSnQyZ2pMLU03R2FtRzcycjR3PUY3OUU2RjI3";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String ATTACHMENT_PATH = "src/test/resources/SG.png";
    private static final String JSON_PAYLOAD_PATH = "src/test/resources/JiraBug.json";

    @Test
    public void jiraBugTest() throws IOException {
        RestAssured.baseURI = BASE_URI;
        String jsonPayload = readJsonPayload();

        Response createIssueResponse = createIssue(jsonPayload);
        String bugKey = extractBugKey(createIssueResponse);

        Response addAttachmentResponse = addAttachment(bugKey);
        System.out.println("addAttachmentresponse: " + addAttachmentResponse.asString());
    }

    private String readJsonPayload() throws IOException {
        return new String(Files.readAllBytes(Paths.get(JiraTest.JSON_PAYLOAD_PATH)));
    }

    private Response createIssue(String jsonPayload) {
        return given()
                .header("Content-Type", CONTENT_TYPE_JSON)
                .header("Authorization", AUTH_HEADER)
                .body(jsonPayload)
                .when()
                .post("/rest/api/3/issue/")
                .then()
                .statusCode(201)
                .extract().response();
    }

    private String extractBugKey(Response response) {
        JsonPath js = new JsonPath(response.asString());
        return js.getString("key");
    }

    private Response addAttachment(String bugKey) {
        return given()
                .header("X-Atlassian-Token", "no-check")
                .pathParam("key", bugKey)
                .header("Authorization", AUTH_HEADER)
                .multiPart("file", new File(ATTACHMENT_PATH))
                .when()
                .post("/rest/api/3/issue/{key}/attachments")
                .then()
                .statusCode(200)
                .extract().response();
    }
}
