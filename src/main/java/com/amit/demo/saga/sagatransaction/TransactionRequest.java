package com.amit.demo.saga.sagatransaction;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Getter
@Setter
public class TransactionRequest {
        private long consumerKey;
        private long emitraId;
        private BigDecimal amount;

}
