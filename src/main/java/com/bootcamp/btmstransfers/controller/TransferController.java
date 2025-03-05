package com.bootcamp.btmstransfers.controller;

import com.bootcamp.btmstransfers.api.TransfersApiDelegate;
import com.bootcamp.btmstransfers.dto.DepositRequestDTO;
import com.bootcamp.btmstransfers.dto.OwnTransferRequestDTO;
import com.bootcamp.btmstransfers.dto.TransferResponseDTO;
import com.bootcamp.btmstransfers.model.Transfer;
import com.bootcamp.btmstransfers.service.ITransferService;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/transfers")
public class TransferController implements TransfersApiDelegate {

    private final ITransferService transferService;



    @GetMapping("/account/{id}/movements")
    public Mono<ResponseEntity<Flux<Transfer>>> getAllTransfersByAccountId(@PathVariable("id") String accountId,
                                                                           @QueryParam("starDate") String startDate,
                                                                           @QueryParam("endDate") String endDate) {

        Flux<Transfer> transfers = transferService.findByAccountId(accountId, startDate, endDate);
        return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(transfers))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<TransferResponseDTO>> transfersLocalPost(Mono<OwnTransferRequestDTO> ownTransferRequestDTO,
                                                                        ServerWebExchange exchange){
        return Mono.empty();
    }
}
