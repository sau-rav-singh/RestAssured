package tests;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class CoursesTest {

    @Test
    public void testJsonParsing() {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get("src/test/resources/Courses.json")));
            JsonPath jsonPath = new JsonPath(jsonContent);

            System.out.println("Instructor: " + jsonPath.getString("instructor"));
            System.out.println("Expertise: " + jsonPath.getString("expertise"));
            System.out.println("Services: " + jsonPath.getString("services"));
            System.out.println("URL: " + jsonPath.getString("url"));
            System.out.println("LinkedIn: " + jsonPath.getString("linkedIn"));

            Map<String, List<Map<String, Object>>> courses = jsonPath.getMap("courses");

            for (Map.Entry<String, List<Map<String, Object>>> categoryEntry : courses.entrySet()) {
                String categoryName = categoryEntry.getKey();
                List<Map<String, Object>> courseList = categoryEntry.getValue();

                System.out.println("\n" + categoryName + " Courses:");
                for (Map<String, Object> course : courseList) {
                    String courseTitle = (String) course.get("courseTitle");
                    int price = Integer.parseInt(course.get("price").toString());
                    System.out.println("Course Title: " + courseTitle + ", Price: " + price);
                }
            }

        } catch (IOException e) {
            Assert.fail("Failed to read JSON file: " + e.getMessage());
        } catch (ClassCastException e) {
            Assert.fail("Type casting issue: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("An unexpected error occurred: " + e.getMessage());
        }
    }
}
