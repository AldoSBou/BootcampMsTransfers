package com.bootcamp.btmstransfers.infrastructure.iwebapi;

import com.bootcamp.btmstransfers.client.model.AccountClient;
import com.bootcamp.btmstransfers.client.model.AccountUpdateBalanceClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface IAccountApi {
    Mono<Void> updateAccountBalance(String accountInstance,String accountId, AccountUpdateBalanceClient accountUpdateBalance);
    Mono<AccountClient> getAccountInformation(String accountInstance, String accountId);
}
