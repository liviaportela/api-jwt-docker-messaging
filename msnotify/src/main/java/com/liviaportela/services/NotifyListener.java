package com.liviaportela.services;

import com.liviaportela.entities.Message;
import com.liviaportela.repositories.NotifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifyListener {

    private final NotifyRepository repository;

    @KafkaListener(topics = "notify", groupId = "notify-group")
    public void process(String message) {
        try {
            Message newMessage = new Message(message);
            repository.save(newMessage);
            System.out.println("Received message: " + message);
        } catch (Exception e) {
            System.out.println("Failed to save message: " + e.getMessage());
        }
    }
}
