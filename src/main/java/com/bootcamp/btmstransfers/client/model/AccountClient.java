package com.bootcamp.btmstransfers.client.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountClient {

    private String id;
    private String productId;
    private String customerId;
    private String accountNumber;
    private String accountStatus;
    private Double accountBalance;
    private Integer monthlyMovements;
    private String accountCreationDate;

}
