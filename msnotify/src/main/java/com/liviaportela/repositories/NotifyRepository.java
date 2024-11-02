package com.liviaportela.repositories;

import com.liviaportela.entities.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotifyRepository extends MongoRepository<Message, String> {
}
