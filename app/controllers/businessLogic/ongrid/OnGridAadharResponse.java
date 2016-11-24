package controllers.businessLogic.ongrid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dao.OnGridVerificationFieldsDAO;
import models.entity.ongrid.OnGridVerificationFields;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by archana on 11/17/16.
 */
public class OnGridAadharResponse {

    @JsonIgnore
    private OnGridVerificationFieldsDAO myFieldsDao = new OnGridVerificationFieldsDAO();

    @JsonIgnore
    public Map<OnGridVerificationFields, String> myFieldToStatusMap =
            new HashMap<OnGridVerificationFields, String>();

    public String uid;
    public String name;
    public String nameMatched;
    public String gender;
    public String phone;
    public String vtc;
    public String email;
    public String dob;
    public String age;
    public String co;
    public String house;
    public String street;
    public String loc;
    public String lm;
    public String subdist;
    public String dist;
    public String state;
    public String pc;
    public String po;
    public String av;
    public String requestId;
    public String individualId;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVtc() {
        return vtc;
    }

    public void setVtc(String vtc) {
        this.vtc = vtc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getLm() {
        return lm;
    }

    public void setLm(String lm) {
        this.lm = lm;
    }

    public String getSubdist() {
        return subdist;
    }

    public void setSubdist(String subdist) {
        this.subdist = subdist;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getAv() {
        return av;
    }

    public void setAv(String av) {
        this.av = av;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestID(String requestId) {
        this.requestId = requestId;
    }

    public String getIndividualId() {
        return individualId;
    }

    public void setIndividualId(String individualId) {
        this.individualId = individualId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameMatched() {
        return nameMatched;
    }

    public void setNameMatched(String nameMatched) {
        this.nameMatched = nameMatched;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<OnGridVerificationFields, String> getMyFieldToStatusMap() {

        myFieldToStatusMap.put(
                myFieldsDao.getById(OnGridVerificationFieldsDAO.ONGRID_VERIFICATION_FIELD_NAME),
                        getName()
        );

        myFieldToStatusMap.put(
                myFieldsDao.getById(OnGridVerificationFieldsDAO.ONGRID_VERIFICATION_FIELD_AGE),
                getAge()
        );

        myFieldToStatusMap.put(
                myFieldsDao.getById(OnGridVerificationFieldsDAO.ONGRID_VERIFICATION_FIELD_PHONE),
                getPhone()
        );

        myFieldToStatusMap.put(
                myFieldsDao.getById(OnGridVerificationFieldsDAO.ONGRID_VERIFICATION_FIELD_DOB),
                getDob()
        );

        myFieldToStatusMap.put(
                myFieldsDao.getById(OnGridVerificationFieldsDAO.ONGRID_VERIFICATION_FIELD_AGE),
                getAge()
        );

        myFieldToStatusMap.put(
                myFieldsDao.getById(OnGridVerificationFieldsDAO.ONGRID_VERIFICATION_FIELD_CITY),
                getVtc()
        );

        myFieldToStatusMap.put(
                myFieldsDao.getById(OnGridVerificationFieldsDAO.ONGRID_VERIFICATION_FIELD_GENDER),
                getGender()
        );

        myFieldToStatusMap.put(
                myFieldsDao.getById(OnGridVerificationFieldsDAO.ONGRID_VERIFICATION_FIELD_EMAIL),
                getEmail()
        );

        return myFieldToStatusMap;
    }

    @Override
    public String toString() {

        return getUid() + "|" + getName() + "|" + getNameMatched() + "|" + getGender() + "|" + getVtc()
        + "|" + getPhone() + "|" + getEmail() + "|" + getDob() + "|" + getAge() + "|"
                + getCo() + "|" + getHouse() + "|" + getStreet() + "|" + getLoc() + "|"
                + getLm() + "|" + getSubdist() + "|" + getDist() + "|" + getState() + "|"
                + getPc() + "|" + getPo() + "|" + getAv() + "|" + getRequestId() + "|" + getIndividualId();
    }
}
