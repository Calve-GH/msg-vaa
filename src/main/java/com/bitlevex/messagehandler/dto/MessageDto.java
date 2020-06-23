package com.bitlevex.messagehandler.dto;

import com.bitlevex.messagehandler.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private static final String DATE_FORMATTER = "dd-MM-yyyy HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
    private Long id;
    private String date;
    private String message;
    private String ip;

    public static MessageDto convertMessage(Message message) {
        return new MessageDto(message.getId(), message.getDate().format(formatter), message.getMsg(), message.getIp());
    }
}
