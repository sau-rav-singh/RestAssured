package NextGenExcel;

import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static NextGenExcel.MatComparePage.logExtentReport;

public class FinreconComparatorPage {

    private static final Pattern FILE_PATTERN = Pattern.compile("^.*\\.(d|D)\\{1}\\d{2}(1|2)\\d{2}\\.(txt|TXT)$");
    private static final List<String> HEADER_FIELDS = Arrays.asList("HEADER", "MATS DATE", "TRADE BRANCH", "TRADE PORTFOLIO", "FLOW CURRENCY", "FLOW TYPE",
            "FLOW AMOUNT", "OUR FLOW AMOUNT", "MARKET RATE COUP", "TOTAL FLOW GROSS", "TOTAL FLOW USD GROSS", "RATE TYPE", "TD MARKET RATE COUP",
            "TD TOTAL FLOW GROSS", "TD TOTAL FLOW USD GROSS", "TD MARKET RATE COUP USD", "TOTAL FLOW AMOUNT CLOSING USD", "TOTAL FLOW AMOUNT CLOSING NAT",
            "MARKET RATE COUP USD", "TOTAL FLOW AMOUNT OPENING USD", "TOTAL FLOW AMOUNT OPENING NAT", "TOTAL FLOW AMOUNT CLOSING", "TOTAL FLOW AMOUNT OPENING");
    private static final List<String> TRADE_HEADER_FIELDS = Arrays.asList("TRADE BRANCH", "TRADE PORTFOLIO", "FLOW CURRENCY", "FLOW TYPE", "FLOW AMOUNT", "OUR FLOW AMOUNT",
            "MARKET RATE COUP", "PRIMARY CCY", "SECONDARY PRIMARY", "NATIONAL CURRENCY", "TRADE DATE", "TRADING DATE", "VALUE DATE",
            "MATS DUE DATE", "TD CROSS RATE", "TD MARKET FACTOR", "TD PV REALISED", "TD REALIZED", "TD MARGIN REQD", "TD NOTIONAL CCY", "TD NOTIONAL DEAL CCY",
            "TD OUR DAILY PNL", "TD OUR DAILY PROFIT", "PNL CCY", "TD OUR TRADE PROFIT", "NEW TRADE PNL", "EVENT TYPE", "TRADE EVENT PNL", "TD ORIGINAL DEAL CCY",
            "TD ORIGINAL NOTIONAL", "TD ORIGINAL NOTIONAL CCY", "TD ORIGINAL TRADE PROFIT", "INTERNAL CORP", "TD INTERNAL CORP", "CUSTOMER CORP", "TD CUSTOMER CORP",
            "TD INSTRUMENT", "TD FUTURE CONTRACT", "TD FUTURE CLOSING PRICE", "TD FX SWAP RATE", "TD FX SWAP SALES MARGIN", "INTERNAL CURRENCY", "TD INTERNAL CURRENCY",
            "TD NATIONAL CURRENCY", "TD CUSTOMER CURRENCY", "TD PRICE DATE");

