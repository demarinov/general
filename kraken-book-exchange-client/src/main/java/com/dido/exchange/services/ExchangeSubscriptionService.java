package com.dido.exchange.services;

public interface ExchangeSubscriptionService {

    void openAndStreamWebSocketSubscription(String connectionURL, String webSocketSubscription, String pairType);

}
