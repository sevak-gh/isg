package com.infotech.isg.validation;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.validation.TransactionValidator;
import com.infotech.isg.validation.impl.TransactionValidatorImpl;
import com.infotech.isg.repository.TransactionRepository;

import java.util.List;
import java.util.ArrayList;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
* test cases for transaction validator
*
* @author Sevak Gharibian
*/
public class TransactionValidatorTest {


    private TransactionRepository transactionRepository;
    private TransactionValidator transactionValidator;

    @BeforeClass
    public void setUp() {
        transactionRepository = new TransactionRepository() {
            private List<Transaction> transactions = new ArrayList<Transaction>() {
                {
                    add(new Transaction() {{
                            setRefNum("111");
                            setBankCode("054");
                            setClientId(1);
                            setResNum("ABC12");
                            setProvider(Operator.MCI_ID);
                            setAmount(20000);
                            setChannel(1);
                            setConsumer("09125067064");
                            setCustomerIp("10.20.1.5");
                            setStatus(1);
                            setOperatorResponseCode(0);
                        }
                    });
                    add(new Transaction() {{
                            setRefNum("222");
                            setBankCode("054");
                            setClientId(1);
                            setResNum("ABC12");
                            setProvider(Operator.MCI_ID);
                            setAmount(20000);
                            setChannel(1);
                            setConsumer("09125067064");
                            setCustomerIp("10.20.1.5");
                            setStatus(-1);
                            setOperatorResponseCode(-1011);
                            // STF is NULL
                        }
                    });
                    add(new Transaction() {{
                            setRefNum("333");
                            setBankCode("054");
                            setClientId(1);
                            setResNum("ABC12");
                            setProvider(Operator.MCI_ID);
                            setAmount(20000);
                            setChannel(1);
                            setConsumer("09125067064");
                            setCustomerIp("10.20.1.5");
                            setStatus(-1);
                            setOperatorResponseCode(2);
                            setStf(1);
                            setStfResult(0);
                        }
                    });
                    add(new Transaction() {{
                            setRefNum("444");
                            setBankCode("054");
                            setClientId(1);
                            setResNum("ABC12");
                            setProvider(Operator.MCI_ID);
                            setAmount(20000);
                            setChannel(1);
                            setConsumer("09125067064");
                            setCustomerIp("10.20.1.5");
                            setStatus(-1);
                            setOperatorResponseCode(2);
                            setStf(2);
                            setStfResult(0);
                        }
                    });
                    add(new Transaction() {{
                            setRefNum("555");
                            setBankCode("054");
                            setClientId(1);
                            setResNum("ABC12");
                            setProvider(Operator.MCI_ID);
                            setAmount(20000);
                            setChannel(1);
                            setConsumer("09125067064");
                            setCustomerIp("10.20.1.5");
                            setStatus(-1);
                            setOperatorResponseCode(2);
                            setStf(3);
                            setStfResult(0);
                        }
                    });
                    add(new Transaction() {{
                            setRefNum("666");
                            setBankCode("054");
                            setClientId(1);
                            setResNum("ABC12");
                            setProvider(Operator.MCI_ID);
                            setAmount(20000);
                            setChannel(1);
                            setConsumer("09125067064");
                            setCustomerIp("10.20.1.5");
                            setStatus(-1);
                            setOperatorResponseCode(2);
                            setStf(5);
                            setStfResult(0);
                        }
                    });
                }
            };

            @Override
            public List<Transaction> findByRefNumBankCodeClientId(String refNum, String bankCode, int clientId) {
                List<Transaction> result = new ArrayList<Transaction>();
                for (Transaction transaction : transactions) {
                    if (transaction.getRefNum().equals(refNum)
                        && transaction.getBankCode().equals(bankCode)
                    && (transaction.getClientId() == clientId)) {
                        result.add(transaction);
                    }
                }
                return result;
            }

            @Override
            public void create(Transaction transaction) {}

            @Override
            public void update(Transaction transaction) {}

        };
        transactionValidator = new TransactionValidatorImpl(transactionRepository);
    }

    @DataProvider(name = "provideTransactions")
    public Object[][] provideTransactions() {
        return new Object[][] {
            {
                "000",              // refnum/bankReceipt
                "",                 // backCode
                1,                  // client id
                "",                 // resnum/order id
                Operator.MCI_ID,    // operator id
                0,                  // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.OK
            },
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                null,               // resnum/order id
                Operator.MCI_ID,    // operator id
                0,                  // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "XY78",             // resnum/order id
                Operator.MCI_ID,    // operator id
                0,                  // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MTN_ID,    // operator id, different
                20000,              // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                10000,              // amount, different
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                2,                  // channel id, different
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "09121121245",      // cell number (consumer), different
                "10.20.1.5",        // customer ip
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            } ,
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "1.2.1.3",          // customer ip, different
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "",                 // cell number (consumer), different
                "10.20.1.5",        // customer ip
                ErrorCodes.DOUBLE_SPENDING_TRANSACTION
            },
            {
                "111",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.REPETITIVE_TRANSACTION
            },
            {
                "222",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.REPETITIVE_TRANSACTION
            },
            {
                "333",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE
            },
            {
                "444",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.STF_RESOLVED_SUCCESSFUL
            },
            {
                "555",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.STF_RESOLVED_FAILED
            },
            {
                "666",              // refnum/bankReceipt
                "054",              // backCode
                1,                  // client id
                "ABC12",            // resnum/order id
                Operator.MCI_ID,    // operator id
                20000,              // amount
                1,                  // channel id
                "09125067064",      // cell number (consumer)
                "10.20.1.5",        // customer ip
                ErrorCodes.STF_ERROR
            }
        };
    }

    @Test(dataProvider = "provideTransactions")
    public void transactionValidatorShouldReturnExpectedErrorCode(String bankReceipt, String bankCode, int clientId,
            String orderId, int operatorId, int amount,
            int channelId, String consumer, String customerIp,
            int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = transactionValidator.validate(bankReceipt, bankCode, clientId,
                     orderId, operatorId, amount,
                     channelId, consumer, customerIp);

        // assert
        assertThat(result, is(errorCode));
    }
}
