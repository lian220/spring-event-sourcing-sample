package web.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private static final String TOPIC = "my1-topic";

    @Autowired
    private final KafkaTemplate<String, String> kafKaTemplate;

    public void sendMessage(String message) {
        log.info("Produce message: {}", message);
        this.kafKaTemplate.send(TOPIC, message);
    }
}
