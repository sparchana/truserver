package controllers.businessLogic.employee;

import com.drew.lang.StringUtil;
import models.util.Validator;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.validator.routines.EmailValidator;
import play.Logger;

import javax.xml.bind.ValidationException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zero on 16/2/17.
 *
 * JavaBean for employee csvToBean Conversion
 */

public class EmployeeCSVBean {
    private long slno;

    private String fullName;

    private String mobile;

    private String email;

    private String locality;

    private String employeeId;

    // setting it empty for csv parser to not throw null error in messageList
    private String firstName="";
    private String lastName="";

    public long getSlno() {
        return slno;
    }

    public void setSlno(long slno) {
        this.slno = slno;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String name) throws ValidationException
    {
        if(!Validator.isNameValid(name)) {
            throw new ValidationException("Invalid Name");
        }
        this.firstName = name = WordUtils.capitalize(name.trim());

        // c.f http://stackoverflow.com/questions/2965747/why-i-get-unsupportedoperationexception-when-trying-to-remove-from-the-list
        List<String> nameList = new LinkedList<String>(Arrays.asList(name.split("\\s+")));

        // logic to divide full name to first name and last name
        if(nameList.size() > 1){
            this.lastName = nameList.get(nameList.size() - 1);
            nameList.remove(nameList.size() - 1);
            this.firstName = StringUtil.join(nameList, " ");
            this.firstName.trim();
        }
        this.fullName = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) throws ValidationException {
        if(!Validator.isPhoneNumberValid(mobile)){
            throw new ValidationException("Invalid Mobile Number");
        }
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidationException {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if(!emailValidator.isValid(email)){
            throw new ValidationException("Invalid Email Id");
        }
        this.email = email;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

