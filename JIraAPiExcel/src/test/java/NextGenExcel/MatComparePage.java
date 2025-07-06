package NextGenExcel;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MatComparePage { // 4 usages
    private static final String SEARCH_TEXT = "<<<<FINAL JSON message which would be sent >>>>"; // 3 usages
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); // 1 usage
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH"); // 1 usage

    public static boolean isFilePresent(String filePath) { // 1 usage
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    public static Map<String, JsonNode> extractJSONForAllRuns(String filePath) throws IOException { // 2 usages
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.filter(line -> line.contains(SEARCH_TEXT)).map(MatComparePage::parseJsonSafely).filter(Objects::nonNull).collect(Collectors.toMap(node -> node.get("Region").asText() + "_" + node.get("RunNum").asText(), Function.identity()));
        }
    }

    public static JsonNode parseJsonSafely(String line) { // 1 usage
        try {
            String jsonString = line.substring(line.indexOf(SEARCH_TEXT) + SEARCH_TEXT.length()).trim();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(jsonString);
        } catch (IOException e) {
            logExtentReport(false, "Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    private static String generateRunDetails(JsonNode rootNode) { // 1 usage
        return String.join("_", rootNode.get("Region").asText(), rootNode.get("RunNum").asText(), parseMatfs(rootNode.get("Matfs").asText()), rootNode.get("Matfs").asText());
    }

    public static String parseMatfs(String matfs) { // 1 usage
        LocalDateTime dateTime = LocalDateTime.parse(matfs, INPUT_FORMATTER);
        String suffix = dateTime.getMinute() < 30 ? "_first" : "_second";
        return dateTime.format(OUTPUT_FORMATTER) + suffix;
    }

    public static void compareJsons(Map<String, JsonNode> prodJsons, Map<String, JsonNode> ppJsons) { // 1 usage
        SoftAssert softAssert = new SoftAssert();
        if (prodJsons.size() != ppJsons.size()) {
            logExtentReport(false, "Report has Missing Records. Prod size = " + prodJsons.size() + ", PP size = " + ppJsons.size());
            softAssert.fail();
        }

        prodJsons.forEach((key, prodJson) -> {
            JsonNode ppJson = ppJsons.get(key);
            if (ppJson == null) {
                logExtentReport(false, "Key not found in PP: " + key);
                softAssert.fail("Key not found in PP: " + key);
            } else {
                compareMatLines(key, prodJson.get("MAT_LINES"), ppJson.get("MAT_LINES"), softAssert);
            }
        });

        softAssert.assertAll();
        logExtentReport(true, "Both files are identical");
    }

    private static void compareMatLines(String key, JsonNode prodMatLines, JsonNode ppMatLines, SoftAssert softAssert) {
        Map<String, JsonNode> prodMatLinesMap = createJsonNodeMap(prodMatLines, "Matname");
        Map<String, JsonNode> ppMatLinesMap = createJsonNodeMap(ppMatLines, "Matname");

        compareMaps(key, prodMatLinesMap, ppMatLinesMap, softAssert, (matName, prodMatLine, ppMatLine) -> {
            comparePortfolios(key, matName, prodMatLine.get("Portfolio"), ppMatLine.get("Portfolio"), softAssert);
            compareLimits(key, matName, prodMatLine.get("MAT_ABS_Limits"), ppMatLine.get("MAT_ABS_Limits"), softAssert);
        });
    }

    private static Map<String, JsonNode> createJsonNodeMap(JsonNode jsonArray, String keyField) { // 4 usages
        Map<String, JsonNode> map = new HashMap<>();
        jsonArray.forEach(node -> map.put(node.get(keyField).asText(), node));
        return map;
    }

    private static void comparePortfolios(String key, String matName, JsonNode prodPortfolio, JsonNode ppPortfolio, SoftAssert softAssert) {
        Map<String, JsonNode> prodPortfolioMap = createJsonNodeMap(prodPortfolio, "PortfolioName");
        Map<String, JsonNode> ppPortfolioMap = createJsonNodeMap(ppPortfolio, "PortfolioName");

        compareMaps(matName, prodPortfolioMap, ppPortfolioMap, softAssert, (portfolioName, prodItem, ppItem) -> {
            if (!prodItem.get("Portfolio_MAT_Val").equals(ppItem.get("Portfolio_MAT_Val"))) {
                logPortfolioMismatch(key, matName, portfolioName, prodItem, ppItem, softAssert);
            }
        });
    }

    private static void compareLimits(String key, String matName, JsonNode prodLimits, JsonNode ppLimits, SoftAssert softAssert) { // 1 usage
        if (!prodLimits.equals(ppLimits)) {
            String logMessage = String.format("Failed Comparison Run Details: No Mismatch in MAT_ABS_Limits. MatName: %s, BR>Prod MAT_ABS_Limits: %s BR>PP MAT_ABS_Limits: %s", matName, prodLimits, ppLimits);
            logExtentReport(false, logMessage);
            softAssert.fail(logMessage);
        }
    }

    private static void logPortfolioMismatch(String key, String matName, String portfolioName, JsonNode prodItem, JsonNode ppItem, SoftAssert softAssert) { // 1 usage
        String logMessage = String.format("Failed Comparison Run Details: Key: %s, MatName: %s, PortfolioName: %s, <br>Prod Portfolio_MAT_Val: %s<br>Mismatched PP Portfolio_MAT_Val: %s", key, matName, portfolioName, prodItem.get("Portfolio_MAT_Val"), ppItem.get("Portfolio_MAT_Val"));
        logExtentReport(false, logMessage);
        softAssert.fail(logMessage);
    }

    private static <T> void compareMaps(String entityName, Map<String, T> prodMap, Map<String, T> ppMap, SoftAssert softAssert, TriConsumer<String, T, T> comparisonLogic) {
        if (prodMap.size() != ppMap.size()) {
            logExtentReport(false, String.format("%s size mismatch. Prod size = %d, PP size = %d", entityName, prodMap.size(), ppMap.size()));
            softAssert.fail();
        }

        prodMap.forEach((name, prodItem) -> {
            T ppItem = ppMap.get(name);
            if (ppItem != null) {
                comparisonLogic.accept(name, prodItem, ppItem);
            } else {
                logExtentReport(false, String.format("%s not found in PP: %s", name, entityName));
                softAssert.fail(String.format("%s not found in PP: %s", name, entityName));
            }
        });

        ppMap.keySet().stream().filter(name -> !prodMap.containsKey(name)).forEach(name -> {
            logExtentReport(false, String.format("%s not found in Prod: %s", name, entityName));
            softAssert.fail(String.format("%s not found in Prod: %s", name, entityName));
        });
    }

    @FunctionalInterface
    private interface TriConsumer<T, U, V> { // 1 usage
        void accept(T t, U u, V v); // 1 usage
    }

    public static void logExtentReport(boolean bool, String message) {
        System.out.println(message + " is " + bool);
    }
}
