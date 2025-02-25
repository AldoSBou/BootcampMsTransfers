package com.bootcamp.btmstransfers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequestDTO {

    @NotBlank(message = "El accountId es obligatorio")
    private String accountId;

    @NotBlank(message = "El customerId es obligatorio")
    // Aunque podría venir del token de seguridad, es bueno validarlo
    private String customerId;

    @NotNull(message = "El amount es obligatorio")
    @Positive(message = "El amount debe ser positivo")
    private BigDecimal amount;

    private String description; // Descripción opcional del depósito
    private String channel;     // Canal del depósito (opcional)
}
