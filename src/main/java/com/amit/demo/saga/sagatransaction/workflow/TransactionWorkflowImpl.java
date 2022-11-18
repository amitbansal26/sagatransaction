package com.amit.demo.saga.sagatransaction.workflow;

import com.amit.demo.saga.sagatransaction.activity.TransactionActivity;
import com.amit.demo.saga.sagatransaction.domain.Citizen;
import com.google.common.base.Throwables;
import io.temporal.activity.ActivityOptions;
import io.temporal.client.WorkflowException;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class TransactionWorkflowImpl implements TransactionWorkflow{
    private final RetryOptions retryOptions = RetryOptions.newBuilder().setMaximumAttempts(10).build();

    private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(120)).setRetryOptions(retryOptions).build();

    private final TransactionActivity transactionActivity = Workflow.newActivityStub(TransactionActivity.class, defaultActivityOptions);

    private final Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(true).setContinueWithError(true).build();

    private final Saga saga = new Saga(sagaOptions);

    private boolean isTransferCompleted = false;

    private boolean isAccountInfoRetrieved = false;

    private boolean isTransactionCompleted = false;

    private boolean isBackupCompleted = false;

    private boolean isCustomerActivityRegistered = false;

    @Override
    public void startTransactionWorkflow(long consumerKey, long emitraId, BigDecimal amount) {
        try{
        Workflow.await( () -> this.isBackupCompleted);
        System.out.println("Backup Completed " + amount);

        saga.addCompensation(transactionActivity::cancelTransfer, new Citizen(112, "dfsdf", 34, Instant.now()));
        try {
            transactionActivity.initiateTransaction(consumerKey, emitraId, amount);
            this.isTransferCompleted = true;
            System.out.println("Signal - Transfer completed.");
        }catch(ApplicationFailure ex) {
            System.out.println("Application Failure.");
            throw ex;
        }
        catch(WorkflowException we) {
            System.out.println("\n Stack Trace \n" + Throwables.getStackTraceAsString(we));
            Throwable cause = Throwables.getRootCause(we);
            System.out.println("\n Root cause: " + cause.getMessage());
            throw we;
        }
        catch(Exception ge) {
            ge.printStackTrace();
            throw ge;
        }
        Workflow.await( () -> this.isCustomerActivityRegistered);
        saga.addCompensation(transactionActivity::registerFailedTransaction, consumerKey, emitraId, amount);
        try {
            transactionActivity.registerTransactionActivity(consumerKey, emitraId, amount);
            this.isCustomerActivityRegistered = true;
            System.out.println("Signal - Customer activity registered.");

        }
        catch(ActivityFailure e) {
            System.out.println("Failed to register customer activity. Execute saga compensation.");
            saga.compensate();
            throw e;
        }
        catch(ApplicationFailure ex) {
            System.out.println("Application Failure.");
            throw ex;
        }
        catch(WorkflowException we) {
            System.out.println("\n Stack Trace \n" + Throwables.getStackTraceAsString(we));
            Throwable cause = Throwables.getRootCause(we);
            System.out.println("\n Root cause: " + cause.getMessage());
            throw we;
        }
        catch(Exception ge) {
            ge.printStackTrace();
            throw ge;
        }

            Workflow.await( () -> this.isTransactionCompleted);
        }
        catch(Exception e) {
            throw e;
        }
    }
    @Override
    public void signalBackupCompleted(long consumerKey, long emitraId) {
        if(this.isBackupCompleted == true) {
            System.out.println("Backup was already taken.");
            return;
        }
        isBackupCompleted = true;
    }
    @Override
    public void signalCustomerActivityRegistered(long consumerKey, long emitraId, BigDecimal amount) {
        if(this.isCustomerActivityRegistered == true) {
            System.out.println("Customer activity was already registered. No further action required.");
            return;
        }
        this.isCustomerActivityRegistered = true;
    }

    @Override
    public void signalPaymentCompleted(long consumerKey, long emitraId, BigDecimal amount) {
        if(this.isTransferCompleted == true) {
            System.out.println("Transfer was already completed. No further action required.");
            return;
        }
        this.isTransferCompleted = true;
    }

    @Override
    public void signalTransactionCompleted(long consumerKey, long emitraId, BigDecimal amount) {
        if(this.isBackupCompleted && this.isTransferCompleted && this.isCustomerActivityRegistered) {
            System.out.println("Signal - Transaction Completed for " + consumerKey + "_" + emitraId);
            this.isTransactionCompleted = true;
        } else {
            System.out.println("Transfer has not completed yet.");
        }
    }
}
