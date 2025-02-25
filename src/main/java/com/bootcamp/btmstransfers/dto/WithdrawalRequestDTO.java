package com.bootcamp.btmstransfers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class WithdrawalRequestDTO {

    @NotBlank(message = "El accountId es obligatorio")
    private String accountId;

    @NotBlank(message = "El customerId es obligatorio") // Aunque podría venir del token, es bueno validarlo
    private String customerId;

    @NotNull(message = "El amount es obligatorio")
    @Positive(message = "El amount debe ser positivo")
    private BigDecimal amount;

    private String description; // Descripción opcional del retiro
    private String channel;     // Canal del retiro (opcional)


}
