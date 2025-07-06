package tests;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.AddPlace;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BasicTest {

    private String place_id;

    private static AddPlace setAddPlace() {
        AddPlace addPlace = new AddPlace();
        AddPlace.Location location = new AddPlace.Location();
        location.setLat(-38.383494);
        location.setLng(33.427362);
        addPlace.setLocation(location);
        addPlace.setAccuracy(50);
        addPlace.setName("Frontline house");
        addPlace.setPhoneNumber("(+91) 983 893 3937");
        addPlace.setAddress("29, side layout, cohen 09");
        addPlace.setTypes(List.of(new String[]{"shoe park", "shop"}));
        addPlace.setWebsite("http://google.com");
        addPlace.setLanguage("French-IN");
        return addPlace;
    }

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://rahulshettyacademy.com";
    }

    @Test
    public void testPOJOAddPlace() {
        InputStream createBookingJsonSchema = getClass().getClassLoader().getResourceAsStream("jsonSchemas/addFileSchema.json");
        AddPlace addPlace = setAddPlace();
        assert createBookingJsonSchema != null;
        Response response = given().queryParam("key", "qaclick123").header("Content-Type", "application/json").body(addPlace).when().post("maps/api/place/add/json").then().assertThat().statusCode(200).body("scope", equalTo("APP")).header("server", "Apache/2.4.52 (Ubuntu)")
                .body(JsonSchemaValidator.matchesJsonSchema(createBookingJsonSchema))
                .extract().response();
        String postResponse = response.asString();
        JsonPath js = new JsonPath(postResponse);

        String placeId = js.get("place_id");
        String status = js.get("status");
        String scope = js.get("scope");
        System.out.println("place_id is " + placeId);

        Assert.assertEquals(status, "OK", "Status should be OK");
        Assert.assertEquals(scope, "APP", "Scope should be APP");
        Assert.assertNotNull(placeId, "Place ID should not be null");
        Assert.assertFalse(placeId.isEmpty(), "Place ID should not be empty");
    }

    @Test
    public void testAddPlace() throws IOException {
        String postResponse = given().queryParam("key", "qaclick123").header("Content-Type", "application/json").body(new String(Files.readAllBytes(Paths.get("src/test/resources/AddPlace.json")))).when().post("maps/api/place/add/json").then().assertThat().statusCode(200).body("scope", equalTo("APP")).header("server", "Apache/2.4.52 (Ubuntu)").extract().response().asString();

        JsonPath js = new JsonPath(postResponse);
        place_id = js.get("place_id");
        System.out.println("place_id is " + place_id);
    }

    @Test(dependsOnMethods = {"testAddPlace"})
    public void testUpdatePlace() {
        String putResponse = given().queryParam("place_id", place_id).queryParam("key", "qaclick123").header("Content-Type", "application/json").body("{\n" + "\"place_id\":\"" + place_id + "\",\n" + "\"address\":\"70 Summer walk, IND\",\n" + "\"key\":\"qaclick123\"\n" + "}").when().put("maps/api/place/update/json").then().assertThat().statusCode(200).body("msg", equalTo("Address successfully updated")).extract().response().asString();

        System.out.println("putResponse is " + putResponse);
    }

    @Test(dependsOnMethods = {"testUpdatePlace"})
    public void testGetPlace() {
        String getResponse = given().queryParam("key", "qaclick123").queryParam("place_id", place_id).when().get("maps/api/place/get/json").then().statusCode(200).extract().response().asString();

        System.out.println(getResponse);
    }
}