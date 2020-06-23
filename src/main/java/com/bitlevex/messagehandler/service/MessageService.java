package com.bitlevex.messagehandler.service;

import com.bitlevex.messagehandler.dto.MessageDto;
import com.bitlevex.messagehandler.model.Message;
import com.bitlevex.messagehandler.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<MessageDto> findAll() {
        return messageRepository.findAll()
                .stream()
                    .sorted(Comparator.comparing(Message::getId).reversed())
                    .map(MessageDto::convertMessage)
                    .collect(Collectors.toList());
    }

}
