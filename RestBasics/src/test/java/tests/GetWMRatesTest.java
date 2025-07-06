package tests;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GetWMRatesTest {

    @Test
    public void testCompareRates() {
        try {
            String uatJson = new String(Files.readAllBytes(Paths.get("src/test/resources/getWMRatesUAT.json")));
            String prodJson = new String(Files.readAllBytes(Paths.get("src/test/resources/getWMRatesPROD.json")));
            List<Map<String, Object>> uatData = parseJson(uatJson);
            List<Map<String, Object>> prodData = parseJson(prodJson);
            Map<String, Map<String, Object>> uatMap = createResponseMap(uatData);
            Map<String, Map<String, Object>> prodMap = createResponseMap(prodData);
            compareRates(uatMap, prodMap);
        } catch (IOException e) {
            Assert.fail("Failed to read JSON files: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> parseJson(String jsonResponse) {
        List<Map<String, Object>> responseData = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> responseMap = new HashMap<>();
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    responseMap.put(key, jsonObject.get(key));
                }
                responseData.add(responseMap);
            }
        } catch (Exception e) {
            Assert.fail("Error parsing JSON response: " + e.getMessage());
        }
        return responseData;
    }

    private Map<String, Map<String, Object>> createResponseMap(List<Map<String, Object>> responseList) {
        Map<String, Map<String, Object>> responseMap = new HashMap<>();
        for (Map<String, Object> response : responseList) {
            String key = String.valueOf(response.get("BaseCurrency")) +
                    response.get("ContraCurrency") +
                    response.get("Tenor");
            responseMap.put(key, response);
        }
        return responseMap;
    }

    private void compareRates(Map<String, Map<String, Object>> uatMap, Map<String, Map<String, Object>> prodMap) {
        for (String key : uatMap.keySet()) {
            if (prodMap.containsKey(key)) {
                double uatBid = convertToDouble(uatMap.get(key).get("BID"));
                double prodBid = convertToDouble(prodMap.get(key).get("BID"));
                double uatAsk = convertToDouble(uatMap.get(key).get("ASK"));
                double prodAsk = convertToDouble(prodMap.get(key).get("ASK"));
                Assert.assertEquals(uatBid, prodBid, "BID mismatch for key: " + key);
                Assert.assertEquals(uatAsk, prodAsk, "ASK mismatch for key: " + key);
            } else {
                Assert.fail("Key " + key + " not found in PROD data.");
            }
        }
    }
    private double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            return Double.parseDouble(value.toString());
        }
    }
}
