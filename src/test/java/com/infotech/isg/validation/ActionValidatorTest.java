package com.infotech.isg.validation;

import com.infotech.isg.validation.ActionValidator;;
import com.infotech.isg.validation.impl.MCIActionValidatorImpl;
import com.infotech.isg.validation.impl.MTNActionValidatorImpl;
import com.infotech.isg.validation.impl.JiringActionValidatorImpl;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
* test cases for action validator
*
* @author Sevak Gharibian
*/
public class ActionValidatorTest {

    @DataProvider(name = "provideMTNActions")
    public Object[][] provideMTNActions() {
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

    @DataProvider(name = "provideMCIActions")
    public Object[][] provideMCIActions() {
        return new Object[][] {
            {"", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"1", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"charge", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"TOPUP", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"topup", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"top_up", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"top-up", ErrorCodes.OK},
            {"BULK", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"bulk", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"GPRS", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"gprs", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"wow", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"post_wimax", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"pre_wimax", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"pay-bill", ErrorCodes.INVALID_OPERATOR_ACTION}
        };
    }

    @DataProvider(name = "provideJiringActions")
    public Object[][] provideJiringActions() {
        return new Object[][] {
            {"", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"1", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"charge", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"TOPUP", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"topup", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"top_up", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"top-up", ErrorCodes.OK},
            {"BULK", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"bulk", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"GPRS", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"gprs", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"wow", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"post_wimax", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"pre_wimax", ErrorCodes.INVALID_OPERATOR_ACTION},
            {"pay-bill", ErrorCodes.INVALID_OPERATOR_ACTION}
        };
    }


    @Test(dataProvider = "provideMTNActions")
    public void mtnActionValidatorShouldReturnExpectedErrorCode(String action, int errorCode) {
        // arrange
        ActionValidator actionValidator = new MTNActionValidatorImpl();
        // different cases provided by data provider

        // act
        int result = actionValidator.validate(action);

        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "provideMCIActions")
    public void mciActionValidatorShouldReturnExpectedErrorCode(String action, int errorCode) {
        // arrange
        ActionValidator actionValidator = new MCIActionValidatorImpl();
        // different cases provided by data provider

        // act
        int result = actionValidator.validate(action);

        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "provideJiringActions")
    public void jiringActionValidatorShouldReturnExpectedErrorCode(String action, int errorCode) {
        // arrange
        ActionValidator actionValidator = new JiringActionValidatorImpl();
        // different cases provided by data provider

        // act
        int result = actionValidator.validate(action);

        // assert
        assertThat(result, is(errorCode));
    }
}
