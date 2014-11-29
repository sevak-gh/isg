package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.service.ErrorCodes;
import com.infotech.isg.service.RequestValidator;
import com.infotech.isg.service.MCIRequestValidator;

import java.util.Map;
import java.util.HashMap;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
* test cases for RequestValidator.
*
* @author Sevak Gharibian
*/
public class RequestValidatorTest {

    private RequestValidator requestValidator;

    @BeforeClass
    public void setUp() {
        requestValidator = new MCIRequestValidator();
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

    @DataProvider(name = "provideOperators")
    public Object[][] provideOperators() {
        return new Object[][] {
            {null, ErrorCodes.INVALID_OPERATOR},
            {new Operator() {{setId(5); setName("test2"); setIsActive(false);}}, ErrorCodes.DISABLED_OPERATOR},
            {new Operator() {{setId(Operator.MTN_ID); setName("MTN"); setIsActive(true);}}, ErrorCodes.OK},
            {new Operator() {{setId(Operator.MCI_ID); setName("MCI"); setIsActive(true);}}, ErrorCodes.OK},
            {new Operator() {{setId(Operator.JIRING_ID); setName("JIRING"); setIsActive(true);}}, ErrorCodes.OK},
            {new Operator() {{setId(4); setName("test1"); setIsActive(true);}}, ErrorCodes.OK}
        };
    }

    @DataProvider(name = "providePaymentChannels")
    public Object[][] providePaymentChannels() {
        return new Object[][] {
            {null, ErrorCodes.INVALID_PAYMENT_CHANNEL},
            {new PaymentChannel() {{setId("59"); setIsActive(true);}}, ErrorCodes.OK},
            {new PaymentChannel() {{setId("14"); setIsActive(true);}}, ErrorCodes.OK},
            {new PaymentChannel() {{setId("5"); setIsActive(true);}}, ErrorCodes.OK},
            {new PaymentChannel() {{setId("25"); setIsActive(false);}}, ErrorCodes.DISABLED_PAYMENT_CHANNEL},
            {new PaymentChannel() {{setId("10"); setIsActive(true);}}, ErrorCodes.OK}
        };
    }

    @DataProvider(name = "provideTransactions")
    public Object[][] provideTransactions() {
        return new Object[][] {
            {null, "", 0, 0, 0, "", "", ErrorCodes.OK},
            {new Transaction() {{}}, "XY78", 0, 0, 0, "", "", ErrorCodes.DOUBLE_SPENDING_TRANSACTION},
            {new Transaction() {{setResNum("ABC12");}}, "XY78", 0, 0, 0, "", "", ErrorCodes.DOUBLE_SPENDING_TRANSACTION},
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.OK
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MTN_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                10000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                2,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09121121245",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "1.2.1.3",          // customerIP
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        //setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        //setStatus(1);
                        //setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.TRANSACTION_ALREADY_FAILED
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        //setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.TRANSACTION_ALREADY_FAILED
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(-1);
                        setOperatorResponseCode(0);
                        setStf(0);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.TRANSACTION_ALREADY_FAILED
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(1);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.OPERATOR_SERVICE_ERROR
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        //setStf(1);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.OK
            },
            {
                new Transaction() {{
                        setResNum("ABC12");
                        setProvider(Operator.MCI_ID);
                        setAmount(20000);
                        setChannel(1);
                        setConsumer("09125067064");
                        setCustomerIp("10.20.1.5");
                        setStatus(1);
                        setOperatorResponseCode(0);
                        setStf(3);
                    }
                },
                "ABC12",            // orderId
                Operator.MCI_ID,    // provider
                20000,              // amount
                1,                  // channel
                "09125067064",      // consumer
                "10.20.1.5",        // customerIP
                ErrorCodes.STF_ERROR
            }

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

    @Test(dataProvider = "provideBankCodes")
    public void shouldValidateBankCodeReturnExpectedErrorCode(String bankCode, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = requestValidator.validateBankCode(bankCode);

        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "provideOperators")
    public void shouldValidateOperatorReturnExpectedErrorCode(Operator operator, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = requestValidator.validateOperator(operator);

        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "providePaymentChannels")
    public void shouldValidatePaymentChannelReturnExpectedErrorCode(PaymentChannel channel, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = requestValidator.validatePaymentChannel(channel);

        // assert
        assertThat(result, is(errorCode));
    }

    @Test(dataProvider = "provideTransactions")
    public void shouldValidateTransactionReturnExpectedErrorCode(Transaction transaction, String orderId,
                                        int operatorId, int amount, int channel,
                                        String consumer, String customerIp, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = requestValidator.validateTransaction(transaction, orderId, operatorId, amount,
                                                            channel, consumer, customerIp);

        // assert
        assertThat(result, is(errorCode));
    }
}
