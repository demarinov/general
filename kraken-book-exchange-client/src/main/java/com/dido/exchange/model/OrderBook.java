package com.dido.exchange.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderBook {

    private String price;
    private String volume;
    private String timestamp;
}