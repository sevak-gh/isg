package com.infotech.com.it;

import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.proxy.jiring.JiringProxy;
import com.infotech.isg.proxy.jiring.TCSResponse;
import com.infotech.isg.it.fake.jiring.JiringFake;
import com.infotech.isg.it.wsclient.ISGClient;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * integration test for Jiring service
 *
 * @author Sevak Gahribian
 */
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class JiringIT extends AbstractTestNGSpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(JiringIT.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionRepository transactionRepo;

    // fake server for jiring
    // defined as spring managed bean so that app properties can be used
    @Autowired
    JiringFake jiringFake;

    // isg web service client
    // defined as spring managed bean so that app properties can be used
    @Autowired
    ISGClient wsclient;

    @BeforeMethod
    public void initDB() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "info_topup_transactions",
                                       "info_topup_operators",
                                       "info_topup_payment_channel",
                                       "info_topup_clients",
                                       "info_topup_client_ips");
        jdbcTemplate.update("insert into info_topup_operators values(1,'MTN','active'), (2,'MCI','active'), (3,'Jiring','active')");
        jdbcTemplate.update("insert into info_topup_payment_channel values(59,'Y'), (14,'Y'), (5,'Y')");
        // add client: username=root, password=123456, active='Y', ips: 127.0.0.1, 172.16.10.15
        jdbcTemplate.update("insert into info_topup_clients(id,client,pin,name,contact,tel,vendor,created,active) values(1, 'root', "
                            + "'ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346"
                            + "ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413'"
                            + ", 'name', 'contact', 'tel', 'vendor', '2014-01-01 13:05:23','Y')");
        jdbcTemplate.update("insert into info_topup_client_ips values(1,'127.0.0.1'), (1, '172.16.10.15')");
    }

    @AfterMethod
    public void tearDown() {
        jiringFake.stop();
    }

    @Test
    public void HappyPathShouldSucceed() {
        // arrange
        String token = "token";
        String jiringResponseCode = "0";
        String jiringResponseTrId = "123234569";
        String jiringResponseMessage = "request done";
        JiringProxy jiringProxy = new JiringProxy() {
            @Override
            public TCSResponse salesRequest(String consumer, int amount) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(token);
                return response;
            }

            @Override
            public TCSResponse salesRequestExec(String param) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(jiringResponseTrId);
                return response;
            }
        };
        jiringFake.setJiringProxyImpl(jiringProxy);
        jiringFake.start();
        String username = "root";
        String password = "123456";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "jirrcpt";
        String orderId = "orderid";
        String consumer = "09125067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.jiring(username, password, bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(jiringResponseTrId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getToken(), is(token));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(jiringResponseCode));
        assertThat(transaction.getOperatorResponse(), is(jiringResponseMessage));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void shouldReturnInvalidUsernamePassword() {
        // arrange
        String token = "token";
        String jiringResponseCode = "0";
        String jiringResponseTrId = "123234569";
        String jiringResponseMessage = "request done";
        JiringProxy jiringProxy = new JiringProxy() {
            @Override
            public TCSResponse salesRequest(String consumer, int amount) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(token);
                return response;
            }

            @Override
            public TCSResponse salesRequestExec(String param) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(jiringResponseTrId);
                return response;
            }
        };
        jiringFake.setJiringProxyImpl(jiringProxy);
        jiringFake.start();
        String username = "root";
        String password = "root";   // invalid password
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "jirrcpt";
        String orderId = "orderid";
        String consumer = "09125067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.jiring(username, password, bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.INVALID_USERNAME_OR_PASSWORD));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(0));
    }

    @Test
    public void shouldReturnInvalidCellNumber() {
        // arrange
        String token = "token";
        String jiringResponseCode = "0";
        String jiringResponseTrId = "123234569";
        String jiringResponseMessage = "request done";
        JiringProxy jiringProxy = new JiringProxy() {
            @Override
            public TCSResponse salesRequest(String consumer, int amount) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(token);
                return response;
            }

            @Override
            public TCSResponse salesRequestExec(String param) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(jiringResponseTrId);
                return response;
            }
        };
        jiringFake.setJiringProxyImpl(jiringProxy);
        jiringFake.start();
        String username = "root";
        String password = "123456";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "jirrcpt";
        String orderId = "orderid";
        String consumer = "077856698";      // invalid cell number
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.jiring(username, password, bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.INVALID_CELL_NUMBER));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(0));
    }

    @Test
    public void shouldReturnInvalidPaymentChannel() {
        // arrange
        String token = "token";
        String jiringResponseCode = "0";
        String jiringResponseTrId = "123234569";
        String jiringResponseMessage = "request done";
        JiringProxy jiringProxy = new JiringProxy() {
            @Override
            public TCSResponse salesRequest(String consumer, int amount) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(token);
                return response;
            }

            @Override
            public TCSResponse salesRequestExec(String param) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(jiringResponseTrId);
                return response;
            }
        };
        jiringFake.setJiringProxyImpl(jiringProxy);
        jiringFake.start();
        String username = "root";
        String password = "123456";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 54;   // invalid channel
        String state = "state";
        String bankReceipt = "jirrcpt";
        String orderId = "orderid";
        String consumer = "09125067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.jiring(username, password, bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.INVALID_PAYMENT_CHANNEL));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(0));
    }

    @Test
    public void shouldReturnNOKWhenOperationNotdSucceed() {
        // arrange
        String token = "token";
        String jiringTokenResponseCode = "7";                // any non-zero value means NOK
        String jiringResponseCode = "0";
        String jiringResponseTrId = "123234569";
        String jiringResponseMessage = "requested operation not possible";
        JiringProxy jiringProxy = new JiringProxy() {
            @Override
            public TCSResponse salesRequest(String consumer, int amount) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringTokenResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(token);
                return response;
            }

            @Override
            public TCSResponse salesRequestExec(String param) {
                TCSResponse response = new TCSResponse();
                response.setResult(jiringResponseCode);
                response.setMessage(jiringResponseMessage);
                response.setParam1(jiringResponseTrId);
                return response;
            }
        };
        jiringFake.setJiringProxyImpl(jiringProxy);
        jiringFake.start();
        String username = "root";
        String password = "123456";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "jirrcpt";
        String orderId = "orderid";
        String consumer = "09125067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.jiring(username, password, bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK));
        assertThat(response.getOPRDoc(), is(jiringTokenResponseCode));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getToken(), is(nullValue()));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(jiringTokenResponseCode));
        assertThat(transaction.getOperatorResponse(), is(jiringResponseMessage));
        assertThat(transaction.getStf(), is(nullValue()));
    }
}
