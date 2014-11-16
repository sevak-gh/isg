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

        // mock repository for payment channel
        paymentChannelRepository = new PaymentChannelRepository() {
            private Map<String, PaymentChannel> channels = new HashMap<String, PaymentChannel>() {
                {
                    put("59", new PaymentChannel() {{setId("59"); setIsActive(true);}});
                    put("14", new PaymentChannel() {{setId("14"); setIsActive(true);}});
                    put("5", new PaymentChannel() {{setId("5"); setIsActive(true);}});
                    put("10", new PaymentChannel() {{setId("10"); setIsActive(true);}});
                    put("25", new PaymentChannel() {{setId("25"); setIsActive(false);}});
                }
            };

            public PaymentChannel findById(String id) {
                return channels.get(id);
            }
        };

        // mock repository for client
        clientRepository = new ClientRepository() {
            private Map<String, Client> clients = new HashMap<String, Client>() {
                {
                    put("root", new Client() {{
                            setUsername("root");
                            // pass = SHA512("123456")
                            setPassword("ba3253876aed6bc22d4a6ff53d8406c6ad864195"
                                        + "ed144ab5c87621b6c233b548baeae6956df346"
                                        + "ec8c17f5ea10f35ee3cbc514797ed7ddd31454"
                                        + "64e2a0bab413");
                            addIp("1.1.1.1");
                            addIp("2.2.2.2");
                            setIsActive(true);
                        }
                    });
                    put("tejarat", new Client() {{
                            setUsername("tejarat");
                            // pass = SHA512("tejarat")
                            setPassword("6020527f4cbbd21282676133b9c66afdd0e1a55"
                                        + "8c15c61de1a58e8276d891e19a75a3b272e34"
                                        + "91a6e1b14c9b4e1e9aceb6e0ec65f4c91ba1a"
                                        + "223e31889423de5");
                            addIp("10.20.120.10");
                            setIsActive(false);
                        }
                    });
                    put("vanak", new Client() {{
                            setUsername("vanak");
                            // pass = SHA512("van123")
                            setPassword("1e4b158a909ba4627a98b83b14db16fbcf319be"
                                        + "10cf7fa287119fd269757d57a72c506284ed0"
                                        + "2c7c92738751778f472ec48200da5c82e1b56"
                                        + "8b52b774339142e");
                            setIsActive(true);
                        }
                    });
                }
            };

            public Client findByUsername(String username) {
                return clients.get(username);
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

    @DataProvider(name = "providePaymentChannelIds")
    public Object[][] providePaymentChannelIds() {
        return new Object[][] {
            { -1, ErrorCodes.INVALID_PAYMENT_CHANNEL},
            {0, ErrorCodes.INVALID_PAYMENT_CHANNEL},
            {125, ErrorCodes.INVALID_PAYMENT_CHANNEL},
            {59, ErrorCodes.OK},
            {14, ErrorCodes.OK},
            {5, ErrorCodes.OK},
            {25, ErrorCodes.DISABLED_PAYMENT_CHANNEL},
            {10, ErrorCodes.OK}
        };
    }

    @DataProvider(name = "provideClients")
    public Object[][] provideClients() {
        return new Object[][] {
            {"", "", "", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"admin", "admin123", "2.5.6.7", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"root", "", "", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"Root", "123456", "1.1.1.1", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"root", "123456", "1.1.1.1", ErrorCodes.OK},
            {"tejarat", "111111", "1.1.1.1", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"tejarat", "tejarat", "10.20.120.10", ErrorCodes.DISABLED_CLIENT_ACCOUNT},
            {"tejarat", "tejarat", "1.1.1.1", ErrorCodes.DISABLED_CLIENT_ACCOUNT},
            {"vanak", "van123", "1.1.1.1", ErrorCodes.INVALID_CLIENT_IP},
            {"vanak", "van123", "10.20.120.10", ErrorCodes.INVALID_CLIENT_IP},
            {"root", "123456", "10.20.120.50", ErrorCodes.INVALID_CLIENT_IP},
            {"root", "123456", "172.16.10.15", ErrorCodes.INVALID_CLIENT_IP},
            {"root", "123456", "2.2.2.2", ErrorCodes.OK}
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

    @Test(dataProvider = "providePaymentChannelIds")
    public void testValidatePaymentChannel(int channelId, int errorCode) {
        Assert.assertEquals(requestValidator.validatePaymentChannel(channelId), errorCode);
    }

    @Test(dataProvider = "provideClients")
    public void testValidateClient(String username, String password, String ip, int errorCode) {
        Assert.assertEquals(requestValidator.validateClient(username, password, ip), errorCode);
    }

}
