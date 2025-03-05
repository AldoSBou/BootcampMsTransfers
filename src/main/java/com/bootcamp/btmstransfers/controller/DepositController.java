package com.bootcamp.btmstransfers.controller;

import com.bootcamp.btmstransfers.dto.DepositRequestDTO;
import com.bootcamp.btmstransfers.dto.OwnTransferRequestDTO;
import com.bootcamp.btmstransfers.dto.TransferResponseDTO;
import com.bootcamp.btmstransfers.model.Transfer;
import com.bootcamp.btmstransfers.service.ITransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/transfers/deposit")
public class DepositController {

    private final ITransferService transferService;

    @PostMapping
    public Mono<ResponseEntity<Transfer>> deposit(@RequestBody DepositRequestDTO depositInfo) {
        Transfer transfer = new Transfer();
        transfer.setAccountId(depositInfo.getAccountId());
        transfer.setAmount(depositInfo.getAmount());
        transfer.setCustomerId(depositInfo.getCustomerId());
        transfer.setMovementType("DEPOSIT");
        transfer.setChannel("WEB APP");
        transfer.setMovementDate(LocalDateTime.now().toString());
        return transferService.deposit(transfer)
                .map(e -> {
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(e);
                });
    }
}
