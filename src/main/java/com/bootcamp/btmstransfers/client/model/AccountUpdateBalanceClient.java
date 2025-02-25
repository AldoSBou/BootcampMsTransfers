package com.bootcamp.btmstransfers.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateBalanceClient {

    private String customerId;
    private BigDecimal balance;
}