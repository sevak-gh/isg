package com.infotech.com.it;

import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.proxy.mtn.MTNProxy;
import com.infotech.isg.proxy.mtn.MTNProxyResponse;
import com.infotech.isg.it.fake.mtn.MTNWSFake;
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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * integration test for MTN service
 *
 * @author Sevak Gahribian
 */
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class MTNIT extends AbstractTestNGSpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(MTNIT.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionRepository transactionRepo;

    // fake mci web service
    // defined as spring managed bean so that app properties can be used
    @Autowired
    MTNWSFake mtnws;

    // isg web service client
    // defined as spring managed bean so that app properties can be used
    @Autowired
    ISGClient wsclient;

    @BeforeMethod
    public void initDB() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "info_topup_transactions",
                                       "info_topup_operator_last_status",
                                       "info_topup_operators",
                                       "info_topup_payment_channel",
                                       "info_topup_clients",
                                       "info_topup_client_ips");
        jdbcTemplate.update("insert into info_topup_operators values(1,'MTN','active'), (2,'MCI','active'), (3,'Jiring','active')");
        jdbcTemplate.update("insert into info_topup_payment_channel values(59,'Y'), (14,'Y'), (5,'Y')");
        // add client: username=root, password=123456, active='Y', ips: 127.0.0.1, 172.16.14.15
        jdbcTemplate.update("insert into info_topup_clients(id,client,pin,name,contact,tel,vendor,created,active) values(1, 'root', "
                            + "'ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346"
                            + "ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413'"
                            + ", 'name', 'contact', 'tel', 'vendor', '2014-01-01 13:05:23','Y')");
        jdbcTemplate.update("insert into info_topup_client_ips values(1,'127.0.0.1'), (1, '172.16.14.15')");
    }

    @AfterMethod
    public void tearDown() {
        mtnws.stop();
    }

    @Test
    public void HappyPathShouldSucceedForTOPUP() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(mtnTransactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void HappyPathShouldSucceedForWOW() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "wow";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(mtnTransactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void HappyPathShouldSucceedForPayBill() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "pay-bill";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(mtnTransactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void HappyPathShouldSucceedForBulk() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "bulk";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(mtnTransactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void HappyPathShouldSucceedForPreWimax() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "pre-wimax";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(mtnTransactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void HappyPathShouldSucceedForPostWimax() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "post-wimax";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(mtnTransactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void HappyPathShouldSucceedForGPRS() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "gprs";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(mtnTransactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void shouldReturnInvalidUsernamePassword() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "root";   // invalid password
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
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
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "077856698";      // invalid cell number
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
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
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 54;   // invalid channel
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
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
    public void shouldReturnNOKWhenOperationNotSucceed() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge not done";
        String mtnCommandStatus = "NOK";
        String mtnResultCode = "2";                 // any non-zero positive number means NOK
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK));
        assertThat(response.getOPRDoc(), is(mtnResultCode));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getToken(), is(nullValue()));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void shouldReturnNotReverseAndSetSTFWhenEndpointNotAvailable() {
        // arrange
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                throw new RuntimeException("something unpredictable happened!!!");
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode(), is(not(0)));
        assertThat(transaction.getStf(), is(1));
        assertThat(transaction.getStfResult(), is(0));
    }

    @Test
    public void shouldReturnNotReverseAndSetSTFWhenResultUnknown() {
        // arrange
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode(), is(not(0)));
        assertThat(transaction.getStf(), is(1));
        assertThat(transaction.getStfResult(), is(0));
    }

    @Test
    public void shouldReturnNotReverseWhenAlreadySetSTFButNotResolvedYet() {
        // arrange
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        // first attempt
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // second attempt
        response = wsclient.mtn(username, password, action,
                                bankCode, amount,
                                channel, state, bankReceipt, orderId,
                                consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode(), is(not(0)));
        assertThat(transaction.getStf(), is(1));
        assertThat(transaction.getStfResult(), is(0));
    }

    @Test
    public void shouldReturnNOKWhenAlreadySetSTFAndResolvedToFailed() {
        // arrange
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        // first attempt
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assuming STF resolved to failed
        jdbcTemplate.update("update info_topup_transactions set stf=3");
        // second attempt
        response = wsclient.mtn(username, password, action,
                                bankCode, amount,
                                channel, state, bankReceipt, orderId,
                                consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode(), is(not(0)));
        assertThat(transaction.getStf(), is(3));
        assertThat(transaction.getStfResult(), is(0));
    }

    @Test
    public void shouldReturnNOKWhenAlreadySetSTFAndResolvedToSuccessful() {
        // arrange
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        // first attempt
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assuming STF resolved to failed
        jdbcTemplate.update("update info_topup_transactions set stf=2, oprresponse='done', oprtid=123654");
        // second attempt
        response = wsclient.mtn(username, password, action,
                                bankCode, amount,
                                channel, state, bankReceipt, orderId,
                                consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is("123654"));             // operator response detail
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));            // this is because first attempt that failed
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode(), is(not(0)));
        assertThat(transaction.getOperatorResponse(), is("done"));
        assertThat(transaction.getOperatorTId(), is("123654"));
        assertThat(transaction.getStf(), is(2));
        assertThat(transaction.getStfResult(), is(0));
    }

    @Test
    public void shouldReturnNOtReverseWhenAlreadySetSTFAndResolvedValueInvalid() {
        // arrange
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                return null;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                return null;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        // first attempt
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // assuming STF resolved to failed
        jdbcTemplate.update("update info_topup_transactions set stf=-1");
        // second attempt
        response = wsclient.mtn(username, password, action,
                                bankCode, amount,
                                channel, state, bankReceipt, orderId,
                                consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode(), is(not(0)));
        assertThat(transaction.getStf(), is(1));
        assertThat(transaction.getStfResult(), is(0));
    }

    @Test
    public void shouldReturnRepetitiveTransactionWhenTheSameTransactionAlreadySuccesseeded() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        // first attempt
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // second attempt
        response = wsclient.mtn(username, password, action,
                                bankCode, amount,
                                channel, state, bankReceipt, orderId,
                                consumer, customerIp);

        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.REPETITIVE_TRANSACTION));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getToken(), is(nullValue()));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void shouldReturnRepetitiveTransactionWhenTheSameTransactionAlreadyFailed() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge not done";
        String mtnCommandStatus = "NOK";
        String mtnResultCode = "2";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "mtnrcpt";
        String orderId = "orderid";
        String consumer = "09385067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        // first attempt
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // second attempt
        response = wsclient.mtn(username, password, action,
                                bankCode, amount,
                                channel, state, bankReceipt, orderId,
                                consumer, customerIp);

        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.REPETITIVE_TRANSACTION));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getToken(), is(nullValue()));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void shouldReturnDoubleSpendingTransactionWhenATransactionWithSameRrnAndBankCodeAndClientAlreadyRegistered() {
        // arrange
        String mtnTransactionId = "1111";
        String mtnOrigResponseMessage = "recharge done";
        String mtnCommandStatus = "OK";
        String mtnResultCode = "0";
        MTNProxy mtnService = new MTNProxy() {
            @Override
            public MTNProxyResponse recharge(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse billPayment(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse bulkTransfer(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse wow(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse postPaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse prePaidWimax(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }

            @Override
            public MTNProxyResponse gprs(String consumer, int amount, long trId) {
                MTNProxyResponse response = new MTNProxyResponse();
                response.setTransactionId(mtnTransactionId);
                response.setOrigResponseMessage(mtnOrigResponseMessage);
                response.setCommandStatus(mtnCommandStatus);
                response.setResultCode(mtnResultCode);
                return response;
            }
        };
        mtnws.setServiceImpl(mtnService);
        mtnws.publish();
        String username = "root";
        String password = "123456";
        String action = "top-up";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "jirrcpt";
        String orderId = "orderid";
        String consumer = "09365067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";

        // act
        // first attempt
        ISGServiceResponse response = wsclient.mtn(username, password, action,
                                      bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp);
        // changing one or more params excluding bankreceipt,bankcode,client(username,password)
        orderId += "123";
        // second attempt
        response = wsclient.mtn(username, password, action,
                                bankCode, amount,
                                channel, state, bankReceipt, orderId,
                                consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("ERROR"));
        assertThat(response.getISGDoc(), is((long)ErrorCodes.DOUBLE_SPENDING_TRANSACTION));
        assertThat(response.getOPRDoc(), is(nullValue()));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getToken(), is(nullValue()));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getOperatorResponseCode().toString(), is(mtnResultCode));
        assertThat(transaction.getOperatorResponse(), is(mtnOrigResponseMessage));
        assertThat(transaction.getOperatorTId(), is(mtnTransactionId));
        assertThat(transaction.getOperatorCommand(), is(mtnCommandStatus));
        assertThat(transaction.getStf(), is(nullValue()));
    }
}
