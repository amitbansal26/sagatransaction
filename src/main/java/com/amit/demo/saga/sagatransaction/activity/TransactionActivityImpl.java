package com.amit.demo.saga.sagatransaction.activity;

import com.amit.demo.saga.sagatransaction.domain.Citizen;

import java.math.BigDecimal;

public class TransactionActivityImpl implements TransactionActivity{

    @Override
    public void initiateTransaction(long consumerKey, long emitraId, BigDecimal amount) {
      System.out.println("Transaction Initiated");
    }

    @Override
    public void cancelTransfer(Citizen citizen) {
        System.out.println("Transaction Cancelled");
    }

    @Override
    public Citizen getCustomerAccountDetails(long consumerKey) {
        return null;
    }

    @Override
    public void registerTransactionActivity(long consumerKey, long emitraId, BigDecimal amount) {
        System.out.println("Register Transaction activity");
    }

    @Override
    public void registerFailedTransaction(long consumerKey, long emitraId, BigDecimal amount) {
        System.out.println("Register Failed Transaction activity");
    }

    @Override
    public boolean checkSufficientBalance(long consumerKey, BigDecimal amount) {
        return true;
    }
}
