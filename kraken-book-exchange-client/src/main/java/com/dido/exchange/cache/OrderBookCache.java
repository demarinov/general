package com.dido.exchange.cache;

import com.dido.exchange.model.OrderBook;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrderBookCache {

    public static Map<String, Map<String, List<OrderBook>>> orderBookPairMap = new TreeMap<>();

    static {
        orderBookPairMap.put("BTC/USD", new TreeMap<>());
        orderBookPairMap.put("ETH/USD", new TreeMap<>());
    }
}
