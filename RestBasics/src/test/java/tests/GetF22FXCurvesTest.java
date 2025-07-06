package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.GetF22FXCurvesPOJO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetF22FXCurvesTest {

    @Test
    public void testFXCurvesComparison() {
        try {
            GetF22FXCurvesPOJO uatResponse = new ObjectMapper().readValue(new String(Files.readAllBytes(Paths.get("src/test/resources/fxCurveUAT.json"))), GetF22FXCurvesPOJO.class);
            GetF22FXCurvesPOJO prodResponse = new ObjectMapper().readValue(new String(Files.readAllBytes(Paths.get("src/test/resources/fxCurvePROD.json"))), GetF22FXCurvesPOJO.class);

            Map<String, GetF22FXCurvesPOJO.FXRate> uatRateMap = getMapOfFXRatesData(uatResponse);
            Map<String, GetF22FXCurvesPOJO.FXRate> prodRateMap = getMapOfFXRatesData(prodResponse);

            for (String key : uatRateMap.keySet()) {
                if (prodRateMap.containsKey(key)) {
                    GetF22FXCurvesPOJO.FXRate uatRate = uatRateMap.get(key);
                    GetF22FXCurvesPOJO.FXRate prodRate = prodRateMap.get(key);
                    if (!uatRate.getBid().equals(prodRate.getBid()) || !uatRate.getAsk().equals(prodRate.getAsk())) {
                        Assert.fail("Mismatch found for key: " + key + "\nUAT Bid: " + uatRate.getBid() + ", Prod Bid: " + prodRate.getBid() + "\nUAT Ask: " + uatRate.getAsk() + ", Prod Ask: " + prodRate.getAsk());
                    }
                } else {
                    Assert.fail("Key missing in Prod response: " + key);
                }
            }
            for (String key : prodRateMap.keySet()) {
                if (!uatRateMap.containsKey(key)) {
                    Assert.fail("Key missing in UAT response: " + key);
                }
            }
        } catch (IOException e) {
            Assert.fail("Failed to read JSON file: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("An unexpected error occurred: " + e.getMessage());
        }
    }

    private Map<String, GetF22FXCurvesPOJO.FXRate> getMapOfFXRatesData(GetF22FXCurvesPOJO parsedResponse) {
        Map<String, GetF22FXCurvesPOJO.FXRate> parsedResponseMap = new HashMap<>();
        List<GetF22FXCurvesPOJO.FXRate> allFXRates = parsedResponse.getFXRates();
        for (GetF22FXCurvesPOJO.FXRate fxRate : allFXRates) {
            String key = fxRate.getCurrency() + fxRate.getTenor();
            parsedResponseMap.put(key, fxRate);
        }
        return parsedResponseMap;
    }
}
