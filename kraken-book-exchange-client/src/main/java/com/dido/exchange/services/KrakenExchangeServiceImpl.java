package com.dido.exchange.services;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KrakenExchangeServiceImpl implements KrakenExchangeService{

    private final ExchangeSubscriptionService exchangeSubscriptionService;

    private final String exchangeUrl = "wss://ws.kraken.com/";
    String subscriptionMsgBtc = "{ \"event\":\"subscribe\", \"subscription\":{\"name\":\"book\"},\"pair\":[\"XBT/USD\"] }";
    String subscriptionMsgEth = "{ \"event\":\"subscribe\", \"subscription\":{\"name\":\"book\"},\"pair\":[\"ETH/USD\"] }";


    public void startKrakenClient() {

        new Thread(() -> {
            exchangeSubscriptionService.
                    openAndStreamWebSocketSubscription(exchangeUrl, subscriptionMsgBtc, "BTC/USD");
        }).start();

        new Thread(() -> {
            exchangeSubscriptionService.
                    openAndStreamWebSocketSubscription(exchangeUrl, subscriptionMsgEth, "ETH/USD");
        }).start();
    }
}
