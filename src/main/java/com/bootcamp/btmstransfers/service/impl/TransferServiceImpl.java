package com.bootcamp.btmstransfers.service.impl;

import com.bootcamp.btmstransfers.client.model.AccountClient;
import com.bootcamp.btmstransfers.client.model.AccountUpdateBalanceClient;
import com.bootcamp.btmstransfers.client.model.PassiveProductClient;
import com.bootcamp.btmstransfers.dto.OwnTransferRequestDTO;
import com.bootcamp.btmstransfers.dto.TransferResponseDTO;
import com.bootcamp.btmstransfers.infrastructure.iwebapi.IAccountApi;
import com.bootcamp.btmstransfers.model.Transfer;
import com.bootcamp.btmstransfers.repository.IGenericRepository;
import com.bootcamp.btmstransfers.repository.ITransferRepository;
import com.bootcamp.btmstransfers.service.ITransferService;
import com.bootcamp.btmstransfers.utils.IMemoryService;
import com.bootcamp.btmstransfers.utils.ServiceServiceDiscoveryUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferServiceImpl extends GenericServiceImpl<Transfer, String> implements ITransferService {

    private final ITransferRepository transferRepository;
    private final IAccountApi accountApi;
    private final ServiceServiceDiscoveryUtils serviceDiscoveryUtils;
    private final IMemoryService memoryService;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;


    @Override
    protected IGenericRepository<Transfer, String> getRepository() {
        return transferRepository;
    }

    private Mono<List<Transfer>> getMonthlyMovements(AccountClient account) {
        return transferRepository.findByAccountId(account.getId())
                .collectList()
                .onErrorResume(e -> {
                    System.err.println("Error al obtener los movimientos mensuales: " + e.getMessage());
                    return Mono.just(List.of());
                });
    }

    private Mono<AccountClient> getAccountInfoWithMonthlyMovements(String accountsInstances, String accountId) {
        // Adaptar la llamada a accountApi para usar el String accountsInstances
        return accountApi.getAccountInformation(accountsInstances, accountId)
                .flatMap(account -> getMonthlyMovements(account)
                        .map(movements -> {
                            account.setMonthlyMovements(movements.size());
                            return account;
                        })
                )
                .onErrorResume(e -> {
                    System.err.println("Error al obtener la información de la cuenta o los movimientos: " + e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Transfer> deposit(Transfer transfer) {
        return processAccountDeposit(transfer, serviceDiscoveryUtils.getDiscoveryInstances("bt-ms-accounts"));
    }

    @Override
    public Mono<TransferResponseDTO> transferLocal(Mono<OwnTransferRequestDTO> transferLocal) {
        return null;
    }

    @Override
    public Flux<Transfer> findByAccountId(String accountId, String startDate, String endDate) {

        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return transferRepository.findByAccountId(accountId);
        }
        return transferRepository.findByAccountId(accountId)
                .filter(transfer -> {
                    String movementDateStr = transfer.getMovementDate().substring(0, 10);
                    return movementDateStr.compareTo(startDate) >= 0 &&
                            movementDateStr.compareTo(endDate) <= 0;
                }).sort(Comparator.comparing(Transfer::getMovementDate));
    }


/*    @Override
    public Mono<TransferResponseDTO> transferLocal(Mono<OwnTransferRequestDTO> transferLocal) {
        return serviceDiscoveryUtils.getDiscoveryInstances("bt-ms-accounts")
                .flatMap(accountsInstances -> {
                    if (accountsInstances.isEmpty()) {
                        return Mono.error(new RuntimeException("No se encontraron instancias para bt-ms-accounts"));
                    }

                })
    }*/

    private Mono<Transfer> processAccountDeposit(Transfer transfer, Mono<String> accountsInstancesMono) {
        return accountsInstancesMono.flatMap(accountsInstances -> {
            if (accountsInstances == null || accountsInstances.isEmpty()) {
                return Mono.error(new RuntimeException("No se encontraron instancias para bt-ms-accounts"));
            }
            return getAccountInfoWithMonthlyMovements(accountsInstances, transfer.getAccountId())
                    .flatMap(account -> applyTransactionLimit(account, transfer))
                    .flatMap(trx -> updateAccountBalance(trx, accountsInstances))
                    .flatMap(this::saveTransfer);
        }).onErrorResume(e -> {
            System.err.println("Error en el depósito: " + e.getMessage());
            return Mono.error(e);
        });
    }

    private Mono<Transfer> applyTransactionLimit(AccountClient account, Transfer transfer) {
        return memoryService.getValue("allProducts", new TypeReference<List<PassiveProductClient>>() {
                })
                .flatMapIterable(e -> e)
                .filter(product -> product.getId().equals(account.getProductId()))
                .next()
                .map(product -> {
                    if (account.getMonthlyMovements() > product.getMonthlyTransactionLimit()) {
                        transfer.setCommission(transfer.getAmount() * 0.10);
                        transfer.setAccountBalance(account.getAccountBalance());
                        transfer.setAccountingBalance(account.getAccountBalance() + transfer.getAmount() - transfer.getCommission());
                    }
                    return transfer;
                })
                .defaultIfEmpty(transfer)
                .onErrorResume(e -> {
                    System.err.println("Error al aplicar el límite de transacciones: " + e.getMessage());
                    return Mono.just(transfer);
                });
    }

    private Mono<Transfer> updateAccountBalance(Transfer transfer, String accountsInstances) {
        AccountUpdateBalanceClient updateBalanceClient = new AccountUpdateBalanceClient();
        updateBalanceClient.setBalance(transfer.getAmount() - transfer.getCommission());
        updateBalanceClient.setCustomerId(transfer.getCustomerId());

        return accountApi.updateAccountBalance(accountsInstances, transfer.getAccountId(), updateBalanceClient)
                .thenReturn(transfer)
                .onErrorResume(e -> {
                    System.err.println("Error al actualizar el saldo de la cuenta: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    private Mono<Transfer> saveTransfer(Transfer transfer) {
        return transferRepository.save(transfer)
                .onErrorResume(e -> {
                    System.err.println("Error al guardar la transferencia: " + e.getMessage());
                    return Mono.error(e);
                });
    }

}
