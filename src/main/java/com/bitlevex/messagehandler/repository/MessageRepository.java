package com.bitlevex.messagehandler.repository;

import com.bitlevex.messagehandler.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
