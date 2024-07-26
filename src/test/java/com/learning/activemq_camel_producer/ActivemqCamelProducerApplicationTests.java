package com.learning.activemq_camel_producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.learning.activemq_camel_producer.producer.MessageProducer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ActivemqCamelProducerApplicationTests {

	@Autowired
	private MessageProducer messageProducer;

	@Autowired
	private ProducerTemplate producerTemplate;

	@Autowired
	private CamelContext camelContext;

	private static final Network network = Network.newNetwork();

	@SuppressWarnings("resource")
	@Container
	static GenericContainer<?> activeMQContainer = new GenericContainer<>(
			DockerImageName.parse("rmohr/activemq:latest"))
			.withNetwork(network)
			.withExposedPorts(61616)
			.waitingFor(Wait.forListeningPort());

	@BeforeAll
	static void startActiveMQ() {
		activeMQContainer.start();
		String activeMQUrl = "tcp://" + activeMQContainer.getHost() + ":" + activeMQContainer.getMappedPort(61616);
		System.setProperty("spring.activemq.broker-url", activeMQUrl);
	}

	@BeforeEach
	void setUp() {
		// Ensuring CamelContext is properly initialized
		assertNotNull(camelContext, "CamelContext should not be null");
		assertNotNull(producerTemplate, "ProducerTemplate should not be null");

		// Resetting and configuring the mock endpoint
		MockEndpoint mock = camelContext.getEndpoint("mock:myQueue", MockEndpoint.class);
		mock.reset();
	}

	@Test
	void testSendMessage() throws InterruptedException {
		// Given
		String message = "HelloActiveMQ";

		// Create a MockEndpoint for testing
		MockEndpoint mock = camelContext.getEndpoint("mock:myQueue", MockEndpoint.class);
		mock.expectedMessageCount(1);

		// When
		messageProducer.sendMessage(message);

		// Then
		mock.assertIsSatisfied();
		assertNotNull(producerTemplate);
		assertEquals(message, mock.getExchanges().get(0).getIn().getBody(String.class));
	}
}
