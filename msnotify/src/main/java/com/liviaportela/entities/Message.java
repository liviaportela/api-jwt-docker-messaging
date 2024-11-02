package com.liviaportela.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    private String message;

    public Message(String message) {
        this.message = message;
    }
}