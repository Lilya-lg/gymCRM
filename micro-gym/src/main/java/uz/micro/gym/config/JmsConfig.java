package uz.micro.gym.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class JmsConfig {

  @Bean
  public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());

    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    converter.setObjectMapper(mapper);
    return converter;
  }

  @Bean
  public JmsTemplate jmsTemplate(
      ConnectionFactory connectionFactory, MappingJackson2MessageConverter messageConverter) {
    JmsTemplate template = new JmsTemplate();
    template.setConnectionFactory(connectionFactory);
    template.setMessageConverter(messageConverter);
    return template;
  }

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
      ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(messageConverter);
    return factory;
  }
}
