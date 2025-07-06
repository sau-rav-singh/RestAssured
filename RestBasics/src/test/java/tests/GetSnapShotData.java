package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import pojo.SnapshotData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GetSnapShotData {
    private static final double TOLERANCE = 0.9; // Tolerance for comparing numeric values

    public static void main(String[] args) {
        String uatFilePath = "src/test/resources/getSnapShotDatauat.json";
        String prodFilePath = "src/test/resources/getSnapShotDataprod.json";

        SnapshotData uatSnapshotData = getSnapshotData(uatFilePath);
        SnapshotData prodSnapshotData = getSnapshotData(prodFilePath);

        if (uatSnapshotData != null && prodSnapshotData != null) {
            compareResponses(uatSnapshotData, prodSnapshotData);
        }
    }

    private static SnapshotData getSnapshotData(String filePath) {
        try {
            String jsonString = Files.readString(Paths.get(filePath));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, SnapshotData.class);
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
        }
        return null;
    }

    private static void compareResponses(SnapshotData uatSnapshotData, SnapshotData prodSnapshotData) {
        System.out.println("Comparing UAT and Prod Responses...");

        List<SnapshotData.SnapshotRate> uatRates = uatSnapshotData.getSnapshotRates();
        List<SnapshotData.SnapshotRate> prodRates = prodSnapshotData.getSnapshotRates();

        for (SnapshotData.SnapshotRate uatRate : uatRates) {
            String uatRICCode = uatRate.getRICCode();
            SnapshotData.SnapshotRate prodRate = findRateByRICCode(prodRates, uatRICCode);

            if (prodRate != null) {
                if (numericValuesMatch(uatRate, prodRate)) {
                    System.out.println("RICCode: " + uatRICCode + " - UAT and Prod responses match.");
                } else {
                    System.out.println("RICCode: " + uatRICCode + " - UAT and Prod responses differ.");
                }
            } else {
                System.out.println("RICCode: " + uatRICCode + " not found in Prod response.");
            }
        }
    }

    private static boolean numericValuesMatch(SnapshotData.SnapshotRate uatRate, SnapshotData.SnapshotRate prodRate) {
        return uatRate.getMidrate() == prodRate.getMidrate()
                && numericFieldsMatch(uatRate.getRatefields(), prodRate.getRatefields());
    }

    private static boolean numericFieldsMatch(List<SnapshotData.RateField> uatFields, List<SnapshotData.RateField> prodFields) {
        if (uatFields.size() != prodFields.size()) {
            return false;
        }

        for (int i = 0; i < uatFields.size(); i++) {
            SnapshotData.RateField uatField = uatFields.get(i);
            SnapshotData.RateField prodField = prodFields.get(i);

            double uatValue = uatField.getFieldvalue();
            double prodValue = prodField.getFieldvalue();

            if (Math.abs(uatValue - prodValue) > TOLERANCE) {
                return false;
            }
        }
        return true;
    }

    private static SnapshotData.SnapshotRate findRateByRICCode(List<SnapshotData.SnapshotRate> rates, String ricCode) {
        return rates.stream()
                .filter(rate -> ricCode.equals(rate.getRICCode()))
                .findFirst()
                .orElse(null);
    }
}
