package vn.distrib.chat.config;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitConfig {

  @Value("${app.amqp.exchange}")
  private String exchangeName;

  @Bean
  public TopicExchange chatExchange() {
    return new TopicExchange(exchangeName, true, false);
  }

  @Bean
  public AnonymousQueue nodeQueue() {
    return new AnonymousQueue();
  }

  @Bean
  public Binding binding(AnonymousQueue nodeQueue, TopicExchange chatExchange) {
    return BindingBuilder.bind(nodeQueue).to(chatExchange).with("room.*");
  }
}
