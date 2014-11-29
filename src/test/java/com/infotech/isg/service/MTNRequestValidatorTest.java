package com.infotech.isg.service;

import com.infotech.isg.service.ErrorCodes;
import com.infotech.isg.service.RequestValidator;
import com.infotech.isg.service.MTNRequestValidator;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
* test cases for RequestValidatorMTN.
*
* @author Sevak Gharibian
*/
public class MTNRequestValidatorTest {

    private RequestValidator requestValidator;

    @BeforeClass
    public void setUp() {
        requestValidator = new MTNRequestValidator();
    }

    @DataProvider(name = "provideAmounts")
    public Object[][] provideAmounts() {
        return new Object[][] {
            { -1, ErrorCodes.INVALID_AMOUNT},
            {0, ErrorCodes.INVALID_AMOUNT},
            {25, ErrorCodes.INVALID_AMOUNT},
            {3000, ErrorCodes.INVALID_AMOUNT},
            {2000, ErrorCodes.INVALID_AMOUNT},
            {1000, ErrorCodes.INVALID_AMOUNT},
            {10000, ErrorCodes.OK},
            {11500, ErrorCodes.OK},
            {50000, ErrorCodes.OK},
            {1258000, ErrorCodes.OK},
            {200000, ErrorCodes.OK},
            {300000, ErrorCodes.OK},
            {500000, ErrorCodes.OK}
        };
    }

    @DataProvider(name = "provideCellNumbers")
    public Object[][] provideCellNumbers() {
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
            {"9809425067064", ErrorCodes.INVALID_CELL_NUMBER}
        };
    }

    @DataProvider(name = "provideActions")
    public Object[][] provideActions() {
        return new Object[][] {
            {"", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"1", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"charge", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"TOPUP", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"topup", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"top_up", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"top-up", ErrorCodes.OK},
            {"BULK", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"bulk", ErrorCodes.OK},
            {"GPRS", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"gprs", ErrorCodes.OK},
            {"wow", ErrorCodes.OK},
            {"post_wimax", ErrorCodes.OK},
            {"pre_wimax", ErrorCodes.OK},
            {"pay-bill", ErrorCodes.OK}
        };
    }

    @Test(dataProvider = "provideAmounts")
    public void shouldValidateAmountReturnExpectedErrorCode(int amount, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = requestValidator.validateAmount(amount);
        
        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "provideCellNumbers")
    public void shouldValidateCellNumberReturnExpectedErrorCode(String cellNumber, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = requestValidator.validateCellNumber(cellNumber);
        
        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "provideActions")
    public void shouldValidateActionReturnExpectedErrorCode(String action, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = requestValidator.validateAction(action);
        
        // assert
        assertThat(result, is(errorCode));
    }
}
