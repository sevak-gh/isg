package com.infotech.com.it;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.domain.Client;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.repository.ClientRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.AuditLogRepository;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * integration test for operator repository
 *
 * @author Sevak Gahribian
 */
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class JdbcRepositoryIT extends AbstractTestNGSpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcRepositoryIT.class);

    @Autowired
    @Qualifier("JdbcOperatorRepository")
    private OperatorRepository operatorRepo;

    @Autowired
    @Qualifier("JdbcPaymentChannelRepository")
    private PaymentChannelRepository paymentChannelRepo;

    @Autowired
    @Qualifier("JdbcTransactionRepository")
    private TransactionRepository transactionRepo;

    @Autowired
    @Qualifier("JdbcClientRepository")
    private ClientRepository clientRepo;

    @Autowired
    @Qualifier("JdbcAuditLogRepository")
    private AuditLogRepository auditLogRepository;

    @Autowired
    private DataSource dataSource;

    @BeforeClass
    public void initDB() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);;
        jdbcTemplate.update("delete from info_topup_transactions");
        jdbcTemplate.update("delete from info_topup_clients");
        jdbcTemplate.update("delete from info_topup_client_ips");
        // add client: username=root, password=123456, active='Y', ips: 1.1.1.1, 2.2.2.2
        jdbcTemplate.update("insert into info_topup_clients(id,client,pin,name,contact,tel,vendor,created,active) values(1, 'root', "
                            + "'ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346"
                            + "ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413'"
                            + ", 'name', 'contact', 'tel', 'vendor', '2014-01-01 13:05:23','Y')");
        jdbcTemplate.update("insert into info_topup_client_ips values(1,'1.1.1.1')");
        jdbcTemplate.update("insert into info_topup_client_ips values(1,'2.2.2.2')");
        jdbcTemplate.update("delete from info_topup_audit");
    }

    @Test
    public void shouldFindOperatorById() {
        LOG.info("finding operator by id:" + Operator.MCI_ID);
        Operator operator = operatorRepo.findById(Operator.MCI_ID);
        assertThat(operator, is(notNullValue()));
        assertThat(operator.getId(), is(Operator.MCI_ID));
        operator = operatorRepo.findById(0);
        assertThat(operator, is(nullValue()));
    }

    @Test
    public void shouldFindPaymentChannelById() {
        LOG.info("finding channel by id...");
        PaymentChannel channel = paymentChannelRepo.findById("1");
        assertThat(channel, is(nullValue()));
        channel = paymentChannelRepo.findById("59");
        assertThat(channel, is(notNullValue()));
        assertThat(channel.getId(), is("59"));
        assertThat(channel.getIsActive(), is(true));
    }

    @Test
    public void shouldFindClientByUsername() {
        Client client = clientRepo.findByUsername("test");
        assertThat(client, is(nullValue()));

        client = clientRepo.findByUsername("root");
        assertThat(client, is(notNullValue()));
        assertThat(client.getUsername(), is("root"));
        assertThat(client.getVendor(), is("vendor"));
        assertThat(client.getIsActive(), is(true));
        assertThat(client.getIps(), is(notNullValue()));
        assertThat(client.getIps().size(), is(2));
        assertThat(client.getIps().get(0), is("1.1.1.1"));
    }

    @Test
    public void shouldCreateUpdateFindTransaction() {
        List<Transaction> transactions = transactionRepo.findByRefNumBankCodeClientId("ref123456", BankCodes.SAMAN, 3);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(0));

        Transaction transaction = null;
        transaction = new Transaction();
        transaction.setProvider(Operator.MCI_ID);
        transaction.setAction(ServiceActions.TOP_UP);
        transaction.setState("trstate");
        transaction.setResNum("res123456");
        transaction.setRefNum("ref123456");
        transaction.setRemoteIp("10.30.180.38");
        transaction.setAmount(10000);
        transaction.setChannel("54");
        transaction.setConsumer("cnsmr5566");
        transaction.setBankCode(BankCodes.SAMAN);
        transaction.setClientId(3);
        transaction.setCustomerIp("10.1.1.1");
        transaction.setTrDateTime(new Date());
        transaction.setStatus(-1);
        transaction.setBankVerify(new Integer(10000));
        transaction.setVerifyDateTime(new Date());
        transactionRepo.create(transaction);

        transactions = transactionRepo.findByRefNumBankCodeClientId("ref123456", BankCodes.SAMAN, 3);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        assertThat(transactions.get(0).getRefNum(), is("ref123456"));

        transaction.setStatus(1);
        transaction.setOperatorDateTime(new Date());
        transaction.setOperatorResponseCode(0);
        transaction.setOperatorResponse("sim card charged");
        transaction.setToken("MCI-TOKEN:ABC12356");
        transaction.setOperatorTId("sim card charged");
        transactionRepo.update(transaction);

        transactions = transactionRepo.findByRefNumBankCodeClientId("ref123456", BankCodes.SAMAN, 3);
        assertThat(transactions, is(notNullValue()));
        assertThat(transactions.size(), is(1));
        assertThat(transactions.get(0).getRefNum(), is("ref123456"));
        assertThat(transactions.get(0).getStatus(), is(1));
        assertThat(transactions.get(0).getOperatorResponseCode(), is(0));
    }

    @Test
    public void shouldInsertAuditLog() {
        String username = "username";
        String bankCode = "bankCode";
        int amount = 10000;
        String channel = "59";
        String state = "state";
        String bankReceipt = "receipt";
        String orderId = "order";
        String consumer = "09125067064";
        String customerIp = "1.1.1.1";
        String remoteIp = "10.20.30.40";
        String action = "action";
        int operatorId = 1;
        String status = "Error";
        long isgDoc = -5;
        String oprDoc = "";
        Date timestamp = new Date();
        long responseTime = 125;
        auditLogRepository.create(username, bankCode, amount, channel, state, bankReceipt,
                                  orderId, consumer, customerIp, remoteIp, action, operatorId,
                                  status, isgDoc, oprDoc, timestamp, responseTime);
    }
}
