package com.bitlevex.messagehandler.controller;

import com.bitlevex.messagehandler.dto.MessageDto;
import com.bitlevex.messagehandler.model.Message;
import com.bitlevex.messagehandler.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @GetMapping("get-msg")
    public List<MessageDto> getMessagesLimit5() {
        return messageRepository.findAll().stream()
                .limit(5)
                .map(MessageDto::convertMessage)
                .collect(Collectors.toList());
    }

    @PostMapping("new-msg")
    public void saveMessage(@RequestBody MessageDto message) {
        messageRepository.save(new Message(null, LocalDateTime.now(), message.getMessage(), "localhost"));
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
    public ResponseEntity<String> handleMsg() {
        String clientIp = getClientIp(request);
        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
//        if (query.contains("event_type")) {
//        if (switchUrls.contains(clientIp) || userUrls.contains(clientIp)) {
            messageRepository.save(new Message(query, clientIp));
        } else {
            System.out.println();//todo sout;
        }
        return new ResponseEntity<>("YES", HttpStatus.OK);
    }

    @GetMapping(path = "messages")
    public ResponseEntity<String> getMsg() {
        String clientIp = getClientIp(request);
//        if (userUrls.contains(clientIp)) {
        return new ResponseEntity<>("MSGS", HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/msg")
    public ResponseEntity<?> getParams(RequestEntity<String> request1) throws IOException {

        log.info(request1.getBody());
        log.info(request1.toString());
        log.info(request1.getUrl().toString());

        Map<String, String> stringStringMultiValueMap = parseFormData(request1);
        log.info("---------------");//todo sout;
        for (String s : stringStringMultiValueMap.keySet()) {
            log.info(s + " : " + stringStringMultiValueMap.get(s));//todo sout;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static InputStream getInputStream(String src) {
        try {
            return IOUtils.toInputStream(src, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getValue(String[] pair) {
        if (pair.length == 2) return pair[1];
        return null;
    }

    private final Pattern paramPattern = Pattern.compile("^((?:[0-9A-Za-z._~-]|%\\p{XDigit}{2})+)(?:=((?:[0-9A-Za-z._~+-]|%\\p{XDigit}{2})*))?$");

    private Map<String, String> parseFormData(HttpEntity<String> message) throws UnsupportedEncodingException {
        MediaType contentType = message.getHeaders().getContentType();
        Charset charset = (contentType != null && contentType.getCharset() != null ?
                contentType.getCharset() : StandardCharsets.UTF_8);
        String body = message.getBody();

        String[] pairs = org.springframework.util.StringUtils.tokenizeToStringArray(body, "&");
        HashMap<String, String> valueMap = new LinkedHashMap<>(pairs.length);
        for (String pair : pairs) {
            Matcher matcher = paramPattern.matcher(pair);
            if (!matcher.matches())
                throw new IllegalArgumentException("bad message format");

            String name = URLDecoder.decode(matcher.group(1), charset.name());
            System.out.println(matcher.group(2));//todo sout;
            String value = matcher.group(2) != null ? URLDecoder.decode(matcher.group(2), charset.name()) : null;
            valueMap.put(name, value);
        }
        return valueMap;
    }
}