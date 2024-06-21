package org.drishti.esign.validator;

import org.springframework.web.multipart.MultipartFile;

public class FieldValidator {

    public boolean validateFields( String consent, String authType, MultipartFile[] fileData) {

        boolean validateFlag = true;

        if (consent != null) {
            if (!consent.equals("Y")) {
                validateFlag = false;
            }
        } else {
            validateFlag = false;
        }
        if (authType != null) {
            if (!(authType.equals("1") || authType.equals("2"))) {
                validateFlag = false;
            }
        } else {
            validateFlag = false;
        }
        if (fileData.length == 0) {
            validateFlag = false;
        }
        return validateFlag;
    }

}
