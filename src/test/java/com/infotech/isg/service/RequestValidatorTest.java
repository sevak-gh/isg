package com.infotech.isg.service;

import com.infotech.isg.domain.*;
import com.infotech.isg.service.*;
import com.infotech.isg.repository.*;

import java.util.Map;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

/**
* test cases for RequestValidator.
*
* @author Sevak Gharibian
*/
public class RequestValidatorTest {

    private RequestValidator requestValidator;
    private OperatorRepository operatorRepository;
    private PaymentChannelRepository paymentChannelRepository;
    private ClientRepository clientRepository;
    private TransactionRepository transactionRepository;

    @BeforeClass
    public void setUp() {
        // mock repository for operator
        operatorRepository = new OperatorRepository() {
            private Map<Integer, Operator> operators = new HashMap<Integer, Operator>() {
                {
                    put(Operator.MTN_ID, new Operator() {{setId(Operator.MTN_ID); setName("MTN"); setIsActive(true);}});
                    put(Operator.MCI_ID, new Operator() {{setId(Operator.MCI_ID); setName("MCI"); setIsActive(true);}});
                    put(Operator.JIRING_ID, new Operator() {{setId(Operator.JIRING_ID); setName("JIRING"); setIsActive(true);}});
                    put(4, new Operator() {{setId(4); setName("test1"); setIsActive(true);}});
                    put(5, new Operator() {{setId(5); setName("test2"); setIsActive(false);}});
                }
            };

            public Operator findById(int id) {
                return operators.get(id);
            }
        };

        requestValidator = new RequestValidatorMCI(operatorRepository, paymentChannelRepository,
                clientRepository, transactionRepository);
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
            {20000, ErrorCodes.OK},
            {50000, ErrorCodes.OK},
            {100000, ErrorCodes.OK},
            {200000, ErrorCodes.OK},
            {300000, ErrorCodes.INVALID_AMOUNT},
            {500000, ErrorCodes.INVALID_AMOUNT}
        };
    }

    @DataProvider(name = "provideCellNumbers")
    public Object[][] provideCellNumbers() {
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

    @DataProvider(name = "provideOperatorIds")
    public Object[][] provideOperatorIds() {
        return new Object[][] {
            {0, ErrorCodes.INVALID_OPERATOR},
            { -1, ErrorCodes.INVALID_OPERATOR},
            {10, ErrorCodes.INVALID_OPERATOR},
            {6, ErrorCodes.INVALID_OPERATOR},
            {5, ErrorCodes.DISABLED_OPERATOR},
            {1, ErrorCodes.OK},
            {2, ErrorCodes.OK},
            {3, ErrorCodes.OK},
            {4, ErrorCodes.OK}
        };
    }

    @Test(dataProvider = "provideAmounts")
    public void testValidateAmount(int amount, int errorCode) {
        Assert.assertEquals(requestValidator.validateAmount(amount), errorCode);
    }

    @Test(dataProvider = "provideCellNumbers")
    public void testValidateCellNumber(String cellNumber, int errorCode) {
        Assert.assertEquals(requestValidator.validateCellNumber(cellNumber), errorCode);
    }

    @Test(dataProvider = "provideBankCodes")
    public void testValidateBankCode(String bankCode, int errorCode) {
        Assert.assertEquals(requestValidator.validateBankCode(bankCode), errorCode);
    }

    @Test(dataProvider = "provideOperatorIds")
    public void testValidateOperator(int operatorId, int errorCode) {
        Assert.assertEquals(requestValidator.validateOperator(operatorId), errorCode);
    }
}
