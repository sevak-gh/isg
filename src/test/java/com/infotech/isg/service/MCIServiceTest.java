package com.infotech.isg.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.infotech.isg.domain.Client;
import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.domain.OperatorStatus;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.RequestValidator;
import com.infotech.isg.validation.TransactionValidator;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.OperatorStatusRepository;
import com.infotech.isg.proxy.mci.MCIProxy;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;
import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;
import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.impl.MCIServiceImpl;

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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * test cases for MCI implementation for ISG service.
 *
 * @author Sevak Gharibian
 */
public class MCIServiceTest {

    private ISGService mciService;

    @Mock
    private OperatorService mciOperatorService;

    @Mock
    private AccessControl accessControl;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RequestValidator requestValidator;

    @Mock
    private TransactionValidator transactionValidator;

    @Mock
    private OperatorStatusRepository operatorStatusRepository;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mciService = new MCIServiceImpl(accessControl, transactionRepository, mciOperatorService,
                                        requestValidator, transactionValidator, operatorStatusRepository);
    }

    @Test
    public void shouldReturnOKWhenSuccessful() {
        // arrange
        // set all validators to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        when(accessControl.getClient()).thenReturn(new Client() {{setId(1);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);

        // bypass proxy
        String code = "0";
        String message = "OK";
        String trId = "1236549870";
        String token = "tok";
        when(mciOperatorService.topup(anyString(), anyInt(), anyLong(), anyString())).thenReturn(
        new OperatorServiceResponse() {{
                setCode(code);
                setMessage(message);
                setTransactionId(trId);
                setToken(token);
            }
        });

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(0L));
        assertThat(response.getOPRDoc(), is(trId));
    }

    @Test
    public void shouldHaveInteractionsWhenSuccessful() {
        // arrange
        String username = "username";
        String password = "password";
        String bankCode = "054";
        int amount = 10000;
        String channel = "1";
        String state = "state";
        String bankReceipt = "receipt";
        String orderId = "orderid";
        String consumer = "consumer";
        String customerIp = "customer";
        String remoteIp = "ip";
        String action = "top-up";
        int operatorId = Operator.MCI_ID;
        long expectedTransactionId = 0L;
        int expectedStatus = 1;
        // set all validators to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // bypass proxy
        String token = "token";
        int code = 0;
        String message = "OK";
        String trId = "1236549870";
        when(mciOperatorService.topup(anyString(), anyInt(), anyLong(), anyString()))
        .thenReturn(new OperatorServiceResponse() {{
                setCode(Integer.toString(code));
                setMessage(message);
                setTransactionId(trId);
                setToken(token);
            }
        });

        // act
        ISGServiceResponse response = mciService.topup(username, password, bankCode, amount,
                                      channel, state, bankReceipt, orderId,
                                      consumer, customerIp, remoteIp, action);

        // assert
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(expectedTransactionId));
        assertThat(response.getOPRDoc(), is(trId));
        verify(requestValidator).validate(username, password, bankCode, amount, channel,
                                          state, bankReceipt, orderId, consumer, customerIp, remoteIp, action, operatorId);
        verify(accessControl).authenticate(username, password, remoteIp);
        verify(transactionValidator).validate(bankReceipt, bankCode, clientId, orderId,
                                              operatorId, amount, channel, consumer, customerIp);
        verify(mciOperatorService).topup(consumer, amount, expectedTransactionId, action);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).create(captor.capture());
        Transaction transaction = captor.getValue();
        assertThat(transaction.getId(), is(expectedTransactionId));
        assertThat(transaction.getAction(), is(ServiceActions.TOP_UP));
        assertThat(transaction.getAmount(), is((long)amount));
        assertThat(transaction.getConsumer(), is(consumer));
        verify(transactionRepository).update(captor.capture());
        transaction = captor.getValue();
        assertThat(transaction.getStatus(), is(expectedStatus));
        assertThat(transaction.getToken(), is(token));
        assertThat(transaction.getOperatorResponseCode(), is(code));
        assertThat(transaction.getOperatorResponse(), is(message));
        assertThat(transaction.getOperatorTId(), is(trId));
        assertThat(transaction.getStf(), is(nullValue()));
        verifyNoMoreInteractions(transactionRepository);
        verifyNoMoreInteractions(mciOperatorService);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenValidationError() {
        // arrange
        // set amount validator to error
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.INVALID_AMOUNT);

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.INVALID_AMOUNT));
        verifyZeroInteractions(mciOperatorService);
        verifyZeroInteractions(accessControl);
        verifyZeroInteractions(transactionValidator);
        verifyZeroInteractions(transactionRepository);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenDoubleSpendingTransactionError() {
        // arrange
        // set transaction validator to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.DOUBLE_SPENDING_TRANSACTION);

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.DOUBLE_SPENDING_TRANSACTION));
        verifyZeroInteractions(transactionRepository);
        verifyZeroInteractions(mciOperatorService);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenRepetitiveTransactionError() {
        // arrange
        // set transaction validator to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.REPETITIVE_TRANSACTION);

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.REPETITIVE_TRANSACTION));
        verifyZeroInteractions(transactionRepository);
        verifyZeroInteractions(mciOperatorService);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenSTFNotResolvedError() {
        // arrange
        // set transaction validator to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE);

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE));
        verifyZeroInteractions(transactionRepository);
        verifyZeroInteractions(mciOperatorService);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenSTFResolvedFailedError() {
        // arrange
        // set transaction validator to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK);

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK));
        verifyZeroInteractions(transactionRepository);
        verifyZeroInteractions(mciOperatorService);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenSTFResolvedSuccessful() {
        // arrange
        // set transaction validator to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.STF_RESOLVED_SUCCESSFUL);
        // set transaction repository
        long transactionId = 5L;
        when(transactionRepository.findByRefNumBankCodeClientId(anyString(), anyString(), anyInt()))
        .thenReturn(new ArrayList<Transaction>() {{add(new Transaction() {{setId(transactionId);}});}});

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is(transactionId));
        assertThat(response.getStatus(), is("OK"));
        verify(transactionRepository).findByRefNumBankCodeClientId("receipt", "054", clientId);
        verifyNoMoreInteractions(transactionRepository);
        verifyZeroInteractions(mciOperatorService);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenSTFInvalidError() {
        // arrange
        // set transaction validator to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE);

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE));
        verifyZeroInteractions(mciOperatorService);
    }

    @Test
    public void shouldNotHaveProxyInteractionsWhenAuthenticationError() {
        // arrange
        // set all validators to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to error
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.INVALID_USERNAME_OR_PASSWORD);

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.INVALID_USERNAME_OR_PASSWORD));
        verifyZeroInteractions(transactionRepository);
        verifyZeroInteractions(transactionValidator);
        verifyZeroInteractions(mciOperatorService);
    }

    @Test
    public void shouldReturnOperatorErrorWhenGetTokenFailed() {
        // arrange
        // set all validators to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // bypass proxy, null means operation failed in any reason, but not ambiguous
        when(mciOperatorService.topup(anyString(), anyInt(), anyLong(), anyString())).thenReturn(null);

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_ERROR));
    }

    @Test
    public void shouldReturnOperatorErrorNotReverseWhenRechargeThrowsException() {
        // arrange
        // set all validators to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // mci proxy throws ISGException
        when(mciOperatorService.topup(anyString(), anyInt(), anyLong(), anyString()))
        .thenThrow(new OperatorUnknownResponseException("ambiguous response from MCI service provider"));

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE));
        verify(mciOperatorService).topup("consumer", 10000, 0L, "top-up");
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).update(captor.capture());
        Transaction transaction = captor.getValue();
        assertThat(transaction.getStatus(), is(ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE));
        assertThat(transaction.getStf(), is(1));
        assertThat(transaction.getStfResult(), is(0));
        assertThat(transaction.getOperatorResponseCode(), is(not(0)));
    }

    @Test
    public void shouldReturnNOKWhenRechargeNotSuccessful() {
        // arrange
        // set all validators to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // recharge responds NOK
        String token = "token";
        int responseCode = -1011;
        String responseDetail = "NOK";
        when(mciOperatorService.topup(anyString(), anyInt(), anyLong(), anyString()))
        .thenReturn(new OperatorServiceResponse() {{
                setCode(Integer.toString(responseCode));
                setMessage(responseDetail);
                setTransactionId(responseDetail);
                setToken(token);
            }
        });

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");

        // assert
        assertThat(response.getISGDoc(), is((long)ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK));
        verify(mciOperatorService).topup("consumer", 10000, 0L, "top-up");
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).update(captor.capture());
        Transaction transaction = captor.getValue();
        assertThat(transaction.getStatus(), is(ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK));
        assertThat(transaction.getOperatorResponseCode(), is(responseCode));
        assertThat(transaction.getOperatorResponse(), is(responseDetail));
        assertThat(transaction.getOperatorTId(), is(responseDetail));
        assertThat(transaction.getToken(), is(token));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldReThrowRuntimeException() {
        // arrange
        // set all validators to OK
        when(requestValidator.validate(anyString(), anyString(), anyString(), anyInt(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyString(), anyString(), anyString(), anyString(),
                                       anyInt())).thenReturn(ErrorCodes.OK);
        // set authentication to OK
        when(accessControl.authenticate(anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        int clientId = 1;
        when(accessControl.getClient()).thenReturn(new Client() {{setId(clientId);}});
        // set transaction validation to OK
        when(transactionValidator.validate(anyString(), anyString(), anyInt(), anyString(), anyInt(),
                                           anyInt(), anyString(), anyString(), anyString())).thenReturn(ErrorCodes.OK);
        // mci proxy throws RuntimeException
        when(mciOperatorService.topup(anyString(), anyInt(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("something strange happened during charge"));

        // act
        ISGServiceResponse response = mciService.topup("username", "password", "054", 10000,
                                      "1", "state", "receipt", "orderid",
                                      "consumer", "customer", "ip", "top-up");
        // assert
    }

    @Test
    public void shouldReturnOperatorStatus() {
        // arrange
        when(operatorStatusRepository.findById(Operator.MCI_ID))
        .thenReturn(new OperatorStatus() {{ setId(Operator.MCI_ID); setIsAvailable(true); setTimestamp(new Date()); }});

        // act
        ISGServiceResponse response = mciService.isOperatorAvailable();

        // assert
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(1L));
        assertThat(response.getOPRDoc(), is(nullValue()));
    }

    @Test
    public void shouldVerifyTransaction() {
        // arrange
        when(transactionRepository.findByProviderTransactionId(anyInt(), anyString()))
        .thenReturn(new Transaction() {{ setId(1); setStatus(1); setOperatorTId("T123456"); }});

        // act
        ISGServiceResponse response = mciService.verifyTransaction("09125067064", "T123456");

        // assert
        assertThat(response.getStatus(), is("OK"));
        assertThat(response.getISGDoc(), is(1L));
        assertThat(response.getOPRDoc(), is("T123456"));
    }
}
