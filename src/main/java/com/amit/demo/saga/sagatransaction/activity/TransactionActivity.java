package com.amit.demo.saga.sagatransaction.activity;

import com.amit.demo.saga.sagatransaction.domain.Citizen;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.math.BigDecimal;

@ActivityInterface
public interface TransactionActivity {

    @ActivityMethod
    void initiateTransaction(long consumerKey, long emitraId, BigDecimal amount);

    @ActivityMethod
    void cancelTransfer(Citizen citizen);

    @ActivityMethod
    Citizen getCustomerAccountDetails(long consumerKey);

    @ActivityMethod
    void registerTransactionActivity(long consumerKey, long emitraId, BigDecimal amount);

    @ActivityMethod
    void registerFailedTransaction(long consumerKey, long emitraId, BigDecimal amount);

    @ActivityMethod
    boolean checkSufficientBalance(long senderAcctNum, BigDecimal amount);

}

