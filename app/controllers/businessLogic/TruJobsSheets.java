package controllers.businessLogic;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;
import org.apache.commons.lang3.time.DateUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by archana on 7/7/16.
 */
public class TruJobsSheets {

    /**
     * date format for metrics sheets
     */
    private static final String SDF_FORMAT = "dd-MMM";
    private static final SimpleDateFormat sfd = new SimpleDateFormat(SDF_FORMAT);

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME =
            "TruJobsSheets";

    /**
     * ID of the metrics sheet
     */
    private static final String SPREADSHEET_ID = "1FHksj6SSzBBOPYO-jTgRZ3iDoqP5X5doYT76e8T2pOE";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart.json");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart.json
     */
    private static final List<String> SCOPES =
            Arrays.asList(SheetsScopes.SPREADSHEETS);

    /**
     *
     */
    static String userHome = System.getProperty("user.home");

    /**
     *
     */
    private static ArrayList<String> alphabetsList = new ArrayList<String>();

    /**
     *
     */
    private static Sheets service;

    private static Map<String, String> topicToSheetName = new HashMap<String, String>();


    private static HashMap<String, TreeMap<Integer, Object>> sheetToVerticalHeader =
            new HashMap<String, TreeMap<Integer, Object>>();


    private static HashMap<String, TreeMap<String, Object>> sheetToHorizontalHeader =
            new HashMap<String, TreeMap<String, Object>>();

    private static final GregorianCalendar gCal = new GregorianCalendar();

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            initializeAlphabetsList();

            service = getSheetsService();

