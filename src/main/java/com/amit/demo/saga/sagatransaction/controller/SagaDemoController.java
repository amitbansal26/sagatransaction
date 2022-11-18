package com.amit.demo.saga.sagatransaction.controller;

import com.amit.demo.saga.sagatransaction.TransactionRequest;
import com.amit.demo.saga.sagatransaction.domain.Citizen;
import com.amit.demo.saga.sagatransaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SagaDemoController {

        @Autowired
        TransactionService transactionService;

        @PostMapping("/startTransaction")
        public void startTransaction(@RequestBody TransactionRequest request) {
            transactionService.startTransaction(request.getConsumerKey(), request.getEmitraId(), request.getAmount());
        }

        @PostMapping("/doPayment")
        public void doPayment(@RequestBody TransactionRequest request) {
            transactionService.doPayment(request.getConsumerKey(), request.getEmitraId(), request.getAmount());
        }

        @GetMapping("/getAccountDetails")
        public Citizen getAccountDetails(@RequestBody TransactionRequest request) {
            return transactionService.getAccountDetails(request.getConsumerKey(), request.getEmitraId(), request.getAmount());
        }

        @PostMapping("/completeTransaction")
        public void completeTransaction(@RequestBody TransactionRequest request) {
            transactionService.completeTransaction(request.getConsumerKey(), request.getEmitraId(), request.getAmount());
        }
    }

