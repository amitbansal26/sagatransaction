package com.amit.demo.saga.sagatransaction;

import com.amit.demo.saga.sagatransaction.activity.TransactionActivity;
import com.amit.demo.saga.sagatransaction.activity.TransactionActivityImpl;
import com.amit.demo.saga.sagatransaction.workflow.TransactionWorkflow;
import com.amit.demo.saga.sagatransaction.workflow.TransactionWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SagatransactionApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(SagatransactionApplication.class, args);
		WorkerFactory factory = applicationContext.getBean(WorkerFactory.class);
		TransactionActivity transactionActivity = applicationContext.getBean(TransactionActivity.class);
		Worker worker = factory.newWorker(TransactionWorkflow.QUEUE_NAME);
		worker.registerWorkflowImplementationTypes(TransactionWorkflowImpl.class);
		worker.registerActivitiesImplementations(transactionActivity);
		factory.start();
	}

}
