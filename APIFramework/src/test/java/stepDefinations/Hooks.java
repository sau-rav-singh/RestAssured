package stepDefinations;

import java.io.IOException;

import io.cucumber.java.Before;

public class Hooks {

    @Before("@DeletePlace")
    public void beforeScenario() throws IOException {
        StepDefinition m = new StepDefinition();
        if (StepDefinition.place_id == null) {
            m.add_Place_Payload_with("Shetty", "French", "Asia");
            m.user_calls_with_http_request("AddPlaceAPI", "POST");
            m.verify_place_Id_created_maps_to_using("Shetty", "getPlaceAPI");
        }
    }
}