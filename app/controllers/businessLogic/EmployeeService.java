package controllers.businessLogic;

import api.http.httpResponse.TruResponse;
import controllers.businessLogic.employee.EmployeeCSVBean;
import controllers.businessLogic.employee.ParseEmployeeCSV;

import java.io.File;
import java.util.List;

/**
 * Created by zero on 16/2/17.
 */
public class EmployeeService {

    public ParseEmployeeCSV.ParseResponse parseEmployeeCsv(File file) throws Exception {

        ParseEmployeeCSV parser = new ParseEmployeeCSV();
        ParseEmployeeCSV.ParseResponse response = parser.parseCSV(file);

        if(response.getStatus() == TruResponse.STATUS_SUCCESS) {

                // remove duplicate info
            parser.removeDuplicates(response.getParsedList());

                // create partner with access level 2
            createEmployee(response.getParsedList());
        }

        return response;
    }

    private void createEmployee(List<EmployeeCSVBean> parsedList) {

    }

}
