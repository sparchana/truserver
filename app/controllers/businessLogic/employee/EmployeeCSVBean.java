package controllers.businessLogic.employee;

import models.util.Validator;
import org.apache.commons.validator.routines.EmailValidator;

import javax.xml.bind.ValidationException;

/**
 * Created by zero on 16/2/17.
 *
 * JavaBean for employee csvToBean Conversion
 */

public class EmployeeCSVBean {
    private long slno;

    private String name;

    private String mobile;

    private String email;

    private String locality;

    public long getSlno() {
        return slno;
    }

    public void setSlno(long slno) {
        this.slno = slno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}

