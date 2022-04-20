package com.dido.exchange;

import com.dido.exchange.services.ExchangeSubscriptionService;
import com.dido.exchange.services.ExchangeSubscriptionServiceImpl;
import com.dido.exchange.services.KrakenExchangeService;
import com.dido.exchange.services.KrakenExchangeServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppMain {

    public static void main(String[] args) {
        ExchangeSubscriptionService exchangeSubscriptionService = new ExchangeSubscriptionServiceImpl();
        KrakenExchangeService krakenExchangeService =
                new KrakenExchangeServiceImpl(exchangeSubscriptionService);

        krakenExchangeService.startKrakenClient();
    }
}
