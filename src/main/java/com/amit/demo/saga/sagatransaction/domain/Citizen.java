package com.amit.demo.saga.sagatransaction.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Citizen {

    private long emitraId;

    private String name;

    private double walletAmount;

    private Instant update_date;
}
