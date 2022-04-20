package com.dido.exchange.services;

import com.dido.exchange.cache.OrderBookCache;
import com.dido.exchange.configuration.MapperConfiguration;
import com.dido.exchange.model.OrderBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

import static com.dido.exchange.services.BookExtractionService.*;

@Slf4j
public class ExchangeSubscriptionServiceImpl implements ExchangeSubscriptionService {

    private static BookExtractionService bookExtractionService = new BookExtractionServiceImpl();

    private static ObjectMapper mapper = MapperConfiguration.getObjectMapper();

    public void openAndStreamWebSocketSubscription(String connectionURL, String webSocketSubscription, String pairType) {

        try {

            CountDownLatch latch = new CountDownLatch(1);

            WebSocket ws = HttpClient

                    .newHttpClient()

                    .newWebSocketBuilder()

                    .buildAsync(URI.create(connectionURL), new WebSocketClient(latch, pairType))

                    .join();

            ws.sendText(webSocketSubscription, true);

            latch.await();

        } catch (Exception e) {

            log.error(e.getMessage());

        }

    }

    private static class WebSocketClient implements WebSocket.Listener {

        private final CountDownLatch latch;
        private final String pairType;

        public WebSocketClient(CountDownLatch latch, String pairType) {

            this.latch = latch;
            this.pairType = pairType;
        }

        @Override

        public void onOpen(WebSocket webSocket) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime now = LocalDateTime.now();

            WebSocket.Listener.super.onOpen(webSocket);

        }

        @SneakyThrows
        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

            handleOrderBook(data);
            return WebSocket.Listener.super.onText(webSocket, data, false);

        }

        private void handleOrderBook(CharSequence data) throws JsonProcessingException {

            JsonNode jsonNode = mapper.readTree(data.toString());

            Map<String, List<OrderBook>> orderBookMap = OrderBookCache.orderBookPairMap.get(pairType);

            if (jsonNode.isArray()) {

                List<OrderBook> askBooks = bookExtractionService.
                        extractOrderBook(jsonNode, BOOK_NAME_ASK);
                if (askBooks != null) {
                    askBooks = bookExtractionService.
                            sortBooks(askBooks);
                    orderBookMap.putIfAbsent(BOOK_NAME_ASK, new ArrayList<>());
                    orderBookMap.get(BOOK_NAME_ASK).addAll(askBooks);

                    List<OrderBook> bidBooks = bookExtractionService.
                            extractOrderBook(jsonNode, BOOK_NAME_BID);
                    bidBooks = bookExtractionService.
                            sortBooks(bidBooks);
                    orderBookMap.putIfAbsent(BOOK_NAME_BID, new ArrayList<>());
                    orderBookMap.get(BOOK_NAME_BID).addAll(bidBooks);

                    bookExtractionService.outputOrderBooks(pairType);
                }

                updateBook(jsonNode, BOOK_NAME_ASK_UPD, BOOK_NAME_ASK);

                updateBook(jsonNode, BOOK_NAME_BID_UPD, BOOK_NAME_BID);

            }

        }

        private void updateBook(JsonNode jsonNode, String bookNameUpd, String bookName) {
            List<OrderBook> askBookUpd = bookExtractionService.extractOrderBook(jsonNode, bookNameUpd);
            Map<String, List<OrderBook>> orderBookMap = OrderBookCache.orderBookPairMap.get(pairType);
            if (askBookUpd != null) {
                askBookUpd = bookExtractionService.sortBooks(askBookUpd);
                orderBookMap.putIfAbsent(bookName, new ArrayList<>());
                orderBookMap.get(bookName).addAll(askBookUpd);

                bookExtractionService.outputOrderBooks(pairType);
            }
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {

            log.error(error.getMessage());

            WebSocket.Listener.super.onError(webSocket, error);

        }

    }
}
