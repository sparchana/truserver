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
import com.google.api.services.sheets.v4.model.*;

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
     * ID of the metrics sheet for test env
     */
    private static final String SPREADSHEET_ID_TEST = "1FHksj6SSzBBOPYO-jTgRZ3iDoqP5X5doYT76e8T2pOE";

    /**
     * ID of the metrics sheet for prod env
     */
    private static final String SPREADSHEET_ID_PROD = "1H4S2ncLRJUyRBxUFvA-jW3B6cMcp-pHbZ58w4";

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

    private static String spreadSheetId;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            initializeAlphabetsList();

            service = getSheetsService();

            // redudant right now as we are maintaining same names for input parameters and sheet names
            topicToSheetName.put(MetricsConstants.METRIC_INPUT_SUMMARY, MetricsConstants.METRIC_INPUT_SUMMARY);
            topicToSheetName.put(MetricsConstants.METRIC_INPUT_LEAD_SOURCES, MetricsConstants.METRIC_INPUT_LEAD_SOURCES);
            topicToSheetName.put(MetricsConstants.METRIC_INPUT_SUPPORT, MetricsConstants.METRIC_INPUT_SUPPORT);
            topicToSheetName.put(MetricsConstants.METRIC_INPUT_ACTIVE_CANDIDATES, MetricsConstants.METRIC_INPUT_ACTIVE_CANDIDATES);


            boolean isDevMode = true; //play.api.Play.isDev(play.api.Play.current());

            spreadSheetId = isDevMode ? SPREADSHEET_ID_TEST : SPREADSHEET_ID_PROD;

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

        boolean isDevMode = true; //play.api.Play.isDev(play.api.Play.current());
        String clientSecretFile = isDevMode? "client_secret.json" : "client_secret_prod.json";

        // Load client secrets.
        FileInputStream fs = new FileInputStream(userHome + "/truserver/" + clientSecretFile);
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
     * Reads and stores the vertical range of values from within the given sheet
     */
    private static void readVertcialHeader(String sheetName) {

        TreeMap<Integer, Object> verticalHeader = new TreeMap<Integer, Object>();

        try {
            String range = sheetName + "!A1:A1000";
            ValueRange response = service.spreadsheets().values()
                    .get(spreadSheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                play.Logger.info("No vertical data header found in sheet " + sheetName);
            } else {
                Integer index = 0;
                for (List row : values) {
                    index++;
                    verticalHeader.put(index, row.get(0));
                }

                sheetToVerticalHeader.put(sheetName, verticalHeader);
                play.Logger.debug(" Vheader " + sheetToVerticalHeader.toString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Reads and stores a range of values from within the metrics sheet
     */
    private static void readHorizontalHeader(String sheetName) {

        TreeMap<String, Object> horizontalHeaderMap = new TreeMap<String, Object>();

        try {
            String range = sheetName + "!A1:AZ1";
            ValueRange response = service.spreadsheets().values()
                    .get(spreadSheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                play.Logger.info("No horizontal data header found in sheet " + sheetName);
            } else {
                Integer index = 0;
                for (List row : values) {
                    for (Object header : row) {
                        horizontalHeaderMap.put(alphabetsList.get(index), header);
                        index++;
                    }
                }

                sheetToHorizontalHeader.put(sheetName, horizontalHeaderMap);
                play.Logger.debug(" Hheader " + sheetToHorizontalHeader.toString());

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void writeMissingVerticalHeaders (String sheetName, Map<Date, Map<Integer, Map<String, Object>>> dateMap)
    {
        // read existing header data
        readVertcialHeader(sheetName);

        // retrieve already read vertical header data
        TreeMap<Integer, Object> existingVerticalHeaderData = sheetToVerticalHeader.get(sheetName);
        ArrayList<List<Object>> missingHeaders = new ArrayList<List<Object>>();


        Iterator<Map.Entry<Date, Map<Integer, Map<String, Object>>>> dateMapItr = dateMap.entrySet().iterator();

        while (dateMapItr.hasNext()) {
            Map.Entry<Date, Map<Integer, Map<String, Object>>> entry = dateMapItr.next();
            String header = sfd.format(entry.getKey());

            if (existingVerticalHeaderData.containsValue(header)) {
                continue;
            }
            else {
                int countOfSameHeader = entry.getValue().keySet().size();

                while (countOfSameHeader > 0) {
                    ArrayList<Object> singleRow = new ArrayList<>();
                    singleRow.add(header);
                    missingHeaders.add(singleRow);
                    --countOfSameHeader;

                    play.Logger.info(" adding to missing list " + header);
                }
            }
        }

        int lastRowIndex = existingVerticalHeaderData.size();
        lastRowIndex++;

        String range = sheetName + "!A" + lastRowIndex + ":A" + lastRowIndex + missingHeaders.size();

        writeRangeToSheet(range, missingHeaders);

        // update the header map with details of new row that we just created
        readVertcialHeader(sheetName);
    }

    /**
     *
     * @param metricToDateToCountToHeaderToValueMap has values in the following construct:
     *                                              {Metric_type->{Date->{RowCount->{Header->Values}}}}
     */
        public static void updateMetricsSheet(
                Map<String,Map<Date, Map<Integer, Map<String, Object>>>> metricToDateToCountToHeaderToValueMap)
    {
        // iterate on all metric types that we need to update. Example (All_Metrics, Lead_sources etc)
        for (Map.Entry<String, Map<Date,  Map<Integer, Map<String, Object>>>> metricMapEntry :
                metricToDateToCountToHeaderToValueMap.entrySet())
        {
            String metricCategory = metricMapEntry.getKey();

            // Get the tab name within the spreadsheet that correspond to the given metric category
            String sheetName = topicToSheetName.get(metricCategory);

            if (sheetName == null) {
                play.Logger.error(" Something went wrong. Could not find sheet corresponding to metrics type "
                        + metricCategory);

                // TODO communicate error to front end
                return;
            }

            // Get the all the values to be updated within the tab
            // E.g., {19-Jun ->{Total Leads-> 100},{Total Candidates -> 200}}
            Map<Date, Map<Integer, Map<String, Object>>> dateMap = metricMapEntry.getValue();

            // Aggregate queries are the ones that have a single date entry for a range of values
            // eg, how many candidates where created by every support agent in a day
            boolean isAggregateQuery = true;


            if (sheetName.equals(MetricsConstants.METRIC_INPUT_ACTIVE_CANDIDATES)) {
                // The query active_candidates is not an aggregate query, as we will write multiple date rows per
                // date . each row will have details corresponding to a candidate that was active that day
                isAggregateQuery = false;

                // TODO : delete existing rows with given dates
                //deleteRowsWithDates(dateToHeaderToValueMap.keySet());
            }
            else {
                // In case of aggregate queries, we need to make sure of the following:
                // 1. should have only one row per date entry
                // 2. if a date row already exists, then need to retain that row and remember that index
                writeMissingVerticalHeaders(sheetName, dateMap);
            }

            try {
                findRangeAndUpdate(sheetName, dateMap, isAggregateQuery);
            }
            catch (IOException ioEX) {
                ioEX.printStackTrace();
            }
        }
    }

    private static void findRangeAndUpdate(String sheetName,
                                                     Map<Date, Map <Integer, Map<String, Object>>> dateToIndexToHeaderToValueMap,
                                                     boolean isAggregateQuery)
            throws IOException
    {
        play.Logger.info("Values to update " + dateToIndexToHeaderToValueMap.toString());

        // Mapping between row index and the corresponding value list
        // Required esp for aggregate queries where we may have cases to update a date row that already exists
        // and is not soretd wrt to the entire range of dates that we are itertaing now
        TreeMap<Integer, List<Object>> rowIndexToValuesMap = new TreeMap<>();

        // Holds the entire list of objects corresponding to list of rows we need to update
        List<List<Object>> valuesList = new ArrayList<List<Object>>();

        // some helper variables
        Date iterationDate;
        int numHeaders = 0;
        Integer dateRowIndex = 1;

        Iterator<Map.Entry<Date, Map< Integer, Map<String, Object>>>> parentItr =
                  dateToIndexToHeaderToValueMap.entrySet().iterator();

        // Iterate on each date map entyr
        while (parentItr.hasNext()) {

            Map.Entry<Date, Map<Integer, Map<String, Object>>> entry = parentItr.next();

            // Get the iteration date
            iterationDate = entry.getKey();

            // Get the data corresponding to each date. for aggregate queries we will have only one row per dat
            // for non-aggregate queries we will have multiple rows per date
            Map<Integer, Map <String, Object>> countToHeaderToValueMap = entry.getValue();

            Iterator<Map.Entry<Integer, Map <String, Object>>> countToHeaderToValueItr =
                    countToHeaderToValueMap.entrySet().iterator();

            // iterate on each row corressponding to a date
            while (countToHeaderToValueItr.hasNext()) {

                Map.Entry<Integer, Map<String, Object>> countToHeaderToValueEntry = countToHeaderToValueItr.next();

                // Get the colheaderToValue entre for each row
                Map<String, Object> headerToValueMap = countToHeaderToValueEntry.getValue();

                // will be used to hold a list of values corresponding to each row
                List<Object> innerValuesList = new ArrayList<Object>();

                if (isAggregateQuery) {
                    // check if row with this date exists, if not create one
                    dateRowIndex = getRowIndexFromVerticalHeaderValue(sheetName, sfd.format(iterationDate));
                }
                else {
                    // for aggregate queries, dont retrieve existing row index. we are going to write the
                    // vertical header (date) value also along with each row
                    ++dateRowIndex;
                    innerValuesList.add(sfd.format(iterationDate));
                }

                Iterator<String> headerItr = headerToValueMap.keySet().iterator();
                numHeaders = headerToValueMap.keySet().size();

                // Iterate on all header values that we need to write
                while (headerItr.hasNext()) {
                    String headerString = headerItr.next();

                    // fetch the col name to horizontal header map corresponding to this sheet
                    // TODO, right now we are not creating a new column if the required column is not found
                    String colName = getColLabelFromHorizontalHeaderValue(sheetName, headerString);

                    if (colName == null) {
                        play.Logger.error(" Error! Couldnt find header with value " + headerString + " . skipping this entry");
                        continue;
                    }

                    // add the current value as an entry to the list corresponding to a single row
                    innerValuesList.add(headerToValueMap.get(headerString) == null ? "" : headerToValueMap.get(headerString));

                }

                // remember the relationship between the row index and the corresponding row values
                rowIndexToValuesMap.put(dateRowIndex, innerValuesList);
            }

            gCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        // TODO: get rid of startcol hardcoding
        String startCol = "B";
        if (!   isAggregateQuery) {
            startCol = "A";
        }

        // Form the range to which we need to write, by using the first key and last key of the rowIndexToValuesMap
        String range = sheetName + "!" + startCol + rowIndexToValuesMap.firstKey()
                + ":" + alphabetsList.get(numHeaders) + rowIndexToValuesMap.lastKey();

        // create a list of object list. This is the format required to do a bulk write to the sheet
        valuesList.addAll(rowIndexToValuesMap.values());

        play.Logger.info(" Writing Range: " + range + " values " + valuesList.toString());

        writeRangeToSheet(range, valuesList);
    }

    // Not currently used
    // Can be used in the future for a use case where we want to update a single cell of metric within a sheet
    private static void updateSingleMetric(String header, Date metricDate, Object value, String metricCategory)
    {
        try {

            String sheetName = topicToSheetName.get(metricCategory);

            if (sheetName == null) {
                play.Logger.error(" Something went wrong. Could not find sheet corresponding to metrics type " + metricCategory);
                // TODO communicate error to front end
                return;
            }

            // check if row with this date exists, if not create one
            Integer dateRowIndex = returnRowIndexForDate(sfd.format(metricDate), sheetName);

            Map<String, Object> headerToValueMap = new HashMap<String, Object>();
            headerToValueMap.put(header, value);

            writeSingleMetricToSheet(headerToValueMap, dateRowIndex, sheetName);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Not currently used
    // Can be used in the future for a use case where we want to update a single row of metric within a sheet
    private static void updateSingleRowMetrics(Map<String, Object> headerToValueMap, Integer rowIndex, String sheetName)
            throws IOException
    {
        String horizontalRange = getFullHorizontalRange(rowIndex, sheetName);
        Iterator<String> headerItr = headerToValueMap.keySet().iterator();

        List<Object> data = new ArrayList<Object>();

        while (headerItr.hasNext()) {

            String headerString = headerItr.next();
            data.add(headerToValueMap.get(headerString));
        }


        play.Logger.info("Updating value  in " + horizontalRange + " data: " + data);
        writeSingleRowToSheet(horizontalRange, data);
    }


    // checks whether the given date exists within the vertical header of a sheet.
    // if exists, returns the row index, if it doesnt then a new row is created and index is returned
    private static Integer returnRowIndexForDate (String metricDate, String sheetName) throws IOException
    {
        play.Logger.debug(" Scanning for row with date " + metricDate);

        Integer rowCount = getRowIndexFromCellValue(metricDate, sheetName);

        if (rowCount == null) {
            // create row
            play.Logger.info("Creating new row for " + metricDate + " at " + sheetName);
            rowCount = createNewRowAtTheEnd(metricDate, sheetName);
        }

        return rowCount;
    }

    // adds a new row with vertical/row header (col A) updated with the given date (rowHeader)
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

    // returns the last row with data within the given sheet
    private static Integer getLastRow(String sheetName)
    {
        Integer lastRow = 0;
        try {

            String range = sheetName + "!A1:A20000";
            ValueRange response = service.spreadsheets().values()
                    .get(spreadSheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();

            if (values == null || values.size() == 0) {
                play.Logger.info("No data found.");
            } else {
                for (List row : values) {
                   if(row.get(0) != null && row.get(0) != "") {
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

        return lastRow;
    }

    // Reads the pre-populated map of vertical headers fo the given sheet and returns the
    // index at which the given date occurs
    // if the date was not found in the sheet, a new row is created with this header value and the static map
    // is also updated
    private static Integer getRowIndexFromVerticalHeaderValue(String sheetName, String date)
    {
        TreeMap<Integer, Object> rowIndexToVerticalHeader = sheetToVerticalHeader.get(sheetName);

        Integer rowIndex = -1;

        if (rowIndexToVerticalHeader == null) {
            readVertcialHeader(sheetName);
            rowIndexToVerticalHeader = sheetToVerticalHeader.get(sheetName);
        }

        // Iterate on already read and saved map that contains col name to horizontal header
        Set<Map.Entry<Integer, Object>> headerSet = rowIndexToVerticalHeader.entrySet();
        Iterator<Map.Entry<Integer, Object>> headerSetItr = headerSet.iterator();


        while (headerSetItr.hasNext()) {
            Map.Entry<Integer, Object> headerEntry = headerSetItr.next();

            // find the entry in the static header map that corresponds to input date
            if (headerEntry.getValue().equals(date)) {
                rowIndex = headerEntry.getKey();
            }
        }

        // create a new row at the end if we dont have the given date in the sheet
        if (rowIndex == -1) {
            rowIndex = rowIndexToVerticalHeader.size();
            rowIndex++;
            List<Object> data = new ArrayList<Object>();
            data.add(date);

            writeSingleRowToSheet(sheetName + "!A" + rowIndex, data);

            // update the header map with details of new row that we just created
            rowIndexToVerticalHeader.put(rowIndex, date);
        }
        return rowIndex;
    }

    // Reads the pre-populated map of horizontal headers of the given sheet and returns the
    // index at which the given header text occurs
    private static String getColLabelFromHorizontalHeaderValue (String sheetName, String headerString)
    {
        TreeMap<String, Object> colLabelTohorizontalHeader = sheetToHorizontalHeader.get(sheetName);

        if (colLabelTohorizontalHeader == null) {
            readHorizontalHeader(sheetName);
            colLabelTohorizontalHeader = sheetToHorizontalHeader.get(sheetName);
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

        // TODO create a new column in case the given column was not found in th sheet

        return colLabel;
    }

    // Searches for the given text value within the vertical header (A1: A500) and
    // returns the row index where the text occurs
    private static Integer getRowIndexFromCellValue(String anchorText, String sheetName) throws IOException
    {
        String range = sheetName + "!A1:A1000";

        ValueRange response = service.spreadsheets().values()
                .get(spreadSheetId, range)
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
                    + spreadSheetId + " within range " + range);

            return null;
        }

        play.Logger.debug("returning index: " + indexOfAnchorText + " for anchor text: " + anchorText);

        return ++indexOfAnchorText;
    }


    // Helper method. Not currently used
    // For a given rowIndex, returns the entire row range, starting from B column (assuming A column stores vertical header)
    // example: Input: 4 => Ouput: B4:AZ4
    private static String getFullHorizontalRange(Integer rowIndex, String sheetName) {

        String rowRange = sheetName + "!B" + rowIndex + ":AZ" + rowIndex;

        return rowRange;
    }

    // Helper method. Not currently used
    // For a given colIndex, returns the entire col range, starting from row#2 (assuming row#1 stores header)
    // example: Input: 4 => Ouput: D2:D1000
    private static String getFullVerticalRange (Integer colIndex) {

        // fetch col alphabet corresponding to returned index
        String col = alphabetsList.get(colIndex);

        String verticalRange = col + "2:" + col + "1000";
        play.Logger.info("returning range: " + verticalRange);

        return verticalRange;
    }

    //
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

    // Writes a single row to the sheet. input must be a list of values corressponding to each col within the row
    private static void writeSingleRowToSheet(String range, List<Object> values)
    {
        List<List<Object>> arrData = convertDataToUpdate(values);

        writeRangeToSheet(range, arrData);
    }

    // Writes an entire range (like B4: K10) in the given range
    // input data is expected as a list of rows where each row contains a list of col values
    private static void writeRangeToSheet(String range, List<List<Object>> values)
    {
        try {

            play.Logger.info(" Writing data " + values.toString() + " in " + range);

            ValueRange oRange = new ValueRange();
            oRange.setRange(range);
            oRange.setValues(values);

            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);

            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);

            BatchUpdateValuesResponse oResp1 =
                    service.spreadsheets().values().batchUpdate(spreadSheetId, oRequest).execute();

        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }



    // Reads the horizontal header values within a sheet (A1 to AZ1)
    // and identifies the colIndex corresponding to the given string
    private static Integer getColIndexFromHeader(String headerText, String sheetName) throws IOException {

        String range = sheetName + "!A1:AZ1";

        // read all values in the row-header
        ValueRange response = service.spreadsheets().values()
                .get(spreadSheetId, range)
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
                    + spreadSheetId + " within range " + range);

            return null;
        }

        return indexOfHeaderText;
    }


    // convert a unideminsional list of values say [value1, value2, value3.... value4]
    // to two dimensional list List[row1=[value1, value2, value3, value4]]
    // This method is specifically to format data that can be used to update in a single row within the sheet
    private static List<List<Object>> convertDataToUpdate (List<Object> dataToUpdate) {

        List<List<Object>> dataParent = new ArrayList<List<Object>>();

        dataParent.add(dataToUpdate);

        return dataParent;
    }

    // Helper method. Not currently used
    private static TreeMap<String, Object> createCellAddressToValuesMap(String sheetName,
                                                                        Map<Date, Map<String, Object>> dateToHeaderToValueMap)
            throws IOException
    {
        play.Logger.info("values to update " + dateToHeaderToValueMap.toString());
        TreeMap<String, Object> cellAddrToValues = new TreeMap<String, Object>();
        Iterator<Map.Entry<Date, Map<String, Object>>> parentItr = dateToHeaderToValueMap.entrySet().iterator();

        Date iterationDate;

        // Iterate on date
        while (parentItr.hasNext()) {

            Map.Entry<Date, Map<String, Object>> entry = parentItr.next();
            iterationDate = entry.getKey();

            Map<String, Object> headerToValueMap = entry.getValue();

            // retrieve index corressponding to this date header
            Integer dateRowIndex = getRowIndexFromVerticalHeaderValue(sheetName, sfd.format(iterationDate));

            play.Logger.info(" DateItr " + iterationDate + " rowIndex " + dateRowIndex);

            // TODO, right now we are not creating a new column if the required column is not found

            Iterator<String> headerItr = headerToValueMap.keySet().iterator();

            // Iterate on all header values that we need to write
            while (headerItr.hasNext()) {
                String headerString = headerItr.next();

                // fetch the col label corresponding to this horizontal header
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
