package com.nettyserver.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

	private static final String CNX_POS_TO_UCOM_QUEUE = "CNX_POS_TO_UCOM_QUEUE";

	private static final String CNX_TO_POS_TOPIC = "CNX_TO_POS_TOPIC";

	@Autowired
	public ConnectionFactory rabbitConnectionFactory;

	// @Bean
	// public Queue csToUcomQueue() {
	// return new ActiveMQQueue("POS_TO_UCOM_QUEUE");
	// }

	@Bean
	public RabbitTemplate rabbitTemplate() {
		final RabbitTemplate template = new RabbitTemplate();
		template.setConnectionFactory(this.rabbitConnectionFactory);
		template.setMessageConverter(this.jsonMessageConverter());
		return template;
	}

	@Bean
	public JsonMessageConverter jsonMessageConverter() {
		return new JsonMessageConverter();
	}

	@Bean
	public Queue queue() {
		return new Queue(CNX_POS_TO_UCOM_QUEUE, false);
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange("cnx-pos-to-ucom-exchange");
	}

	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange("cnx-to-pos-exchange");
	}

	@Bean
	public Queue topic() {
		return new Queue(CNX_TO_POS_TOPIC, false);
	}

	@Bean
	public Binding topicBinding(final TopicExchange exchange) {
		return BindingBuilder.bind(this.topic()).to(exchange).with("cnxSocketCnxToPosBinding");
	}

	@Bean
	public Binding queueBinding(final Queue queue, final DirectExchange exchange) {
		return BindingBuilder.bind(this.queue()).to(exchange).with("cnxSocketPosToUcomBinding");
	}

	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(final ConnectionFactory rabbitConnectionFactory) {
		final SimpleMessageListenerContainer result = new SimpleMessageListenerContainer(rabbitConnectionFactory);
		result.addQueueNames(CNX_TO_POS_TOPIC);
		result.setMaxConcurrentConsumers(10);
		return result;
	}

}
