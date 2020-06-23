package com.bitlevex.messagehandler.controller;

import com.bitlevex.messagehandler.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@RequiredArgsConstructor
public class BaseExceptionHandler {

    private final MessageRepository messageRepository;

    @ExceptionHandler(Exception.class)
    public void handleError(Exception e) {
//        messageRepository.save(new Message(e.getClass().getSimpleName() + " " + e.getLocalizedMessage()));
    }
}