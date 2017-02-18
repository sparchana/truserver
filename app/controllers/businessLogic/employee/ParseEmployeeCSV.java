package controllers.businessLogic.employee;

import api.http.httpResponse.TruResponse;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvException;
import models.util.CsvToBean;
import models.util.Message;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by zero on 16/2/17.
 */
public class ParseEmployeeCSV {

    public ParseResponse parseCSV(File file) throws Exception {
        ParseResponse response = new ParseResponse();
        response.setStatus(TruResponse.STATUS_FAILURE);

        if(file == null){
            return response;
        }

            // tika for fileType detection
        Tika tika = new Tika();
        String fileContentType = tika.detect(file);

            // excel has formatting issue, this takes only csv
        if(!fileContentType.toLowerCase().contains("text/plain")){

            // invalid file type --> Only csv formats are supported
            response.setMessages(Arrays.asList(new Message(Message.MESSAGE_ERROR, "Invalid File Type: "+ fileContentType)));

            return response;
        }

            // parsing logic begins
            // reader
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(file), CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return response;
        }

            // setting parsing strategy
        ColumnPositionMappingStrategy<EmployeeCSVBean> strategy = new ColumnPositionMappingStrategy<EmployeeCSVBean>();
        strategy.setType(EmployeeCSVBean.class);
        String[] columns = new String[] {"slno", "name", "mobile", "email", "locality", "employee id"}; // the fields to bind do in JavaBean
        strategy.setColumnMapping(columns);

            /* this list may contain duplicate data, process it further with business logic */
        CsvToBean<EmployeeCSVBean> csv = new CsvToBean<>();

        List<EmployeeCSVBean> employeeCSVBeanList = null;

            // converting csv to java bean
        try {
            employeeCSVBeanList = csv.parse(strategy, reader, false);
        } catch (Exception e) {
            response.setMessages(Arrays.asList(new Message(Message.MESSAGE_ERROR, e.getMessage())));
            return response;
        } // parsing logic end

            // building parse error message list
        for(CsvException exception: csv.getCapturedExceptions()) {
            Message message = new Message(Message.MESSAGE_ERROR, "Error parsing CSV line: " + (exception.getLineNumber()+1)+ " | Error msg: " + exception.getMessage());
            response.getMessages().add(message);
        }

            // building response
        response.setParsedList(employeeCSVBeanList);
        response.setTotalCount(reader.getLinesRead());
        response.setSuccessCount(employeeCSVBeanList.size());
        response.setFailureCount(response.getMessages().size());
        if(employeeCSVBeanList.size()>0)
            response.setStatus(TruResponse.STATUS_SUCCESS);

        return response;
    }

    /**
     * keeps the first encountered data, removes rest of remaining duplicates
     * @param employeeCSVBeanList
     */
    public void removeDuplicates(List<EmployeeCSVBean> employeeCSVBeanList) {
        Map<String, EmployeeCSVBean> map = getEmployeeCSVBeanMap(employeeCSVBeanList);

        employeeCSVBeanList.clear();
        for(Map.Entry<String, EmployeeCSVBean> entry: map.entrySet()){
            employeeCSVBeanList.add(entry.getValue());
        }
    }

    private Map<String, EmployeeCSVBean> getEmployeeCSVBeanMap(List<EmployeeCSVBean> employeeCSVBeanList){
        Map<String, EmployeeCSVBean> employeeCSVBeanMap = new LinkedHashMap<>();

        if(employeeCSVBeanList == null || employeeCSVBeanList.size() == 0) {
            return employeeCSVBeanMap;
        }

        for(EmployeeCSVBean bean : employeeCSVBeanList) {
            employeeCSVBeanMap.putIfAbsent(bean.getMobile(), bean);
        }

        return employeeCSVBeanMap;
    }

    public class ParseResponse extends TruResponse {
        private List<EmployeeCSVBean> parsedList;
        private long totalCount;
        private long successCount;
        private long failureCount;

        ParseResponse() {
            this.parsedList = new ArrayList<>();
        }

        public List<EmployeeCSVBean> getParsedList() {
            return parsedList;
        }

        private void setParsedList(List<EmployeeCSVBean> parsedList) {
            this.parsedList = parsedList;
        }

        public long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }

        public long getSuccessCount() {
            return successCount;
        }

        private void setSuccessCount(long successCount) {
            this.successCount = successCount;
        }

        public long getFailureCount() {
            return failureCount;
        }

        private void setFailureCount(long failureCount) {
            this.failureCount = failureCount;
        }

    }
}
