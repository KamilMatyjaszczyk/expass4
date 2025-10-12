package dat250.services;

import dat250.messaging.VoteEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import static dat250.config.RabbitConfig.EXCHANGE_NAME;

@Service
public class VoteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public VoteEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishVoteEvent(Long pollId, Long optionId) {
        VoteEvent event = new VoteEvent(pollId, optionId);
        String routingKey = "poll." + pollId;
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, event);
        System.out.println(" [x] Sent VoteEvent: " + event);
    }
}