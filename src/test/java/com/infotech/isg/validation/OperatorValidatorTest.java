package com.infotech.isg.validation;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.validation.OperatorValidator;
import com.infotech.isg.validation.impl.OperatorValidatorImpl;
import com.infotech.isg.repository.OperatorRepository;

import java.util.Map;
import java.util.HashMap;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * test cases for operator validator
 *
 * @author Sevak Gharibian
 */
public class OperatorValidatorTest {


    private OperatorRepository operatorRepository;
    private OperatorValidator operatorValidator;

    @BeforeClass
    public void setUp() {
        operatorRepository = new OperatorRepository() {
            private Map<Integer, Operator> operators = new HashMap<Integer, Operator>() {
                {
                    put(Operator.MCI_ID, new Operator() {{setId(Operator.MTN_ID); setName("MTN"); setIsActive(true);}});
                    put(Operator.MTN_ID, new Operator() {{setId(Operator.MCI_ID); setName("MCI"); setIsActive(true);}});
                    put(Operator.JIRING_ID, new Operator() {{setId(Operator.JIRING_ID); setName("JIRING"); setIsActive(false);}});
                }
            };

            @Override
            public Operator findById(int operatorId) {
                return operators.get(operatorId);
            }
        };
        operatorValidator = new OperatorValidatorImpl(operatorRepository);
    }

    @DataProvider(name = "provideOperators")
    public Object[][] provideOperators() {
        return new Object[][] {
            { -1, ErrorCodes.INVALID_OPERATOR},
            {0, ErrorCodes.INVALID_OPERATOR},
            {Operator.JIRING_ID, ErrorCodes.DISABLED_OPERATOR},
            {Operator.MTN_ID, ErrorCodes.OK},
            {Operator.MCI_ID, ErrorCodes.OK}
        };
    }

    @Test(dataProvider = "provideOperators")
    public void operatorValidatorShouldReturnExpectedErrorCode(int operatorId, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = operatorValidator.validate(operatorId);

        // assert
        assertThat(result, is(errorCode));
    }
}
