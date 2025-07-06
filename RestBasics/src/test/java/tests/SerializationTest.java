package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.testng.annotations.Test;
import pojo.AddPlace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SerializationTest {
    String filePath = "src/test/resources/complex.json";
    String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));

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
    public SerializationTest() throws IOException {
    }

    @Test
    public void orgJsonMockSerialization() {
        JSONObject jsonObject = new JSONObject(jsonString);
        String serializedJson = jsonObject.toString(4); // Indented with 4 spaces
        System.out.println(serializedJson);
    }

    @Test
    public void orgJsonPOJOSerialization(){
        JSONObject jsonObject = new JSONObject(setAddPlace());
        System.out.println(jsonObject.toString(4));
    }

    @Test
    public void orgGsonMockSerialization() {
        Gson gson = new Gson();
        Object jsonObject = gson.fromJson(jsonString, Object.class);
        String serializedJson = gson.toJson(jsonObject);
        System.out.println(serializedJson);
    }

    @Test
    public void orgGsonPOJOSerialization(){
        Gson gson = new Gson();
        String serializedJson = gson.toJson(setAddPlace());
        System.out.println(serializedJson);
        // Pretty-print JSON
        Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gsonPretty.toJson(setAddPlace());
        System.out.println(prettyJson);
    }

    @Test
    public void orgJacksonMockSerialization() throws JsonProcessingException {
        // Parse JSON String
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(jsonString, Object.class);

        // Serialize JSON Object_Pretty
        String serializedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        System.out.println(serializedJson);
    }

    @Test
    public void orgJacksonPOJOSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String serializedJson = objectMapper.writeValueAsString(setAddPlace());
        System.out.println(serializedJson);
    }
}
