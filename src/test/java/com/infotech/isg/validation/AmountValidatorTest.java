package com.infotech.isg.validation;

import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.validation.AmountValidator;;
import com.infotech.isg.validation.impl.MCIAmountValidatorImpl;
import com.infotech.isg.validation.impl.MTNAmountValidatorImpl;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * test cases for amount validator
 *
 * @author Sevak Gharibian
 */
public class AmountValidatorTest {

    @DataProvider(name = "provideMCIAmounts")
    public Object[][] provideMCIAmounts() {
        return new Object[][] {
            { -1, ErrorCodes.INVALID_AMOUNT},
            {0, ErrorCodes.INVALID_AMOUNT},
            {25, ErrorCodes.INVALID_AMOUNT},
            {3000, ErrorCodes.INVALID_AMOUNT},
            {2000, ErrorCodes.INVALID_AMOUNT},
            {1000, ErrorCodes.INVALID_AMOUNT},
            {10000, ErrorCodes.OK},
            {20000, ErrorCodes.OK},
            {50000, ErrorCodes.OK},
            {100000, ErrorCodes.OK},
            {200000, ErrorCodes.OK},
            {500000, ErrorCodes.OK},
            {1000000, ErrorCodes.OK},
            {300000, ErrorCodes.INVALID_AMOUNT},
            {600000, ErrorCodes.INVALID_AMOUNT},
            {800000, ErrorCodes.INVALID_AMOUNT},
            {12000000, ErrorCodes.INVALID_AMOUNT}
        };
    }

    @DataProvider(name = "provideMTNAmounts")
    public Object[][] provideMTNAmounts() {
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

    @Test(dataProvider = "provideMCIAmounts")
    public void mciAmountValidatorShouldReturnExpectedErrorCode(int amount, int errorCode) {
        // arrange
        AmountValidator amountValidator = new MCIAmountValidatorImpl();
        // different cases provided by data provider

        // act
        int result = amountValidator.validate(amount, ServiceActions.TOP_UP);

        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "provideMTNAmounts")
    public void mtnAmountValidatorShouldReturnExpectedErrorCode(int amount, int errorCode) {
        // arrange
        AmountValidator amountValidator = new MTNAmountValidatorImpl();
        // different cases provided by data provider

        // act
        int result = amountValidator.validate(amount, ServiceActions.TOP_UP);

        // assert
        assertThat(result, is(errorCode));
    }
}

