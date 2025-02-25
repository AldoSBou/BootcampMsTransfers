package com.bootcamp.btmstransfers.controller;

import com.bootcamp.btmstransfers.dto.DepositRequestDTO;
import com.bootcamp.btmstransfers.model.Transfer;
import com.bootcamp.btmstransfers.service.ITransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/transfers")
public class TransferController {

    private final ITransferService transferService;

    @PostMapping("/deposit")
    public Mono<ResponseEntity<Transfer>> deposit(@RequestBody DepositRequestDTO depositInfo) {
        Transfer transfer = new Transfer();
        transfer.setAccountId(depositInfo.getAccountId());
        transfer.setAmount(depositInfo.getAmount());
        transfer.setCustomerId(depositInfo.getCustomerId());
        transfer.setMovementType("DEPOSIT");
        transfer.setChannel("WEB APP");
        transfer.setMovementDate(LocalDateTime.now());
        return transferService.deposit(transfer)
                .map(e -> {
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(e);
                });
    }

    @PostMapping("/withdraw")
    public Mono<ResponseEntity<Transfer>> withDraw(@RequestBody DepositRequestDTO depositInfo) {
        Transfer transfer = new Transfer();
        transfer.setAccountId(depositInfo.getAccountId());
        transfer.setAmount(depositInfo.getAmount().negate());
        transfer.setCustomerId(depositInfo.getCustomerId());
        transfer.setMovementType("WITHDRAW");
        transfer.setChannel("WEB APP");
        transfer.setMovementDate(LocalDateTime.now());
        return transferService.deposit(transfer)
                .map(e -> {
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(e);
                });
    }

    @GetMapping("/account/{id}/movements")
    public Mono<ResponseEntity<Flux<Transfer>>> getAllTransfersByAccountId(@PathVariable("id") String accountId) {

        Flux<Transfer> transfers = transferService.findByAccountId(accountId);
        return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(transfers))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
