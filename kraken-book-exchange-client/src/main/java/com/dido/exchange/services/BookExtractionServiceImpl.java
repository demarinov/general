package com.dido.exchange.services;


import com.dido.exchange.cache.OrderBookCache;
import com.dido.exchange.configuration.MapperConfiguration;
import com.dido.exchange.model.OrderBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookExtractionServiceImpl implements BookExtractionService {

    private static ObjectMapper mapper = MapperConfiguration.getObjectMapper();
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public List<OrderBook> sortBooks(List<OrderBook> books) {
        return books.stream().sorted((bookOne,
                                      bookTwo) -> Double.valueOf(bookTwo.getPrice()).
                        compareTo(Double.valueOf(bookOne.getPrice())))
                .collect(Collectors.toList());
    }

    public void outputOrderBooks(String pairType) {
        outputBook(pairType);
    }

    private void outputBook(String pairType) {

        Map<String, List<OrderBook>> orderBookMap = OrderBookCache.orderBookPairMap.get(pairType);
        System.out.println("<------------------------------------->");
        System.out.println("asks:");
        List<OrderBook> askBooks = orderBookMap.get(BOOK_NAME_ASK);
        String outputAsks = askBooks.stream().
                map(ask -> String.format("[ %s, %s ]",ask.getPrice(), ask.getVolume()))
                .collect(Collectors.joining(",\n  "));
        System.out.println("[ "+outputAsks+" ]");

        List<OrderBook> bidBooks = orderBookMap.get(BOOK_NAME_BID);

        bidBooks = bidBooks.stream().sorted((bookOne,
                                             bookTwo) -> Double.valueOf(bookTwo.getPrice()).
                        compareTo(Double.valueOf(bookOne.getPrice())))
                .collect(Collectors.toList());

        System.out.println(String.format("best bid: [ %s, %s ]",
                bidBooks.get(0).getPrice(), bidBooks.get(0).getVolume()));
        System.out.println(String.format("best ask: [ %s, %s ]",
                askBooks.get(askBooks.size()-1).getPrice(), askBooks.get(askBooks.size()-1)
                        .getVolume()));

        System.out.println("bids:");
        String outputBids = bidBooks.stream().
                map(bid -> String.format("[ %s, %s ]",bid.getPrice(), bid.getVolume()))
                .collect(Collectors.joining(",\n  "));
        System.out.println("[ "+outputBids+" ]");


        System.out.println(LocalDateTime.now().atZone(ZoneOffset.UTC).format(dateFormatter));
        System.out.println(pairType);
        System.out.println(">-------------------------------------<");
    }


    public List<OrderBook> extractOrderBook(JsonNode jsonNode, String bookName) {

        List<OrderBook> orderBooks = new ArrayList<>();
        TypeReference<List<Object>> typeRef = new TypeReference<List<Object>>() {};
        try {
            extractBooks(jsonNode, bookName, orderBooks, typeRef, mapper);
        } catch (JsonProcessingException | NullPointerException ex) {
//            System.out.println(ex.getMessage());
            return null;
        }

        return orderBooks;
    }

    private void extractBooks(JsonNode jsonNode, String bookName, List<OrderBook> orderBooks, TypeReference<List<Object>> typeRef, ObjectMapper mapper) throws JsonProcessingException {
        List<Object> orderBooksExtracted = mapper.readValue(jsonNode.get(1).get(bookName).toString(), typeRef);
        for (int i = 0; i < orderBooksExtracted.size(); i++) {
            Object orderBookObj = orderBooksExtracted.get(i);
            OrderBook orderBook = new OrderBook();
            orderBook.setPrice(((List<String>) orderBookObj).get(0));
            orderBook.setVolume(((List<String>) orderBookObj).get(1));
            orderBook.setTimestamp(((List<String>) orderBookObj).get(2));

            orderBooks.add(orderBook);
        }
    }
}
