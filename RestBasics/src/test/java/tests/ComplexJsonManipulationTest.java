package tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ComplexJsonManipulationTest {

    public static void main(String[] args) throws IOException {
        String filePath = "src/test/resources/complex.json";
        String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. Deserialize JSON string to JsonNode
        JsonNode rootNode = objectMapper.readTree(jsonString);

        // 2. Update the "purchaseAmount" in the "dashboard" section
        ObjectNode dashboardNode = (ObjectNode) rootNode.path("dashboard");
        dashboardNode.put("purchaseAmount", 1000); // Updated purchaseAmount

        // 3. Add "courseLevel" field under each course in "courses" array
        ArrayNode coursesNode = (ArrayNode) rootNode.path("courses");
        for (JsonNode courseNode : coursesNode) {
            ((ObjectNode) courseNode).put("courseLevel", "Beginner"); // Added new field
        }

        // 4. Change the "title" of the first course ("Selenium Python") to "Advanced Selenium"
        JsonNode firstCourse = coursesNode.get(0);
        ((ObjectNode) firstCourse).put("title", "Advanced Selenium");

        // 5. Delete the "reviews" field from the second course ("Cypress")
        JsonNode secondCourse = coursesNode.get(1);
        ((ObjectNode) secondCourse).remove("reviews");

        // 6. Add a new course to the "courses" array
        ObjectNode newCourse = objectMapper.createObjectNode();
        newCourse.put("title", "Appium");
        newCourse.put("price", 55);
        newCourse.put("copies", 8);
        ObjectNode salesNode = objectMapper.createObjectNode();
        salesNode.put("udemy", 15);
        salesNode.put("website", 5);
        newCourse.set("sales", salesNode);
        ArrayNode reviewsNode = objectMapper.createArrayNode();
        ObjectNode reviewNode = objectMapper.createObjectNode();
        reviewNode.put("channel", "udemy");
        reviewNode.put("reviewcount", 30);
        reviewsNode.add(reviewNode);
        newCourse.set("reviews", reviewsNode);

        // Add the new course to the courses array
        coursesNode.add(newCourse);

        // 7. Serialize the modified JSON back to string
        String modifiedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

        // Print the modified JSON
        System.out.println(modifiedJson);
    }
}