            topicToSheetName.put(MetricsConstants.METRIC_INPUT_ALL, "All Metrics");
            topicToSheetName.put("Lead Sources", "Lead Sources");
            topicToSheetName.put("Support Metrics", "Support Metrics");

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        FileInputStream fs = new FileInputStream(userHome + "/projects/truserver/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(fs));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     *
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();

        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    /**
     * Reads and stores a range of values from within the metrics sheet
     */
    public static void readVertcialHeader(String sheetName) {

        TreeMap<Integer, Object> verticalHeader = new TreeMap<Integer, Object>();

        try {
            String range = sheetName + "!A1:A1000";
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            } else {
                Integer index = 0;
                for (List row : values) {
                    play.Logger.info("Col value: " + row.toString() + " values " + values.size());
                    index++;
                    verticalHeader.put(index, row.get(0));
                }

                sheetToVerticalHeader.put(sheetName, verticalHeader);
                play.Logger.info(" Vheader " + sheetToVerticalHeader.toString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Reads and stores a range of values from within the metrics sheet
     */
    public static void readHorizontalHeader(String sheetName) {

        TreeMap<String, Object> horizontalHeaderMap = new TreeMap<String, Object>();

        try {
            String range = sheetName + "!A1:AZ1";
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            } else {
                Integer index = 0;
                for (List row : values) {
                    for (Object header : row) {
                        horizontalHeaderMap.put(alphabetsList.get(index), header);
                        index++;
                    }
                }

                sheetToHorizontalHeader.put(sheetName, horizontalHeaderMap);
                play.Logger.info(" Hheader " + sheetToHorizontalHeader.toString());

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     *
     * @param headerToValueMap
     * @param metricDate
     * @param metricCategory
     */
    public static void updateMetricsSheet(Map<String, Object> headerToValueMap, Date metricDate, String metricCategory) {

        try {

            String sheetName = topicToSheetName.get(metricCategory);

            if (sheetName == null) {
                play.Logger.error(" Something went wrong. Could not find sheet corresponding to metrics type " + metricCategory);
                // TODO communicate error to front end
                return;
            }

            // check if row with this date exists, if not create one
            Integer dateRowIndex = createNewRowForDate(sfd.format(metricDate), sheetName);

            updateSingleRow(headerToValueMap, dateRowIndex, sheetName);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void updateSingleMetric(String header, Date metricDate, Object value, String metricCategory)
    {
        try {

            String sheetName = topicToSheetName.get(metricCategory);

            if (sheetName == null) {
                play.Logger.error(" Something went wrong. Could not find sheet corresponding to metrics type " + metricCategory);
                // TODO communicate error to front end
                return;
            }

            // check if row with this date exists, if not create one
            Integer dateRowIndex = createNewRowForDate(sfd.format(metricDate), sheetName);

            Map<String, Object> headerToValueMap = new HashMap<String, Object>();
            headerToValueMap.put(header, value);

            writeSingleMetricToSheet(headerToValueMap, dateRowIndex, sheetName);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void updateSingleRow(Map<String, Object> headerToValueMap, Integer rowIndex, String sheetName)
            throws IOException
    {
        String horizontalRange = getFullRowMatrix(rowIndex, sheetName);
        Iterator<String> headerItr = headerToValueMap.keySet().iterator();
        List<Object> data = new ArrayList<Object>();

        while (headerItr.hasNext()) {

            String headerString = headerItr.next();
            data.add(headerToValueMap.get(headerString));
        }

        play.Logger.info("Updating value  in " + horizontalRange + " data: " + data);
        writeSingleRowToSheet(horizontalRange, data);
    }

    public static void updateRCMatrix(LinkedHashMap<Date, Map<String, Object>> dateToHeaderToValueMap,
                                      String sheetName,
                                      Date startDate,
                                      Date endDate)
    {

        try {
            TreeMap<String, Object> cellAddrToValuesMap =
                    createCellAddressToValuesMap(sheetName, dateToHeaderToValueMap);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static TreeMap<String, Object> createCellAddressToValuesMap(String sheetName,
                                                                        LinkedHashMap<Date, Map<String, Object>> dateToHeaderToValueMap)
            throws IOException
    {
        play.Logger.info("values to update " + dateToHeaderToValueMap.toString());
        TreeMap<String, Object> cellAddrToValues = new TreeMap<String, Object>();
        Iterator<Map.Entry<Date, Map<String, Object>>> parentItr = dateToHeaderToValueMap.entrySet().iterator();

        Date iterationDate;

        // Iterate on date
        while (parentItr.hasNext()) {
        //gCal.setTime(startDate);
        //endDate = DateUtils.addDays(endDate, 1);

        //Date iterationDate;
        //while (gCal.getTime().before(endDate)) {

            //iterationDate = gCal.getTime();

            Map.Entry<Date, Map<String, Object>> entry = parentItr.next();
            iterationDate = entry.getKey();

            Map<String, Object> headerToValueMap = entry.getValue();

            // check if row with this date exists, if not create one
            Integer dateRowIndex = getRowIndexFromVerticalHeaderValue(sheetName, sfd.format(iterationDate));

            play.Logger.info(" DateItr " + iterationDate + " rowIndex " + dateRowIndex);

            // fetch the col name to horizontal header map corresponding to this sheet

            // TODO, right now we are not creating a new column if the required column is not found

            Iterator<String> headerItr = headerToValueMap.keySet().iterator();

            // Iterate on all header values that we need to write
            while (headerItr.hasNext()) {
                String headerString = headerItr.next();

                String colName = getColLabelFromHorizontalHeaderValue(sheetName, headerString);

                if (colName == null) {
                    play.Logger.error(" Error! Couldnt find header with value " + headerString + " . skipping this entry");
                    continue;
                }

                cellAddrToValues.put(colName + dateRowIndex, headerToValueMap.get(headerString));
                play.Logger.info(" Cell addr map " + cellAddrToValues.toString());

            }
            gCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        return cellAddrToValues;
    }

    private static Integer createNewRowForDate (String metricDate, String sheetName) throws IOException
    {
        play.Logger.info(" Scanning for row with date " + metricDate);

        Integer rowCount = getRowIndexFromCellValue(metricDate, sheetName);

        if (rowCount == null) {
            // create row
            play.Logger.info("Creating new row for " + metricDate + " at " + sheetName);
            rowCount = createNewRowAtTheEnd(metricDate, sheetName);
        }

        return rowCount;
    }

    private static Integer createNewRowAtTheEnd (String rowHeader, String sheetName) throws IOException
    {
        // Find last row
        Integer lastRow = getLastRow(sheetName);
        lastRow += 1;

        // Set lastRowCol1 = rowHeader

        List<Object> data = new ArrayList<Object>();
        data.add(rowHeader);

        writeSingleRowToSheet(sheetName + "!A"+lastRow, data);

        return lastRow;
    }

    private static Integer getLastRow(String sheetName)
    {
        Integer lastRow = 0;
        try {

            String range = sheetName + "!A1:A20000";
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();

            List<List<Object>> values = response.getValues();

            if (values == null || values.size() == 0) {
                play.Logger.info("No data found.");
            } else {
                for (List row : values) {
                   if(row.get(0) != null && row.get(0) != "") {
                       play.Logger.info("row: " + row.get(0));
                       lastRow++;
                   }
                    else {
                       break;
                   }
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        play.Logger.info("Last Row " + lastRow);
        return lastRow;
    }

    private static Integer getRowIndexFromVerticalHeaderValue(String sheetName, String date)
    {
        TreeMap<Integer, Object> rowIndexToVerticalHeader = sheetToVerticalHeader.get(sheetName);

        if (rowIndexToVerticalHeader == null) {
            play.Logger.error("Error: couldnt find vertical header map for " + sheetName);
            return null;
        }

        // Iterate on already read and saved map that contains col name to horizontal header
        Set<Map.Entry<Integer, Object>> headerSet = rowIndexToVerticalHeader.entrySet();
        Iterator<Map.Entry<Integer, Object>> headerSetItr = headerSet.iterator();

        Integer rowIndex = -1;
        while (headerSetItr.hasNext()) {
            Map.Entry<Integer, Object> headerEntry = headerSetItr.next();

            // find the entry in the static header map that corresponds to input date
            if (headerEntry.getValue().equals(date)) {
               rowIndex = headerEntry.getKey();
            }
        }

        if (rowIndex == -1) {
            rowIndex = rowIndexToVerticalHeader.size();
            rowIndex++;
            List<Object> data = new ArrayList<Object>();
            data.add(date);

            writeSingleRowToSheet(sheetName + "!A"+rowIndex, data);

            // update the header map with details of new row that we just created
            rowIndexToVerticalHeader.put(rowIndex, date);
        }

        play.Logger.info("Returning row index " + rowIndex + " for " + date);
        return rowIndex;
    }

    private static String getColLabelFromHorizontalHeaderValue (String sheetName, String headerString)
    {
        TreeMap<String, Object> colLabelTohorizontalHeader = sheetToHorizontalHeader.get(sheetName);

        if (colLabelTohorizontalHeader == null) {
            play.Logger.error("Error: couldnt find horizontal header map for " + sheetName);
            return null;
        }

        // Iterate on already read and saved map that contains col name to horizontal header
        Set<Map.Entry<String, Object>> headerSet = colLabelTohorizontalHeader.entrySet();
        Iterator<Map.Entry<String, Object>> headerSetItr = headerSet.iterator();

        String colLabel = null;
        while (headerSetItr.hasNext()) {
            Map.Entry<String, Object> headerEntry = headerSetItr.next();

            // find the entry in the static header map that corresponds to headerString from the
            // map with values to write
            if (headerEntry.getValue().equals(headerString)) {
                colLabel = headerEntry.getKey();
            }
        }

        return colLabel;
    }

    private static void writeSingleMetricToSheet(Map<String, Object> headerToValueMap, Integer rowIndex, String sheetName)
            throws IOException
    {
        Iterator<String> headerItr = headerToValueMap.keySet().iterator();

        while (headerItr.hasNext()) {

            String headerString = headerItr.next();
            Integer colIndex = getColIndexFromHeader(headerString, sheetName);
            String cellAddress = sheetName +"!" + alphabetsList.get(colIndex) + rowIndex.toString();

            List<Object> data = new ArrayList<Object>();
            data.add(headerToValueMap.get(headerString));

            play.Logger.info("Updating value  in " + cellAddress);
            writeSingleRowToSheet(cellAddress, data);
        }
    }

    private static Integer getRowIndexFromCellValue(String anchorText, String sheetName) throws IOException
    {
        String range = sheetName + "!A1:A100";

        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();

        ListIterator itr = values.listIterator();
        List innerList = new ArrayList<Object>();
        List tempList;

        while (itr.hasNext()) {

            tempList = (List) itr.next();
            innerList.add(tempList.get(0));
        }

        Integer indexOfAnchorText = -1;

        if (innerList.contains(anchorText)) {
            indexOfAnchorText = innerList.indexOf(anchorText);
        }

        if (indexOfAnchorText == -1) {
            play.Logger.error("Could not identify anchor text " + anchorText + " in sheet "
                    + SPREADSHEET_ID + " within range " + range);

            return null;
        }

        play.Logger.info("returning index: " + indexOfAnchorText + " for anchor text: " + anchorText);

        return ++indexOfAnchorText;
    }

    private static String getFullRowMatrix(Integer rowIndex, String sheetName) {

        String rowRange = sheetName + "!B" + rowIndex + ":AZ" + rowIndex;

        return rowRange;
    }


    private static void writeSingleRowToSheet(String range, List<Object> values) {
        try {

            play.Logger.info(" Writing data " + values.toString() + " in " + range);

            List<List<Object>> arrData = convertDataToUpdate(values);

            ValueRange oRange = new ValueRange();
            oRange.setRange(range);
            oRange.setValues(arrData);

            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);

            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);

            BatchUpdateValuesResponse oResp1 =
                    service.spreadsheets().values().batchUpdate(SPREADSHEET_ID, oRequest).execute();

        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // convert a unideminsional list of values say [value1, value2, value3.... value4]
    // to two dimensional list List[row1=[value1, value2, value3, value4]]
    // This method is specifically to format data that can be used to update in a single row within the sheet
    private static List<List<Object>> convertDataToUpdate(List<Object> dataToUpdate)
    {
        List<List<Object>> dataParent = new ArrayList<List<Object>>();

        dataParent.add(dataToUpdate);

        return dataParent;
    }

    private static Integer getColIndexFromHeader(String headerText, String sheetName) throws IOException {

        String range = sheetName + "!A1:Z1";

        // read all values in the row-header
        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();

        // The returned data is of type "values": [["header1","header2",...]]
        // so extract the list within the parent list called "values"
        List innerList = values.get(0);
        Integer indexOfHeaderText = -1;

        // find which index col num has the header text we are looking for
        if (innerList.contains(headerText)) {
            indexOfHeaderText = innerList.indexOf(headerText);
        }

        // Do error handling
        if (indexOfHeaderText == -1) {
            play.Logger.error("Could not identify anchor text " + headerText + " in sheet "
                    + SPREADSHEET_ID + " within range " + range);

            return null;
        }

        return indexOfHeaderText;
    }

    private static String getFullColMatrix (Integer colIndex) {

        // fetch col alphabet corresponding to returned index
        String col = alphabetsList.get(colIndex);

        String verticalRange = col + "2:" + col + "500";
        play.Logger.info("returning range: " + verticalRange);

        return verticalRange;
    }


    private static void initializeAlphabetsList() {
        alphabetsList.add("A");
        alphabetsList.add("B");
        alphabetsList.add("C");
        alphabetsList.add("D");
        alphabetsList.add("E");
        alphabetsList.add("F");
        alphabetsList.add("G");
        alphabetsList.add("H");
        alphabetsList.add("I");
        alphabetsList.add("J");
        alphabetsList.add("K");
        alphabetsList.add("L");
        alphabetsList.add("M");
        alphabetsList.add("N");
        alphabetsList.add("O");
        alphabetsList.add("P");
        alphabetsList.add("Q");
        alphabetsList.add("R");
        alphabetsList.add("S");
        alphabetsList.add("T");
        alphabetsList.add("U");
        alphabetsList.add("V");
        alphabetsList.add("W");
        alphabetsList.add("X");
        alphabetsList.add("Y");
        alphabetsList.add("Z");
        alphabetsList.add("AA");
        alphabetsList.add("AB");
        alphabetsList.add("AC");
        alphabetsList.add("AD");
        alphabetsList.add("AE");
        alphabetsList.add("AF");
        alphabetsList.add("AG");
        alphabetsList.add("AH");
        alphabetsList.add("AI");
        alphabetsList.add("AJ");
        alphabetsList.add("AK");
        alphabetsList.add("AL");
        alphabetsList.add("AM");
        alphabetsList.add("AN");
        alphabetsList.add("AO");
        alphabetsList.add("AP");
        alphabetsList.add("AQ");
        alphabetsList.add("AR");
        alphabetsList.add("AS");
        alphabetsList.add("AT");
        alphabetsList.add("AU");
        alphabetsList.add("AV");
        alphabetsList.add("AW");
        alphabetsList.add("AX");
        alphabetsList.add("AY");
        alphabetsList.add("AZ");
    }
}
