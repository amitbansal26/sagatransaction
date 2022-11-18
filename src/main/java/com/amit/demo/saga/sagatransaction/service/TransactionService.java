package com.amit.demo.saga.sagatransaction.service;

import com.amit.demo.saga.sagatransaction.domain.Citizen;
import com.amit.demo.saga.sagatransaction.workflow.TransactionWorkflow;
import com.google.common.base.Throwables;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    @Autowired
    private WorkflowServiceStubs workflowServiceStubs;

    @Autowired
    WorkflowClient workflowClient;

    public void startTransaction(long consumerKey, long emitraId , BigDecimal amount){
        TransactionWorkflow workflow = createWorkflowConnection(consumerKey, emitraId);
        WorkflowClient.start(workflow::startTransactionWorkflow, consumerKey, emitraId, amount);
    }
    public void doPayment(long consumerKey, long emitraId, BigDecimal amount) {
        try {
            TransactionWorkflow workflow = workflowClient.newWorkflowStub(TransactionWorkflow.class, "TransactionWorkflow_" + consumerKey + "_" + emitraId);
            workflow.signalBackupCompleted(consumerKey, emitraId);

            TransactionWorkflow transferCompleteWorkflow = workflowClient.newWorkflowStub(TransactionWorkflow.class, "TransactionWorkflow_" + consumerKey + "_" + emitraId);
            transferCompleteWorkflow.signalPaymentCompleted(consumerKey, emitraId, amount);

            TransactionWorkflow registerActivityWorkflow = workflowClient.newWorkflowStub(TransactionWorkflow.class, "TransactionWorkflow_" + consumerKey + "_" + emitraId);
            registerActivityWorkflow.signalCustomerActivityRegistered(consumerKey, emitraId, amount);
        }
        catch(ApplicationFailure ex) {
            System.out.println("Application Failure");
            throw ex;
        }
        catch(WorkflowException we) {
            System.out.println("\n Stack Trace:\n" + Throwables.getStackTraceAsString(we));
            Throwable cause = Throwables.getRootCause(we);
            System.out.println("\n Root cause: " + cause.getMessage());
            throw we;
        }
        catch(Exception ge) {
            ge.printStackTrace();
            throw ge;
        }
    }

    public TransactionWorkflow createWorkflowConnection(long consumerKey, long emitraId) {
        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(TransactionWorkflow.QUEUE_NAME)
                .setWorkflowId("TransactionWorkflow_" + consumerKey + "_" + emitraId).build();
        return workflowClient.newWorkflowStub(TransactionWorkflow.class, options);
    }

    public void completeTransaction(long consumerKey, long emitraId, BigDecimal amount) {
        TransactionWorkflow workflow = workflowClient.newWorkflowStub(TransactionWorkflow.class, "TransactionWorkflow_" + consumerKey + "_" + emitraId);
        workflow.signalTransactionCompleted(consumerKey, emitraId, amount);
    }

    public Citizen getAccountDetails(long consumerKey, long emitraId, BigDecimal amount) {
        TransactionWorkflow workflow = workflowClient.newWorkflowStub(TransactionWorkflow.class, "TransactionWorkflow_" + consumerKey + "_" + emitraId);
        return null;
    }
}