    public static Map<String, Path> fetchReportFile(String directory, String fileType, String region) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().contains(fileType))
                    .filter(file -> file.getFileName().toString().contains(region))
                    .collect(Collectors.toMap(path -> {
                        String fileName = path.getFileName().toString();
                        Matcher matcher = FILE_PATTERN.matcher(fileName);
                        if (matcher.find()) {
                            return matcher.group();
                        }
                        return fileName;
                    }, Function.identity()));
        } catch (NoSuchFileException e) {
            throw new IllegalArgumentException("Invalid file directory: " + directory, e);
        }
    }
    public static void compareFilesAndAddToReport(Path file1, Path file2, String fileType) throws IOException {
        List<String> prodFileLines = Files.readAllLines(file1);
        List<String> ppFileLines = Files.readAllLines(file2);

        SoftAssert softAssert = new SoftAssert();

        Map<String, String> prodFileMap = createFileMap(prodFileLines, fileType);
        Map<String, String> ppFileMap = createFileMap(ppFileLines, fileType);

        compareHeaders(prodFileLines.get(0), ppFileLines.get(0), softAssert);
        checkKeyPresence(prodFileMap, ppFileMap, softAssert);
        compareFileValues(prodFileMap, ppFileMap, fileType, softAssert);
        compareLastRow(prodFileLines, ppFileLines, softAssert);

        softAssert.assertAll();
        logExtentReport(true, fileType + " files are identical");
    }

    // Creates Map with Unique identifiers for each row depending on the Report Type
    private static Map<String, String> createFileMap(List<String> fileLines, String fileType) {
        Map<String, String> fileMap = new HashMap<>();
        if (fileLines.size() > 1) {
            String[] headers = fileLines.get(0).split("\\|");
            for (int i = 1; i < fileLines.size(); i++) {
                String[] columns = fileLines.get(i).split("\\|");
                String key = "";
                if (fileType.equals("TRDCP")) {
                    key = columns[0] + "|" + columns[1] + "|" + columns[2] + "|" + columns[3] + "|" + columns[4];
                } else {
                    key = fileLines.get(i); // Use the whole line as key for other file types
                }
                fileMap.put(key, fileLines.get(i));
            }
        }
        return fileMap;
    }

    private static void checkKeyPresence(Map<String, String> prodFileMap, Map<String, String> ppFileMap, SoftAssert softAssert) {
        if (prodFileMap.size() != ppFileMap.size()) {
            logExtentReport(false, "Key size mismatch in both files");
            softAssert.fail("Key size mismatch in both files");
        }

        for (String key : ppFileMap.keySet()) {
            if (!prodFileMap.containsKey(key)) {
                logExtentReport(false, "Trade " + key + " not found in Prod file.");
                softAssert.fail("Trade " + key + " not found in Prod file.");
            }
        }

        for (String key : prodFileMap.keySet()) {
            if (!ppFileMap.containsKey(key)) {
                logExtentReport(false, "Trade " + key + " not found in PP file.");
                softAssert.fail("Trade " + key + " not found in PP file.");
            }
        }
    }

    private static void compareHeaders(String prodHeader, String ppHeader, SoftAssert softAssert) {
        if (!Objects.equals(prodHeader, ppHeader)) {
            logExtentReport(false, "Headers data mismatch. Prod value: " + prodHeader + ", PP value: " + ppHeader);
            softAssert.fail("Headers data mismatch. Prod value: " + prodHeader + ", PP value: " + ppHeader);
        }
    }



    private static void compareFileValues(Map<String, String> prodFileMap, Map<String, String> ppFileMap, String fileType, SoftAssert softAssert) {
        List<String> headersList = (fileType.equals("TRDCP")) ? TRADE_HEADER_FIELDS : HEADER_FIELDS;
        int columnCount = headersList.size();

        for (String key : prodFileMap.keySet()) {
            if (ppFileMap.containsKey(key)) {
                String prodRow = prodFileMap.get(key);
                String ppRow = ppFileMap.get(key);
                String[] prodColumns = prodRow.split("\\|");
                String[] ppColumns = ppRow.split("\\|");

                if (prodColumns.length != ppColumns.length) {
                    logExtentReport(false, "Column count mismatch for Key: " + key + ". Prod columns: " + prodColumns.length + ", PP columns: " + ppColumns.length);
                    softAssert.fail("Column count mismatch for Key: " + key + ". Prod columns: " + prodColumns.length + ", PP columns: " + ppColumns.length);
                    continue;
                }

                for (int i = 0; i < columnCount; i++) {
                    String prodValue = (i < prodColumns.length) ? prodColumns[i].trim() : "";
                    String ppValue = (i < ppColumns.length) ? ppColumns[i].trim() : "";
                    if (!prodValue.equals(ppValue)) {
                        logExtentReport(false, String.format("Data mismatch for Key: %s, Field: %s. Prod value: %s, PP value: %s", key, headersList.get(i), prodValue, ppValue));
                        softAssert.fail(String.format("Data mismatch for Key: %s, Field: %s. Prod value: %s, PP value: %s", key, headersList.get(i), prodValue, ppValue));
                    }
                }
            }
        }
    }

    private static void compareLastRow(List<String> prodFileLines, List<String> ppFileLines, SoftAssert softAssert) {
        if (prodFileLines.size() > 1 && ppFileLines.size() > 1) {
            String prodLastRow = prodFileLines.get(prodFileLines.size() - 1).trim();
            String ppLastRow = ppFileLines.get(ppFileLines.size() - 1).trim();
            if (!prodLastRow.equals(ppLastRow)) {
                logExtentReport(false, "Last row data mismatch. Prod value: " + prodLastRow + ", PP value: " + ppLastRow);
                softAssert.fail("Last row data mismatch. Prod value: " + prodLastRow + ", PP value: " + ppLastRow);
            }
        }
    }
}
