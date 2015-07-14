package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.CellNumberValidator;
import com.infotech.isg.validation.ErrorCodes;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.stereotype.Component;

/**
 * Rightel validator for cell number
 *
 * @author Sevak Gharibian
 */
@Component("RightelCellNumberValidator")
public class RightelCellNumberValidatorImpl implements CellNumberValidator {

    @Override
    public int validate(String cellNumber) {
        Pattern pattern = Pattern.compile("^(0|98|\\+98|0098)?92[0-9]{8}$");
        if (!pattern.matcher(cellNumber).matches()) {
            return ErrorCodes.INVALID_CELL_NUMBER;
        }
        return ErrorCodes.OK;
    }
}
