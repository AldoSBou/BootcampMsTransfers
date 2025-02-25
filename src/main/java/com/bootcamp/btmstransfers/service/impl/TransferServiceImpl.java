package com.bootcamp.btmstransfers.service.impl;

import com.bootcamp.btmstransfers.client.model.AccountUpdateBalanceClient;
import com.bootcamp.btmstransfers.infrastructure.iwebapi.IAccountApi;
import com.bootcamp.btmstransfers.model.Transfer;
import com.bootcamp.btmstransfers.repository.IGenericRepository;
import com.bootcamp.btmstransfers.repository.ITransferRepository;
import com.bootcamp.btmstransfers.service.ITransferService;
import com.bootcamp.btmstransfers.utils.ServiceServiceDiscoveryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferServiceImpl extends GenericServiceImpl<Transfer, String> implements ITransferService {

    private final ITransferRepository transferRepository;
    private final IAccountApi accountApi;
    private final ServiceServiceDiscoveryUtils serviceDiscoveryUtils;

    @Override
    protected IGenericRepository<Transfer, String> getRepository() {
        return transferRepository;
    }

    @Override
    public Mono<Transfer> deposit(Transfer transfer) {
        return serviceDiscoveryUtils.getDiscoveryInstances("bt-ms-accounts")
                .flatMap(accountsInstances -> {
                    if (accountsInstances.isEmpty()) {
                        return Mono.error(new RuntimeException("No se encontraron instancias para bt-ms-accounts"));
                    }
                    AccountUpdateBalanceClient updateBalanceClient = new AccountUpdateBalanceClient();
                    updateBalanceClient.setBalance(transfer.getAmount());
                    updateBalanceClient.setCustomerId(transfer.getCustomerId());
                    return accountApi.updateAccountBalance(accountsInstances, transfer.getAccountId(), updateBalanceClient);
                }).then(Mono.defer(() -> transferRepository.save(transfer)));
    }

    @Override
    public Flux<Transfer> findByAccountId(String accountId) {
        return transferRepository.findByAccountId(accountId);
    }
}
