package com.bootcamp.btmstransfers.repository;

import com.bootcamp.btmstransfers.model.Transfer;
import reactor.core.publisher.Flux;

public interface ITransferRepository extends IGenericRepository<Transfer, String> {
    Flux<Transfer> findByAccountId(String accountId);
    Flux<Transfer> findByAccountIdAndMovementDateBetween(String accountId, String starDate, String endDate);
}
