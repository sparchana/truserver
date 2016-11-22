package dao;

import models.entity.ongrid.OnGridVerificationFields;

import java.util.List;

/**
 * Created by archana on 11/19/16.
 */
public class OnGridVerificationFieldsDAO {

    // Ongrid verification fields static table value
    public static final int ONGRID_VERIFICATION_DOC_AADHAAR = 1;
    public static final int ONGRID_VERIFICATION_DOC_DL = 2;
    public static final int ONGRID_VERIFICATION_FIELD_NAME = 3;
    public static final int ONGRID_VERIFICATION_FIELD_PHONE = 4;
    public static final int ONGRID_VERIFICATION_FIELD_DOB = 5;
    public static final int ONGRID_VERIFICATION_FIELD_AGE = 6;
    public static final int ONGRID_VERIFICATION_FIELD_CITY = 7;
    public static final int ONGRID_VERIFICATION_DOC_PAN = 8;
    public static final int ONGRID_VERIFICATION_FIELD_GENDER = 9;
    public static final int ONGRID_VERIFICATION_FIELD_EMAIL = 10;

    public static OnGridVerificationFields getField(int fieldId) {
        return OnGridVerificationFields.find.where().eq("fieldId", fieldId).findUnique();
    }

    public static List<OnGridVerificationFields> getAllFields() {
        return OnGridVerificationFields.find.all();
    }
}
