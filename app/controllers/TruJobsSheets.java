package controllers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private static ArrayList<Character> alphabetsList = new ArrayList<Character>();

    /**
     *
     */
    private static Sheets service;

    private static Map<String, String> topicToSheetName = new HashMap<String, String>();



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
        FileInputStream fs = new FileInputStream(userHome + "/truserver/client_secret.json");
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
     * Prints a range of values from within the metrics sheet
     */
    public static void printMetricsSheet(String sheetName, String rcMatrix) {

        try {
            // Build a new authorized API client service.
            Sheets service = getSheetsService();

            String range = sheetName + rcMatrix;
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            } else {
                for (List row : values) {
                    // Print columns
                    System.out.printf("%s, %s, %s\n", row.get(0), row.get(1), row.get(2));
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     *
     * @param mapOfHeaderToValueMap
     * @param metricDate
     * @param metricCategory
     */
    public static void updateMetricsSheet(Map<String, Map<String, Object>> mapOfHeaderToValueMap, Date metricDate, String metricCategory) {

        for (Map.Entry<String,  Map<String, Object>> eachHeaderToValueMap : mapOfHeaderToValueMap.entrySet())
        {
            Map<String, Object> headToValueMap = eachHeaderToValueMap.getValue();

            try {
                String sheetName = topicToSheetName.get(metricCategory);

                if (sheetName == null) {
                    play.Logger.error(" Something went wrong. Could not find sheet corresponding to metrics type " + metricCategory);
                    // TODO communicate error to front end
                    return;
                }

                // check if row with this date exists, if not create one
                Integer dateRowIndex = createNewRowForDate(sfd.format(metricDate), sheetName);

                updateAllMetrics(headToValueMap, dateRowIndex, sheetName);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
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

            updateMetric(headerToValueMap, dateRowIndex, sheetName);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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

        updateRange(sheetName + "!A"+lastRow, data);

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

    private static void updateAllMetrics(Map<String, Object> headerToValueMap, Integer rowIndex, String sheetName) throws IOException
    {
        String horizontalRange = getFullRowMatrix(rowIndex, sheetName);
        Iterator<String> headerItr = headerToValueMap.keySet().iterator();
        //updateRange(horizontalRange, values);
        List<Object> data = new ArrayList<Object>();

        while (headerItr.hasNext()) {

            String headerString = headerItr.next();
            //Integer colIndex = getColIndexFromHeader(headerString, sheetName);
            //String cellAddress = alphabetsList.get(colIndex) + rowIndex.toString();

            data.add(headerToValueMap.get(headerString));
        }


        play.Logger.info("Updating value  in " + horizontalRange + " data: " + data);
        updateRange(horizontalRange, data);
    }

    private static void updateMetric(Map<String, Object> headerToValueMap, Integer rowIndex, String sheetName) throws IOException
    {
        Iterator<String> headerItr = headerToValueMap.keySet().iterator();

        while (headerItr.hasNext()) {

            String headerString = headerItr.next();
            Integer colIndex = getColIndexFromHeader(headerString, sheetName);
            String cellAddress = sheetName +"!" + alphabetsList.get(colIndex) + rowIndex.toString();

            List<Object> data = new ArrayList<Object>();
            data.add(headerToValueMap.get(headerString));

            play.Logger.info("Updating value  in " + cellAddress);
            updateRange(cellAddress, data);
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


    private static void updateRange(String range, List<Object> values) {
        try {

            List<List<Object>> arrData = formatUpdateData(values);

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
    private static List<List<Object>> formatUpdateData (List<Object> dataToUpdate)  {

        /*List<Object> dataChild1 = new ArrayList<>();
        dataChild1.add ("Archana");
        dataChild1.add ("30");
        dataChild1.add ("wow");*/

        /*List<Object> dataChild2 = new ArrayList<>();
        dataChild2.add ("Arun");
        dataChild2.add ("31");*/

        List<List<Object>> dataParent = new ArrayList<List<Object>>();

        /*ListIterator dataItr = dataToUpdate.listIterator();

        while(dataItr.hasNext()) {
            Object item = dataItr.next();
            List<Object> itemList = new ArrayList<Object>();
            itemList.add(item);
        }*/

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
        Character col = alphabetsList.get(colIndex);

        String verticalRange = col + "2:" + col + "500";
        play.Logger.info("returning range: " + verticalRange);

        return verticalRange;
    }


    private static void initializeAlphabetsList() {
        alphabetsList.add('A');
        alphabetsList.add('B');
        alphabetsList.add('C');
        alphabetsList.add('D');
        alphabetsList.add('E');
        alphabetsList.add('F');
        alphabetsList.add('G');
        alphabetsList.add('H');
        alphabetsList.add('I');
        alphabetsList.add('J');
        alphabetsList.add('K');
        alphabetsList.add('L');
        alphabetsList.add('M');
        alphabetsList.add('N');
        alphabetsList.add('O');
        alphabetsList.add('P');
        alphabetsList.add('Q');
        alphabetsList.add('R');
        alphabetsList.add('S');
        alphabetsList.add('T');
        alphabetsList.add('U');
        alphabetsList.add('V');
        alphabetsList.add('W');
        alphabetsList.add('X');
        alphabetsList.add('Y');
        alphabetsList.add('Z');
    }
}
