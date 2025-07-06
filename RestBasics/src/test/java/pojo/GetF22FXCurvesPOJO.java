package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetF22FXCurvesPOJO {
    private String status;
    private String applicateData;
    private String region;

    @JsonProperty("FXRates")
    private List<FXRate> FXRates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FXRate {
        private String bid;
        private String ask;
        private String asset;
        private String currency;
        private String tenor;
        private String tenorDate;
    }
}
