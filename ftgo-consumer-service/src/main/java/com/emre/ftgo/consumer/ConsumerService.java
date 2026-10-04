package com.emre.ftgo.consumer;

import com.emre.ftgo.common.outbox.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsumerService {
    private final ConsumerRepository consumerRepository;
    private final OutboxService outboxService;

    public ConsumerService(ConsumerRepository consumerRepository, OutboxService outboxService) {
        this.consumerRepository = consumerRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public Consumer createConsumer(String firstName, String lastName) {
        Consumer consumer = consumerRepository.save(new Consumer(firstName, lastName));

        outboxService.publish(
                "consumer-events",
                "Consumer",
                String.valueOf(consumer.getId()),
                "ConsumerCreated",
                new ConsumerCreateEvent(consumer.getId(), consumer.getFirstName(), consumer.getLastName())
        );

        return consumer;
    }


}
