package com.dido.exchange.services;

import com.dido.exchange.model.OrderBook;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface BookExtractionService {

    String BOOK_NAME_ASK = "as";
    String BOOK_NAME_BID = "bs";
    String BOOK_NAME_ASK_UPD = "a";
    String BOOK_NAME_BID_UPD = "b";

    List<OrderBook> extractOrderBook(JsonNode jsonNode, String bookName);
    void outputOrderBooks(String pairType);

    List<OrderBook> sortBooks(List<OrderBook> books);
}
