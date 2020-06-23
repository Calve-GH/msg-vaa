package com.bitlevex.messagehandler.model;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date = LocalDateTime.now();
    private String msg;
    private String ip;

    public Message(String msg, String ip) {
        this.msg = msg;
        this.ip = ip;
    }
}
