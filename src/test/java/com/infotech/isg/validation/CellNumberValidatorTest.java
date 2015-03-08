package com.infotech.isg.validation;

import com.infotech.isg.validation.CellNumberValidator;;
import com.infotech.isg.validation.impl.MCICellNumberValidatorImpl;
import com.infotech.isg.validation.impl.MTNCellNumberValidatorImpl;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * test cases for cell number validator
 *
 * @author Sevak Gharibian
 */
public class CellNumberValidatorTest {

    @DataProvider(name = "provideMCICellNumbers")
    public Object[][] provideMCICellNumbers() {
        return new Object[][] {
            {"", ErrorCodes.INVALID_CELL_NUMBER},
            {"123ABC", ErrorCodes.INVALID_CELL_NUMBER},
            {"02177265698", ErrorCodes.INVALID_CELL_NUMBER},
            {"9125067064", ErrorCodes.OK},
            {"09125067064", ErrorCodes.OK},
            {"989125067064", ErrorCodes.OK},
            {"+989125067064", ErrorCodes.OK},
            {"00989125067064", ErrorCodes.OK},
            {"+9125067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"0989125067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"09215067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"009125067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"+00989125067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"009809125067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"+9809125067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"9809125067064", ErrorCodes.INVALID_CELL_NUMBER}
        };
    }

    @DataProvider(name = "provideMTNCellNumbers")
    public Object[][] provideMTNCellNumbers() {
        return new Object[][] {
            {"", ErrorCodes.INVALID_CELL_NUMBER},
            {"123ABC", ErrorCodes.INVALID_CELL_NUMBER},
            {"02177265698", ErrorCodes.INVALID_CELL_NUMBER},
            {"9325067064", ErrorCodes.OK},
            {"09325067064", ErrorCodes.OK},
            {"989325067064", ErrorCodes.OK},
            {"+989325067064", ErrorCodes.OK},
            {"00989325067064", ErrorCodes.OK},
            {"+9325067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"0989325067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"09215067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"009325067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"+00989325067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"009809325067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"+9809325067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"9809325067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"9425067064", ErrorCodes.OK},
            {"09425067064", ErrorCodes.OK},
            {"989425067064", ErrorCodes.OK},
            {"+989425067064", ErrorCodes.OK},
            {"00989425067064", ErrorCodes.OK},
            {"+9425067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"0989425067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"09215067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"009425067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"+00989425067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"009809425067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"+9809425067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"9809425067064", ErrorCodes.INVALID_CELL_NUMBER},
            {"9015575752", ErrorCodes.OK},
            {"9025575752", ErrorCodes.OK}
        };
    }

    @Test(dataProvider = "provideMCICellNumbers")
    public void mciCellNumberValidatorShouldReturnExpectedErrorCode(String cellNumber, int errorCode) {
        // arrange
        CellNumberValidator cellNumberValidator = new MCICellNumberValidatorImpl();
        // different cases provided by data provider

        // act
        int result = cellNumberValidator.validate(cellNumber);

        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "provideMTNCellNumbers")
    public void mtnCellNumberValidatorShouldReturnExpectedErrorCode(String cellNumber, int errorCode) {
        // arrange
        CellNumberValidator cellNumberValidator = new MTNCellNumberValidatorImpl();
        // different cases provided by data provider

        // act
        int result = cellNumberValidator.validate(cellNumber);

        // assert
        assertThat(result, is(errorCode));
    }
}
