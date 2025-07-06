package resources;

import lombok.Getter;

@Getter
public enum APIResources {
    AddPlaceAPI("/maps/api/place/add/json"),
    getPlaceAPI("/maps/api/place/get/json"),
    deletePlaceAPI("/maps/api/place/delete/json");
    private final String resource;
    APIResources(String resource) {
        this.resource = resource;
    }
}