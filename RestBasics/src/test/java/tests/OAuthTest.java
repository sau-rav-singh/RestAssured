package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.CourseDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OAuthTest {

    private String accessToken;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://rahulshettyacademy.com";
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com");
        formParams.put("client_secret", "erZOWM9g3UtwNRj340YYaK_W");
        formParams.put("grant_type", "client_credentials");
        formParams.put("scope", "trust");

        String response = given().formParams(formParams).when().post("oauthapi/oauth2/resourceOwner/token").then().statusCode(200).extract().response().asString();

        JsonPath js = new JsonPath(response);
        accessToken = js.getString("access_token");
    }

    @Test
    public void getCourseDetails() {
        String courseDetails = given().queryParam("access_token", accessToken).when().get("oauthapi/getCourseDetails").then().log().all().statusCode(200).extract().response().asString();
        System.out.println(courseDetails);
    }

    @Test
    public void getCourseAsPOJODetails() {
        CourseDetails courseDetails = given().queryParam("access_token", accessToken).when().get("oauthapi/getCourseDetails").then().statusCode(200).extract().response().as(CourseDetails.class);
        System.out.println("Instructor: " + courseDetails.getInstructor());
        System.out.println("LinkedIn: " + courseDetails.getLinkedIn());
        System.out.println("Services: " + courseDetails.getServices());
        System.out.println("Expertise: " + courseDetails.getExpertise());
        System.out.println("URL: " + courseDetails.getUrl());

        Map<String, List<CourseDetails.Course>> courses = courseDetails.getCourses();

        courses.forEach((category, courseList) -> {
            System.out.println("Category: " + category);
            courseList.forEach(course -> {
                System.out.println("  Course Title: " + course.getCourseTitle());
                System.out.println("  Price: " + course.getPrice());
            });
        });
    }
}
