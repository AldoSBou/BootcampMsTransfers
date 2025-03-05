package com.bootcamp.btmstransfers.infrastructure.webapi;

import com.bootcamp.btmstransfers.client.model.AccountClient;
import com.bootcamp.btmstransfers.client.model.AccountUpdateBalanceClient;
import com.bootcamp.btmstransfers.infrastructure.iwebapi.IAccountApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountApi implements IAccountApi {

    private final @Qualifier("accountsWebClient") WebClient.Builder clientBuilder;

    @Override
    public Mono<Void> updateAccountBalance(String accountInstance, String accountId, AccountUpdateBalanceClient accountUpdateBalance) {
        return clientBuilder.build().post()
                .uri(accountInstance + "/api/accounts/{accountId}/balance", accountId)
                .bodyValue(accountUpdateBalance)
                .header("Transaction-Type", "DEPOSIT")
                .retrieve()
                .toBodilessEntity() // Consume el body y retorna Mono<Void> en caso de éxito
                .then()
                .onErrorMap(WebClientResponseException.class, ex -> {
                    log.error("Error calling bt-ms-accounts to update balance: status={}, response={}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
                    return new RuntimeException("Error updating account balance in accounts service.");
                });
    }

    public Mono<AccountClient> getAccountInformation(String accountInstance, String accountId){
        return clientBuilder.build().get()
                .uri(accountInstance + "/api/accounts/{accountId}", accountId)
                .retrieve()
                .bodyToMono(AccountClient.class)
                .log();
    }
}
