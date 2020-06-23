package com.bitlevex.messagehandler.controller;

import com.bitlevex.messagehandler.model.Message;
import com.bitlevex.messagehandler.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@RestController
@RequestMapping(value = BaseController.REST_URL)
@Slf4j
public class BaseController {
    public static final String REST_URL = "/api/";

    private final MessageRepository messageRepository;
    private final HttpServletRequest request;
    private final RestTemplate restTemplate;

    private final String endpointUrl;
    private final List<String> switchUrls;
    private final List<String> userUrls;

    @Autowired
    public BaseController(@Value("${endpoint.url}") String endpointUrl,
                          @Value("${switch.urls}") String switchUrls,
                          @Value("${user.urls}") String userUrls,
                          MessageRepository messageRepository,
                          HttpServletRequest request) {
        this.endpointUrl = endpointUrl;
        this.messageRepository = messageRepository;
        this.request = request;
        this.switchUrls = Arrays.asList(switchUrls.split(";"));
        this.userUrls = Arrays.asList(userUrls.split(";"));
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(2))
                .build();
    }

    private static ResponseEntity<String> getResponseEntity(List<Message> messages) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        for (Message message : messages) {
            sj.add(message.toString());
        }
        return new ResponseEntity<>(sj.toString(), HttpStatus.OK);
    }

    private static String httpServletRequestToString(HttpServletRequest request) {
        try {
            ServletInputStream mServletInputStream = request.getInputStream();
            byte[] httpInData = new byte[request.getContentLength()];
            int retVal = -1;
            StringBuilder stringBuilder = new StringBuilder();

            while ((retVal = mServletInputStream.read(httpInData)) != -1) {
                for (int i = 0; i < retVal; i++) {
                    stringBuilder.append((char) httpInData[i]);
                }
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            // EMPTY BODY EXCEPTION: 20.06.2020
        }
        return "exception";
    }

    private static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-Real-Ip");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getHeader("X-Forwarded-For");
                if (remoteAddr == null || "".equals(remoteAddr)) {
                    remoteAddr = request.getRemoteAddr();
                }
            }
        }
        if (remoteAddr != null) {
            if (remoteAddr.contains(",")) {
                remoteAddr = remoteAddr.substring(0, remoteAddr.indexOf(","));
            }
        }
        return StringUtils.truncate(remoteAddr, 32);
    }

    private static String getStringParameter(String key, String[] values) {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(key).add(":");
        for (String value : values) {
            sj.add(value);
        }
        return sj.toString();
    }

    @PostMapping(path = "message-handle")
    public void handleMsg() {
        String clientIp = getClientIp(request);
        String query = request.getQueryString();
        if (query.contains("event_type")) {
//        if (switchUrls.contains(clientIp) || userUrls.contains(clientIp)) {
            messageRepository.save(new Message(query, clientIp));
        }
    }

    @GetMapping(path = "messages")
    public ResponseEntity<String> getMsg() {
        String clientIp = getClientIp(request);
        if (userUrls.contains(clientIp)) {

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}