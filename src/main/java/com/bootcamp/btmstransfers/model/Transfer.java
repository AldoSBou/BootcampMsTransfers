package com.bootcamp.btmstransfers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transfers")
public class Transfer {

    @Id
    private String id;

    @Field("accountId") // ID de la cuenta involucrada (referencia a microservicio accounts)
    private String accountId;

    @Field("customerId") // ID del cliente (referencia a microservicio customers - opcional, puede venir de accounts)
    private String customerId;

    @Field("movementType") // Tipo de movimiento: "DEPOSIT", "WITHDRAWAL"
    private String movementType;

    @Field("amount")
    private Double amount;

    @Field("movementDate")
    private String movementDate;

    @Field("description") // Descripción del movimiento (opcional)
    private String description;

    @Field("channel") // Canal por el cual se realizó el movimiento (ej., "WEB", "ATM", "BRANCH") - opcional
    private String channel;

    @Field("commission")
    private Double commission;

    @Field("accountBalance")

    private Double accountBalance;

    @Field("accountingBalance")

    private Double accountingBalance;

}
