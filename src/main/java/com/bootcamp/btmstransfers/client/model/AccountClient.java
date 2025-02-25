package com.bootcamp.btmstransfers.client.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountClient {

    private String id;
    private String productType;
    private String customerId;
    private String accountNumber;
    private String accountStatus;
    private BigDecimal accountBalance;

}
