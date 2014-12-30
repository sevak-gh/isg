package com.infotech.isg.validation;

import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.validation.PaymentChannelValidator;
import com.infotech.isg.validation.impl.PaymentChannelValidatorImpl;
import com.infotech.isg.repository.PaymentChannelRepository;

import java.util.Map;
import java.util.HashMap;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
* test cases for paymentChannel validator
*
* @author Sevak Gharibian
*/
public class PaymentChannelValidatorTest {


    private PaymentChannelRepository paymentChannelRepository;
    private PaymentChannelValidator paymentChannelValidator;

    @BeforeClass
    public void setUp() {
        paymentChannelRepository = new PaymentChannelRepository() {
            private Map<String, PaymentChannel> channels = new HashMap<String, PaymentChannel>() {
                {
                    put("59", new PaymentChannel() {{setId("59"); setIsActive(true);}});
                    put("14", new PaymentChannel() {{setId("14"); setIsActive(true);}});
                    put("5", new PaymentChannel() {{setId("5"); setIsActive(true);}});
                    put("25", new PaymentChannel() {{setId("25"); setIsActive(false);}});
                    put("10", new PaymentChannel() {{setId("10"); setIsActive(true);}});
                }
            };

            @Override
            public PaymentChannel findById(String channelId) {
                return channels.get(channelId);
            }
        };
        paymentChannelValidator = new PaymentChannelValidatorImpl(paymentChannelRepository);
    }

    @DataProvider(name = "providePaymentChannels")
    public Object[][] providePaymentChannels() {
        return new Object[][] {
            { -1, ErrorCodes.INVALID_PAYMENT_CHANNEL},
            {0, ErrorCodes.INVALID_PAYMENT_CHANNEL},
            {59, ErrorCodes.OK},
            {14, ErrorCodes.OK},
            {5, ErrorCodes.OK},
            {25, ErrorCodes.DISABLED_PAYMENT_CHANNEL},
            {10, ErrorCodes.OK}
        };
    }

    @Test(dataProvider = "providePaymentChannels")
    public void paymentChannelValidatorShouldReturnExpectedErrorCode(int channelId, int errorCode) {
        // arrange
        // different cases provided by data provider

        // act
        int result = paymentChannelValidator.validate(channelId);

        // assert
        assertThat(result, is(errorCode));
    }
}
