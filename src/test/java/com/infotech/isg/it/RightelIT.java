package com.infotech.com.it;

import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.proxy.rightel.RightelProxy;
import com.infotech.isg.proxy.rightel.RightelProxySubmitChargeRequestResponse;
import com.infotech.isg.proxy.rightel.RightelProxyConfirmChargeRequestResponse;
import com.infotech.isg.proxy.rightel.RightelProxyInquiryChargeResponse;
import com.infotech.isg.proxy.rightel.RightelProxyGetAccountBalanceResponse;
import com.infotech.isg.it.fake.rightel.RightelWSFake;
import com.infotech.isg.it.wsclient.ISGClient;
import com.infotech.isg.domain.ServiceActions;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

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
 * integration test for Rightel service
 *
 * @author Sevak Gahribian
 */
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class RightelIT extends AbstractTestNGSpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(RightelIT.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionRepository transactionRepo;

    // fake rightel web service
    // defined as spring managed bean so that app properties can be used
    @Autowired
    RightelWSFake rightelws;

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
        jdbcTemplate.update("insert into info_topup_operators values(1,'MTN','active'), (2,'MCI','active'), (3,'Jiring','active'), (4,'Rightel','active')");
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
        rightelws.stop();
    }

    @Test
    public void HappyPathShouldSucceedForTopup() {
        // arrange
        int errorCode = 0;
        String errorDesc = "Success";
        String requestId = "11";
        BigDecimal vat = new BigDecimal(0);
        BigDecimal discount = new BigDecimal(3000);
        BigDecimal billAmount = new BigDecimal(47000);
        int billState = 1;
        String voucherSerial = "";
        String chargeResponse = "40500000";
        String chargeResponseDesc = "The operation done successfully";
        int status = 4;
        String statusTime = "3/27/2015 3:22:41 PM";
        String transactionId = "TRX123";

        RightelProxy rightelService = new RightelProxy() {
            BigDecimal decimalAmount = null;
            String telNo = null;
           
            @Override
            public RightelProxySubmitChargeRequestResponse submitChargeRequest(String consumer, int amount, int channel) {
                RightelProxySubmitChargeRequestResponse response = new RightelProxySubmitChargeRequestResponse();
                response.setErrorCode(errorCode);
                response.setErrorDesc(errorDesc);
                response.setRequestId(requestId); 
                telNo = consumer;
                response.setTelNo(telNo); 
                decimalAmount = new BigDecimal(amount);
                response.setAmount(decimalAmount); 
                response.setVat(vat); 
                response.setDiscount(discount); 
                response.setBillAmount(billAmount);
                return response;
            }

            @Override
            public RightelProxyConfirmChargeRequestResponse confirmChargeRequest(String requestId, long trId) {
                RightelProxyConfirmChargeRequestResponse response = new RightelProxyConfirmChargeRequestResponse();
                response.setErrorCode(errorCode);
                response.setErrorDesc(errorDesc);
                response.setRequestId(transactionId); 
                response.setTelNo(telNo); 
                response.setAmount(decimalAmount); 
                response.setVat(vat); 
                response.setDiscount(discount); 
                response.setBillAmount(billAmount);
                response.setBillState(billState);
                response.setVoucherSerial(voucherSerial);
                response.setChargeResponse(chargeResponse);
                response.setChargeResponseDesc(chargeResponseDesc);
                response.setTransactionId(String.format("Info%d", trId));
                response.setStatus(status);
                response.setStatusTime(statusTime);
                return response;
               
            }
    
            @Override
            public RightelProxyInquiryChargeResponse inquiryCharge(long trId) {
                throw new UnsupportedOperationException("charge inquiery not supported");
            }

            @Override
            public RightelProxyGetAccountBalanceResponse getAccountBalance() {
                throw new UnsupportedOperationException("get balance not supported");
            }
        };
        rightelws.setServiceImpl(rightelService);
        rightelws.publish();
        String username = "root";
        String password = "123456";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "receipt";
        String orderId = "orderid";
        String consumer = "09215067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";
        String action = "top-up";

        // act
        ISGServiceResponse response = wsclient.rightel(username, password, action, bankCode, amount,
                                      channel, state, bankReceipt, orderId, consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(transactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getToken(), is(requestId));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getAction(), is(ServiceActions.getActionCode(action)));
        assertThat(transaction.getOperatorResponseCode(), is(errorCode));
        assertThat(transaction.getOperatorResponse(), is(chargeResponseDesc));
        assertThat(transaction.getOperatorTId(), is(transactionId));
        assertThat(transaction.getStf(), is(nullValue()));
    }

    @Test
    public void HappyPathShouldSucceedForWow() {
        // arrange
        int errorCode = 0;
        String errorDesc = "Success";
        String requestId = "11";
        BigDecimal vat = new BigDecimal(0);
        BigDecimal discount = new BigDecimal(3000);
        BigDecimal billAmount = new BigDecimal(47000);
        int billState = 1;
        String voucherSerial = "";
        String chargeResponse = "40500000";
        String chargeResponseDesc = "The operation done successfully";
        int status = 4;
        String statusTime = "3/27/2015 3:22:41 PM";
        String transactionId = "TRX123";

        RightelProxy rightelService = new RightelProxy() {
            BigDecimal decimalAmount = null;
            String telNo = null;
           
            @Override
            public RightelProxySubmitChargeRequestResponse submitChargeRequest(String consumer, int amount, int channel) {
                RightelProxySubmitChargeRequestResponse response = new RightelProxySubmitChargeRequestResponse();
                response.setErrorCode(errorCode);
                response.setErrorDesc(errorDesc);
                response.setRequestId(requestId); 
                telNo = consumer;
                response.setTelNo(telNo); 
                decimalAmount = new BigDecimal(amount);
                response.setAmount(decimalAmount); 
                response.setVat(vat); 
                response.setDiscount(discount); 
                response.setBillAmount(billAmount);
                return response;
            }

            @Override
            public RightelProxyConfirmChargeRequestResponse confirmChargeRequest(String requestId, long trId) {
                RightelProxyConfirmChargeRequestResponse response = new RightelProxyConfirmChargeRequestResponse();
                response.setErrorCode(errorCode);
                response.setErrorDesc(errorDesc);
                response.setRequestId(transactionId); 
                response.setTelNo(telNo); 
                response.setAmount(decimalAmount); 
                response.setVat(vat); 
                response.setDiscount(discount); 
                response.setBillAmount(billAmount);
                response.setBillState(billState);
                response.setVoucherSerial(voucherSerial);
                response.setChargeResponse(chargeResponse);
                response.setChargeResponseDesc(chargeResponseDesc);
                response.setTransactionId(String.format("Info%d", trId));
                response.setStatus(status);
                response.setStatusTime(statusTime);
                return response;
               
            }
    
            @Override
            public RightelProxyInquiryChargeResponse inquiryCharge(long trId) {
                throw new UnsupportedOperationException("charge inquiery not supported");
            }

            @Override
            public RightelProxyGetAccountBalanceResponse getAccountBalance() {
                throw new UnsupportedOperationException("get balance not supported");
            }
        };
        rightelws.setServiceImpl(rightelService);
        rightelws.publish();
        String username = "root";
        String password = "123456";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 20000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "receipt";
        String orderId = "orderid";
        String consumer = "09215067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";
        String action = "wow";

        // act
        ISGServiceResponse response = wsclient.rightel(username, password, action, bankCode, amount,
                                      channel, state, bankReceipt, orderId, consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(transactionId));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getToken(), is(requestId));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getAction(), is(ServiceActions.getActionCode(action)));
        assertThat(transaction.getOperatorResponseCode(), is(errorCode));
        assertThat(transaction.getOperatorResponse(), is(chargeResponseDesc));
        assertThat(transaction.getOperatorTId(), is(transactionId));
        assertThat(transaction.getStf(), is(nullValue()));
    }

}
