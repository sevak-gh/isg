package com.infotech.isg.service;

import java.util.Arrays;

import com.infotech.isg.domain.Client;
import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.RequestValidator;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.proxy.mci.MCIProxy;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;
import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
* test cases for ISG service.
*
* @author Sevak Gharibian
*/
public class ISGServiceTest {

    private ISGService isgService;

    @Mock
    private MCIProxy mciProxy;

    @Mock
    private AccessControl accessControl;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RequestValidator mciValidator;

    @Mock
    private RequestValidator mtnValidator;

    @Mock
    private RequestValidator jiringValidator;


    @BeforeMethod(alwaysRun = true)
    public void setup() {
        MockitoAnnotations.initMocks(this);
        isgService = new ISGServiceImpl(accessControl, transactionRepository, mciProxy,
                                        mciValidator, mtnValidator, jiringValidator);
    }

    @Test
    public void shouldReturnOKWhenSuccessful() {
        // arrange
        // set all validators to OK
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAction(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateCellNumber(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateBankCode(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateOperator(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validatePaymentChannel(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateTransaction(anyObject(), anyString(),
                                              anyInt(), anyInt(), anyInt(),
                                              anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(accessControl.getClient()).thenReturn(new Client() {{setId(1);}});
        // bypass proxy
        when(mciProxy.getToken()).thenReturn(new MCIProxyGetTokenResponse() {{setToken("");}});
        when(mciProxy.recharge(anyString(), anyString(), anyInt(), anyLong()))
        .thenReturn(new MCIProxyRechargeResponse() {{setResponse(Arrays.asList("0", "OK"));}});

        // act
        ISGServiceResponse response = isgService.mci("username", "password", "054", 10000,
                                      1, "state", "receipt", "orderid",
                                      "consumer", "customer", "ip");
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.OK));
    }

    @Test
    public void shouldHaveInteractionsWhenSuccessful() {
        // arrange
        String username = "username";
        String password = "password";
        String bankCode = "054";
        int amount = 10000;
        int channel = 1;
        String state = "state";
        String bankReceipt = "receipt";
        String orderId = "orderid";
        String consumer = "consumer";
        String customerIp = "customer";
        String remoteIp = "ip";
        String action = "top-up";
        int operatorId = Operator.MCI_ID;
        // set all validators to OK
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAction(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateCellNumber(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateBankCode(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateOperator(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validatePaymentChannel(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateTransaction(anyObject(), anyString(),
                                              anyInt(), anyInt(), anyInt(),
                                              anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // bypass proxy
        String token = "token";
        when(mciProxy.getToken()).thenReturn(new MCIProxyGetTokenResponse() {{setToken(token);}});
        int responseCode = 0;
        String responseDetail = "OK";
        when(mciProxy.recharge(anyString(), anyString(), anyInt(), anyLong()))
        .thenReturn(new MCIProxyRechargeResponse() {{setResponse(Arrays.asList(Integer.toString(responseCode), responseDetail));}});

        // act
        ISGServiceResponse response = isgService.mci(username, password, bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp, remoteIp);
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.OK));
        verify(mciValidator).validateRequiredParams(username, password, action, bankCode, amount, channel,
                state, bankReceipt, orderId, consumer, customerIp);
        verify(mciValidator).validateAmount(amount);
        verify(mciValidator).validateCellNumber(consumer);
        verify(mciValidator).validateBankCode(bankCode);
        verify(mciValidator).validateOperator(operatorId);
        verify(mciValidator).validatePaymentChannel(channel);
        verify(accessControl).authenticate(username, password, remoteIp);
        verify(transactionRepository).findByRefNumBankCodeClientId(bankReceipt, bankCode, clientId);
        verify(mciProxy).getToken();
        verify(mciProxy).recharge(token, consumer, amount, 0);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).create(captor.capture());
        Transaction transaction = captor.getValue();
        assertThat(transaction.getId(), is(0L));
        assertThat(transaction.getAction(), is(ServiceActions.TOP_UP));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        verify(transactionRepository).update(captor.capture());
        transaction = captor.getValue();
        assertThat(transaction.getStatus(), is(1));
        assertThat(transaction.getToken(), is(token));
        assertThat(transaction.getOperatorResponseCode(), is(responseCode));
        assertThat(transaction.getOperatorResponse(), is(responseDetail));
        assertThat(transaction.getOperatorTId(), is(responseDetail));
        assertThat(transaction.getStf(), is(nullValue()));
        verifyNoMoreInteractions(transactionRepository);
        verifyNoMoreInteractions(mciProxy);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenValidationError() {
        // arrange
        // set amount validator to error
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.INVALID_AMOUNT);

        // act
        ISGServiceResponse response = isgService.mci("username", "password", "054", 10000,
                                      1, "state", "receipt", "orderid",
                                      "consumer", "customer", "ip");
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.INVALID_AMOUNT));
        verifyZeroInteractions(transactionRepository);
        verifyZeroInteractions(mciProxy);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenDuplicateTransactionError() {
        // arrange
        // set transaction validator to error
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAction(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateCellNumber(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateBankCode(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateOperator(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validatePaymentChannel(anyInt())).thenReturn(ErrorCodes.OK);
        when(transactionRepository.findByRefNumBankCodeClientId(anyString(), anyString(), anyInt()))
        .thenReturn(Arrays.asList(new Transaction()));
        when(mciValidator.validateTransaction(anyObject(), anyString(), anyInt(), anyInt(), anyInt(), anyString(), anyString()))
        .thenReturn(ErrorCodes.DOUBLE_SPENDING_TRANSACTION);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});

        // act
        ISGServiceResponse response = isgService.mci("username", "password", "054", 10000,
                                      1, "state", "receipt", "orderid",
                                      "consumer", "customer", "ip");
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.DOUBLE_SPENDING_TRANSACTION));
        verify(transactionRepository).findByRefNumBankCodeClientId("receipt", "054", clientId);
        verifyNoMoreInteractions(transactionRepository);
        verifyZeroInteractions(mciProxy);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenAuthenticationError() {
        // arrange
        // set all validators to OK
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAction(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateCellNumber(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateBankCode(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateOperator(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validatePaymentChannel(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateTransaction(anyObject(), anyString(), anyInt(), anyInt(), anyInt(), anyString(), anyString()))
        .thenReturn(ErrorCodes.OK);
        // set authentication to error
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.INVALID_USERNAME_OR_PASSWORD);

        // act
        ISGServiceResponse response = isgService.mci("username", "password", "054", 10000,
                                      1, "state", "receipt", "orderid",
                                      "consumer", "customer", "ip");
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.INVALID_USERNAME_OR_PASSWORD));
        verifyZeroInteractions(transactionRepository);
        verifyZeroInteractions(mciProxy);
    }

    @Test
    public void shouldReturnOperatorErrorWhenGetTokenFailed() {
        // arrange
        // set all validators to OK
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAction(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateCellNumber(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateBankCode(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateOperator(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validatePaymentChannel(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateTransaction(anyObject(), anyString(),
                                              anyInt(), anyInt(), anyInt(),
                                              anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});

        // act
        ISGServiceResponse response = isgService.mci("username", "password", "054", 10000,
                                      1, "state", "receipt", "orderid",
                                      "consumer", "customer", "ip");
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.OPERATOR_SERVICE_ERROR));
        verify(mciProxy).getToken();
        verifyNoMoreInteractions(mciProxy);
        verify(transactionRepository).findByRefNumBankCodeClientId("receipt", "054", clientId);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).create(captor.capture());
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    public void shouldReturnOperatorErrorWhenRechargeThrowsException() {
        // arrange
        // set all validators to OK
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAction(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateCellNumber(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateBankCode(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateOperator(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validatePaymentChannel(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateTransaction(anyObject(), anyString(),
                                              anyInt(), anyInt(), anyInt(),
                                              anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // bypass proxy
        String token = "token";
        when(mciProxy.getToken()).thenReturn(new MCIProxyGetTokenResponse() {{setToken(token);}});
        when(mciProxy.recharge(anyString(), anyString(), anyInt(), anyLong()))
        .thenThrow(new ISGException(ErrorCodes.OPERATOR_SERVICE_ERROR, "recharge operation failed"));

        // act
        ISGServiceResponse response = isgService.mci("username", "password", "054", 10000,
                                      1, "state", "receipt", "orderid",
                                      "consumer", "customer", "ip");
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.OPERATOR_SERVICE_ERROR));
        verify(mciProxy).getToken();
        verify(mciProxy).recharge(token, "consumer", 10000, 0);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).update(captor.capture());
        Transaction transaction = captor.getValue();
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getStf(), is(1));
        assertThat(transaction.getStfResult(), is(0));
        assertThat(transaction.getOperatorResponseCode(), is(2));
    }

    @Test
    public void shouldReturnOperatorErrorWhenRechargeResponseInvalid() {
        // arrange
        // set all validators to OK
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAction(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateCellNumber(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateBankCode(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateOperator(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validatePaymentChannel(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateTransaction(anyObject(), anyString(),
                                              anyInt(), anyInt(), anyInt(),
                                              anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // bypass proxy
        String token = "token";
        when(mciProxy.getToken()).thenReturn(new MCIProxyGetTokenResponse() {{setToken(token);}});

        // act
        ISGServiceResponse response = isgService.mci("username", "password", "054", 10000,
                                      1, "state", "receipt", "orderid",
                                      "consumer", "customer", "ip");
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.OPERATOR_SERVICE_ERROR));
        verify(mciProxy).getToken();
        verify(mciProxy).recharge(token, "consumer", 10000, 0);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).update(captor.capture());
        Transaction transaction = captor.getValue();
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getStf(), is(1));
        assertThat(transaction.getStfResult(), is(0));
        assertThat(transaction.getOperatorResponseCode(), is(2));
    }

    @Test
    public void shouldReturnServiceUnavailableWhenRechargeNotSuccessful() {
        // arrange
        // set all validators to OK
        when(mciValidator.validateRequiredParams(anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAmount(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateAction(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateCellNumber(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateBankCode(anyString())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateOperator(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validatePaymentChannel(anyInt())).thenReturn(ErrorCodes.OK);
        when(mciValidator.validateTransaction(anyObject(), anyString(),
                                              anyInt(), anyInt(), anyInt(),
                                              anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // bypass proxy
        String token = "token";
        when(mciProxy.getToken()).thenReturn(new MCIProxyGetTokenResponse() {{setToken(token);}});
        int responseCode = 1011;
        String responseDetail = "NOK";
        when(mciProxy.recharge(anyString(), anyString(), anyInt(), anyLong()))
        .thenReturn(new MCIProxyRechargeResponse() {{setResponse(Arrays.asList(Integer.toString(responseCode), responseDetail));}});

        // act
        ISGServiceResponse response = isgService.mci("username", "password", "054", 10000,
                                      1, "state", "receipt", "orderid",
                                      "consumer", "customer", "ip");
        int result = (int)response.getISGDoc();

        // assert
        assertThat(result, is(ErrorCodes.OPERATOR_SERVICE_UNAVAILABLE));
        verify(mciProxy).getToken();
        verify(mciProxy).recharge(token, "consumer", 10000, 0);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).update(captor.capture());
        Transaction transaction = captor.getValue();
        assertThat(transaction.getStatus(), is(-1));
        assertThat(transaction.getOperatorResponseCode(), is(responseCode));
        assertThat(transaction.getOperatorResponse(), is(responseDetail));
        assertThat(transaction.getToken(), is(token));
        assertThat(transaction.getOperatorTId(), is(responseDetail));
    }
}
