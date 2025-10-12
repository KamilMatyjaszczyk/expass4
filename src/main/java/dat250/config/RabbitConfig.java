package dat250.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "poll.exchange";
    public static final String QUEUE_NAME = "poll.votes";
    public static final String ROUTING_KEY = "poll.#";

    @Bean
    public TopicExchange pollExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue pollQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding pollBinding(Queue pollQueue, TopicExchange pollExchange) {
        return BindingBuilder.bind(pollQueue).to(pollExchange).with(ROUTING_KEY);
    }
}
