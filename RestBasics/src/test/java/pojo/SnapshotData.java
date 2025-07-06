package pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SnapshotData {
    @JsonProperty("Status")
    private String status;

    @JsonProperty("StatusMessage")
    private String statusMessage;

    @JsonProperty("ApplicationDate")
    private String applicationDate;

    @JsonProperty("SnapshotRates")
    private List<SnapshotRate> snapshotRates;

    @Data
    public static class RateField {
        private String fieldcode;
        private double fieldvalue;
    }
    @Data
    public static class SnapshotRate {
        @JsonProperty("RicID")
        private long ricID;

        @JsonProperty("RICCode")
        private String RICCode;

        @JsonProperty("AssetClassCode")
        private String assetClassCode;

        @JsonProperty("ratefields")
        private List<RateField> ratefields;

        @JsonProperty("midrate")
        private int midrate;
    }
}
