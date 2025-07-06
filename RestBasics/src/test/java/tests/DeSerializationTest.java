package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.restassured.path.json.JsonPath;
import org.json.JSONObject;
import org.testng.annotations.Test;

public class DeSerializationTest {

    static class Book {
        public String name;
        public String isbn;
        public String aisle;
        public String author;
    }
    @Test
    public void JacksonDeserializationExample() throws JsonProcessingException {
        String jsonResponse = "{ \"name\": \"Learn Appium Automation with Java\", \"isbn\": \"bcd1\", \"aisle\": \"2926\", \"author\": \"John foer\" }";

        // Deserialize JSON response using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = objectMapper.readValue(jsonResponse, Book.class);

        System.out.println("Book Name: " + book.name);
        System.out.println("Author: " + book.author);
        System.out.println("isbn Name: " + book.isbn);
        System.out.println("aisle: " + book.aisle);
    }

    @Test
    public void GsonDeserializationExample (){
        String jsonResponse = "{ \"name\": \"Learn Appium Automation with Java\", \"isbn\": \"bcd1\", \"aisle\": \"2926\", \"author\": \"John foer\" }";

        // Deserialize JSON response using Gson
        Gson gson = new Gson();
        Book book = gson.fromJson(jsonResponse, Book.class);

        System.out.println("Book Name: " + book.name);
        System.out.println("Author: " + book.author);
    }

    @Test
    public void OrgJsonDeserializationExample  (){
        String jsonResponse = "{ \"name\": \"Learn Appium Automation with Java\", \"isbn\": \"bcd1\", \"aisle\": \"2926\", \"author\": \"John foer\" }";

        // Deserialize JSON response using org.json
        JSONObject jsonObject = new JSONObject(jsonResponse);

        String name = jsonObject.getString("name");
        String author = jsonObject.getString("author");

        System.out.println("Book Name: " + name);
        System.out.println("Author: " + author);
    }

    @Test
    public void JsonPathDeserializationExample(){
        String jsonResponse = "{ \"name\": \"Learn Appium Automation with Java\", \"isbn\": \"bcd1\", \"aisle\": \"2926\", \"author\": \"John foer\" }";

        // Parse JSON using JsonPath
        JsonPath jsonPath = new JsonPath(jsonResponse);

        // Extract specific values
        String name = jsonPath.getString("name");
        String author = jsonPath.getString("author");

        System.out.println("Book Name: " + name);
        System.out.println("Author: " + author);
    }
}
