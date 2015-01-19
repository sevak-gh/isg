package com.infotech.isg.validation;

import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.validation.BankCodeValidator;
import com.infotech.isg.validation.impl.BankCodeValidatorImpl;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * test cases for bankCode validator
 *
 * @author Sevak Gharibian
 */
public class BankCodeValidatorTest {

    @DataProvider(name = "provideBankCodes")
    public Object[][] provideBankCodes() {
        return new Object[][] {
            {BankCodes.SAMAN, ErrorCodes.OK},
            {"ABC", ErrorCodes.INVALID_BANK_CODE},
            {"111", ErrorCodes.INVALID_BANK_CODE},
            {"0", ErrorCodes.INVALID_BANK_CODE},
            {"", ErrorCodes.INVALID_BANK_CODE},
            {"-1", ErrorCodes.INVALID_BANK_CODE},
            {BankCodes.PARSIAN, ErrorCodes.OK},
            {"018", ErrorCodes.OK},
            {"057", ErrorCodes.OK}
        };
    }

    @Test(dataProvider = "provideBankCodes")
    public void bankCodeValidatorShouldReturnExpectedErrorCode(String bankCode, int errorCode) {
        // arrange
        BankCodeValidator bankCodeValidator = new BankCodeValidatorImpl();
        // different cases provided by data provider

        // act
        int result = bankCodeValidator.validate(bankCode);

        // assert
        assertThat(result, is(errorCode));
    }
}
