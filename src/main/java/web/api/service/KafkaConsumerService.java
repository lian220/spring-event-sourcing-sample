package web.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "my1-topic", groupId = "foo")
    public void consume(String message) throws IOException {
        log.info("Consumed message : {}", message);
    }

}
