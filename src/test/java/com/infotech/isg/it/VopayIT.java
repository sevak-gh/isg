package com.infotech.com.it;

import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.proxy.vopay.VopayProxy;
import com.infotech.isg.proxy.vopay.VopayProxyAccountInfoResponse;
import com.infotech.isg.proxy.vopay.VopayProxyAgentInfo;
import com.infotech.isg.proxy.vopay.VopayProxyAvailablePackagesResponse;
import com.infotech.isg.proxy.vopay.VopayProxyPackageInfo;
import com.infotech.isg.proxy.vopay.VopayProxyPerformTransactionResponse;
import com.infotech.isg.it.fake.vopay.VopayFake;
import com.infotech.isg.it.wsclient.ISGClient;
import com.infotech.isg.domain.ServiceActions;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
 * integration test for Vopay service
 *
 * @author Sevak Gahribian
 */
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class VopayIT extends AbstractTestNGSpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(VopayIT.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionRepository transactionRepo;

    // fake vopay web service
    VopayFake vopayws;

    // isg web service client
    // defined as spring managed bean so that app properties can be used
    @Autowired
    ISGClient wsclient;

    @Value("${vopay.url}")
    private String url;

    @BeforeMethod
    public void initDB() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "info_topup_transactions",
                                       "info_topup_operators",
                                       "info_topup_payment_channel",
                                       "info_topup_clients",
                                       "info_topup_client_ips");
        jdbcTemplate.update("insert into info_topup_operators values(1,'MTN','active'), "
                                                              + "(2,'MCI','active'), "
                                                              + "(3,'Jiring','active'), "
                                                              + "(4,'Rightel','active'), "
                                                              + "(5,'Vopay','active')");
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
        vopayws.stop();
    }

    @Test
    public void HappyPathShouldSucceedForTopup() {
        // arrange
        int errorCode = 0;
        boolean success = true;
        String errorMessage = "";
        String accountId = "40";
        BigDecimal accountBalance = new BigDecimal(356.78);
        String accountCurrency = "Rials";
        int agentId = 1;
        String agentName = "infotech";
        String recipientPhoneNumber = "09882157668";
        String operator = "operator";
        String country = "Afghanistan";
        String packageName = "package";
        BigDecimal price = new BigDecimal(125.55);
        BigDecimal wholesale = new BigDecimal(100.00);
        BigDecimal baseCost = new BigDecimal(100.00);
        BigDecimal transactionFee = new BigDecimal(100.00);
        BigDecimal totalCost = new BigDecimal(100.00);
        BigDecimal proposedSellingPrice = new BigDecimal(100.00);
        String currency = "Rial";
        String timestamp = "2015-11-26 18:20:35";
        int transactionId = 12566;
        String confirmationNumber = "X123";
        boolean emailSent = false;
        boolean smsSent = false;

        VopayProxy vopayService = new VopayProxy() {
            @Override
            public VopayProxyAccountInfoResponse accountInfo() {
                VopayProxyAccountInfoResponse response = new VopayProxyAccountInfoResponse();
                response.setSuccess(success);
                response.setErrorMessage(errorMessage);
                response.setAccountId(accountId);
                response.setAccountBalance(accountBalance);
                response.setAccountCurrency(accountCurrency);
                VopayProxyAgentInfo agent = new VopayProxyAgentInfo();
                agent.setId(agentId);
                agent.setName(agentName);
                response.getAgents().add(agent);
                return response;
            }

            @Override
            public VopayProxyAvailablePackagesResponse availablePackages(String phoneNumber) {
                VopayProxyAvailablePackagesResponse response = new VopayProxyAvailablePackagesResponse();
                response.setSuccess(success);
                response.setErrorMessage(errorMessage);
                response.setPhoneNumber(phoneNumber);
                response.setCountry(country);
                response.setOperator(operator);
                VopayProxyPackageInfo packageInfo = new VopayProxyPackageInfo();
                packageInfo.setName(packageName);
                packageInfo.setBaseCost(baseCost);
                packageInfo.setBaseCost(transactionFee);
                packageInfo.setBaseCost(totalCost);
                packageInfo.setBaseCost(proposedSellingPrice);
                packageInfo.setCurrency(currency);
                response.getPackages().add(packageInfo);
                return response;
            }

            @Override
            public VopayProxyPerformTransactionResponse performTransaction(String recipientPhoneNumber,
                                                                   String packageName,
                                                                   String senderName,
                                                                   String senderPhoneNumber,
                                                                   String senderEmail,
                                                                   String senderMessage) {
                LOG.debug("vopay perform_transaction service: {},{},{},{},{},{}", 
                            recipientPhoneNumber, packageName, senderName, senderPhoneNumber, senderEmail, senderMessage);
                VopayProxyPerformTransactionResponse response = new VopayProxyPerformTransactionResponse();
                response.setSuccess(success);    
                response.setErrorMessage(errorMessage);
                response.setRecipientPhoneNumber(recipientPhoneNumber); 
                response.setCountry(country); 
                response.setOperator(operator); 
                response.setPackageName(packageName); 
                response.setPrice(price); 
                response.setWholesale(wholesale); 
                response.setCurrency(currency); 
                response.setTimestamp(timestamp); 
                response.setTransactionId(transactionId); 
                response.setConfirmationNumber(confirmationNumber); 
                response.setEmailSent(emailSent); 
                response.setSmsSent(smsSent); 
                LOG.debug("vopay perform_transaction response: {}", response);
                return response;    
            }
        };
        vopayws = new VopayFake(vopayService, url);
        vopayws.start();
        String username = "root";
        String password = "123456";
        int clientId = 1;
        String bankCode = BankCodes.SAMAN;
        int amount = 5000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "receiptvopay";
        String orderId = "orderid";
        String consumer = "628123456770";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";
        String action = "5000 IDR";

        // act
        ISGServiceResponse response = wsclient.vopay(username, password, action, bankCode, amount,
                                                     channel, state, bankReceipt, orderId, consumer, customerIp);
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));      // TR ID, any positive number
        assertThat(response.getOPRDoc(), is(String.valueOf(transactionId)));
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId(bankReceipt, BankCodes.SAMAN, clientId);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getRefNum(), is(bankReceipt));
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getToken(), is(nullValue()));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        assertThat(transaction.getAction(), is(ServiceActions.getActionCode(action)));
        assertThat(transaction.getOperatorResponseCode(), is(errorCode));
        assertThat(transaction.getOperatorResponse(), is(errorMessage));
        assertThat(transaction.getOperatorTId(), is(String.valueOf(transactionId)));
        assertThat(transaction.getStf(), is(nullValue()));
    }
}
