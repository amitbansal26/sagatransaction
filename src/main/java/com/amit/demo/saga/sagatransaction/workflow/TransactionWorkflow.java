package com.amit.demo.saga.sagatransaction.workflow;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.math.BigDecimal;

@WorkflowInterface
public interface TransactionWorkflow {
    public static final String QUEUE_NAME = "AmountTransaction";
    @WorkflowMethod
    void startTransactionWorkflow(long consumerKey, long emitraId, BigDecimal amount);

    @SignalMethod
    void signalPaymentCompleted(long consumerKey, long emitraId, BigDecimal amount);

    @SignalMethod
    void signalBackupCompleted(long consumerKey, long emitraId);

    @SignalMethod
    void signalTransactionCompleted(long consumerKey, long emitraId, BigDecimal amount);

    @SignalMethod
    void signalCustomerActivityRegistered(long consumerKey, long emitraId, BigDecimal amount);

}
