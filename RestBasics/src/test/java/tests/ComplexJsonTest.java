package tests;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ComplexJsonTest {

    @Test
    public void testComplexJsonParsing() {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get("src/test/resources/complex.json")));
            JsonPath jsonPath = new JsonPath(jsonContent);

            List<Map<String, Object>> courses = jsonPath.get("courses");
            System.out.println("Count of all the courses is " + courses.size());

            Map<String, Object> dashboard = jsonPath.get("dashboard");
            Integer totalAmount = (Integer) dashboard.get("purchaseAmount");
            System.out.println("Total Amount: " + totalAmount);

            int totalCalculatedAmount = 0;
            for (Map<String, Object> course : courses) {
                String title = (String) course.get("title");
                Integer price = (Integer) course.get("price");
                Integer copies = (Integer) course.get("copies");
                totalCalculatedAmount += price * copies;
                System.out.println("Title: " + title + ", Price: " + price);
                if ("RPA".equals(title)) {
                    System.out.println("Copies of RPA sold are " + copies);
                }
                @SuppressWarnings("unchecked")
                Map<String, Integer> salesMap = (Map<String, Integer>) course.get("sales");
                System.out.println("Udemy Sales of " + title + " is " + salesMap.get("udemy"));
                System.out.println("Website Sales of " + title + " is " + salesMap.get("website"));
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> reviewsList = (List<Map<String, Object>>) course.get("reviews");
                for (Map<String, Object> review : reviewsList) {
                    String channel = (String) review.get("channel");
                    Integer reviewCount = (Integer) review.get("reviewcount");
                    System.out.println("Reviews from " + channel + " is " + reviewCount);
                }
            }
            Assert.assertEquals(totalCalculatedAmount, totalAmount, "Total Sum Test");

        } catch (IOException e) {
            Assert.fail("Failed to read JSON file: " + e.getMessage());
        } catch (ClassCastException e) {
            Assert.fail("Type casting issue: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("An unexpected error occurred: " + e.getMessage());
        }
    }
}
