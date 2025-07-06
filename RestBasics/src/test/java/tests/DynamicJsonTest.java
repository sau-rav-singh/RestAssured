package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class DynamicJsonTest {

    @Test(dataProvider = "BooksData")
    public void addBook(String bookGenre, int bookNum) throws IOException {
        RestAssured.baseURI = "https://rahulshettyacademy.com";

        JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get("src/test/resources/AddBook.json"))));
        Random random = new Random();
        String randomIsbn = "ISBN-" + bookGenre + (1000 + random.nextInt(9000));
        String randomAisle = String.valueOf(100 + bookNum + random.nextInt(900));

        jsonObject.put("isbn", randomIsbn);
        jsonObject.put("aisle", randomAisle);

        String addBookResponse = given().header("Content-Type", "application/json").body(jsonObject.toString()).when().post("Library/Addbook.php").then().assertThat().statusCode(200).extract().response().asString();

        JsonPath js = new JsonPath(addBookResponse);
        String bookId = js.getString("ID");
        System.out.println("bookId is " + bookId);
    }

    @DataProvider(name = "BooksData")
    public Object[][] getData() {
        return new Object[][]{{"Fic", 2}, {"Non_Fic", 3}};
    }
}
