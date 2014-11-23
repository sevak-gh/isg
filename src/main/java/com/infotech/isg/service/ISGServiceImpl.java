package com.infotech.isg.service;

import com.infotech.isg.service.AccessControl;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
* ISG service implementation.
*
* @author Sevak Gharibian
*/
@Service("ISGService")
public class ISGServiceImpl implements ISGService {

    private AccessControl accessControl;
    private OperatorRepository operatorRepository;
    private PaymentChannelRepository paymentChannelRepository;
    private TransactionRepository transactionRepository;

    @Autowired
    public void setAccesControl(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setPaymentChannelRepository(PaymentChannelRepository paymentChannelRepository) {
        this.paymentChannelRepository = paymentChannelRepository;
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ISGServiceResponse mci(String username, String password,
                                  String bankCode, int amount,
                                  int channel, String state,
                                  String bankReceipt, String orderId,
                                  String consumer, String customerIp,
                                  String remoteIp) {

        RequestValidator validator = new MCIRequestValidator();

        return new ISGServiceResponse() {
            {
                setStatus("OK");
                setISGDoc(567);
                setOPRDoc("YES");
            }
        };
    }
}
