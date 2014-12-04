package com.infotech.com.it;

import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.RequestValidator;
import com.infotech.isg.service.AccessControl;
import com.infotech.isg.service.ISGService;
import com.infotech.isg.service.ISGServiceImpl;
import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.proxy.mci.MCIProxy;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;
import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;

import javax.sql.DataSource;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;

/**
* integration test for MCI service
*
* @author Sevak Gahribian
*/
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class MCIIT extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LoggerFactory.getLogger(MCIIT.class);

    @Mock
    private MCIProxy mciProxy;

    @Autowired
    private AccessControl accessControl;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    @Qualifier("MCIValidator")
    private RequestValidator mciValidator;
 
    @Autowired
    @Qualifier("MTNValidator")
    private RequestValidator mtnValidator;

    @Autowired
    @Qualifier("JiringValidator")
    private RequestValidator jiringValidator;

    private ISGService isgService;
    
    @Autowired
    private DataSource dataSource;

    @BeforeMethod
    public void initDB() {
        MockitoAnnotations.initMocks(this);
        isgService = new ISGServiceImpl(accessControl, transactionRepository, mciProxy,
                                        mciValidator, mtnValidator, jiringValidator);
        logger.info("init db...");
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
    }

    @Test
    public void HappyPathShouldSucceed() {
        // arrange
        String username = "root";
        String password = "123456";
        String bankCode = BankCodes.SAMAN;
        int amount = 10000;
        int channel = 59;
        String state = "state";
        String bankReceipt = "receipt";
        String orderId = "orderid";
        String consumer = "09125067064";
        String customerIp = "10.20.120.30";
        String remoteIp = "1.1.1.1";
        when(mciProxy.getToken()).thenReturn(new MCIProxyGetTokenResponse() {{setToken("token");}});
        String operatorResponseCode = "0";
        String operatorResponseDetail = "Done";
        when(mciProxy.recharge(anyString(), anyString(), anyInt(), anyLong())).thenReturn(
            new MCIProxyRechargeResponse() {{setResponse(Arrays.asList(operatorResponseCode, operatorResponseDetail));}});
 
        // act
        ISGServiceResponse response = isgService.mci(username, password, bankCode, amount,
                                                    channel, state, bankReceipt, orderId, 
                                                    consumer, customerIp, remoteIp);
 
        // assert
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(greaterThan(0L)));
        assertThat(response.getOPRDoc(), is(operatorResponseDetail));
    }
}
