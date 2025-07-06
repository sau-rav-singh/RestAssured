package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;
import pojo.CourseDetails;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class CoursesPojoTest {
    @Test
    public void CoursesJSONPathTest() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get("src/test/resources/Courses.json")));
        JsonPath jsonPath = new JsonPath(jsonContent);

        System.out.println("Instructor: " + jsonPath.get("instructor"));
        System.out.println("LinkedIn: " + jsonPath.get("linkedIn"));
        System.out.println("Services: " + jsonPath.get("services"));
        System.out.println("Expertise: " + jsonPath.get("expertise"));
        System.out.println("URL: " + jsonPath.get("url"));

        Map<String, List<Map<String, String>>> courses = jsonPath.getMap("courses");
        courses.forEach((category, courseList) -> {
            System.out.println("Category: " + category);
            courseList.forEach(course -> {
                System.out.println("Course Title: " + course.get("courseTitle"));
                System.out.println("Price: " + course.get("price"));
            });
        });
    }

    @Test
    public void CoursesPOJOTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/Courses.json");
        CourseDetails courseDetails = objectMapper.readValue(file, CourseDetails.class);
        System.out.println("Instructor: " + courseDetails.getInstructor());
        System.out.println("LinkedIn: " + courseDetails.getLinkedIn());
        System.out.println("Services: " + courseDetails.getServices());
        System.out.println("Expertise: " + courseDetails.getExpertise());
        System.out.println("URL : " + courseDetails.getUrl());

        Map<String, List<CourseDetails.Course>> courses = courseDetails.getCourses();
        courses.forEach((category, courseList) -> {
            System.out.println("Category: " + category);
            courseList.forEach(course -> {
                System.out.println("Course Title: " + course.getCourseTitle());
                System.out.println("Price: " + course.getPrice());
            });
        });
    }
}
