package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddPlace {

    private Location location;
    private int accuracy;
    private String name;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String address;
    private List<String> types;
    private String website;
    private String language;

    @Data
    public static class Location {
        private double lat;
        private double lng;
    }
}
