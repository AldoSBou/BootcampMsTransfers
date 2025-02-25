package com.bootcamp.btmstransfers.utils;

import reactor.core.publisher.Mono;

public interface IServiceDiscoveryUtils {
    Mono<String> getDiscoveryInstances(String instace);
}
