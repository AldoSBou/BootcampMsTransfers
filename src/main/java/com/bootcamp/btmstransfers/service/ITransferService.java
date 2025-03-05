package com.bootcamp.btmstransfers.service;

import com.bootcamp.btmstransfers.dto.OwnTransferRequestDTO;
import com.bootcamp.btmstransfers.dto.TransferResponseDTO;
import com.bootcamp.btmstransfers.model.Transfer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITransferService extends IGenericService<Transfer, String> {

    Mono<Transfer> deposit(Transfer transfer);
    Mono<TransferResponseDTO> transferLocal(Mono<OwnTransferRequestDTO> transferLocal);
    Flux<Transfer> findByAccountId(String accountId, String startDate, String endDate);
}
