package com.learning.activemq_camel_producer;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class MessageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Network network = Network.newNetwork();

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> activeMQContainer = new GenericContainer<>(
            DockerImageName.parse("rmohr/activemq:latest"))
            .withNetwork(network)
            .withExposedPorts(61616);

    @BeforeEach
    void setUp() {
        // Configure the ActiveMQ URL for the test
        String activeMQUrl = "tcp://" + activeMQContainer.getHost() + ":" + activeMQContainer.getMappedPort(61616);
        System.setProperty("spring.activemq.broker-url", activeMQUrl);
        // String prop = System.getProperty("spring.activemq.broker-url");
    }

    @Test
    void testSendMessage() throws Exception {
        // Given
        String message = "HelloActiveMQ";

        // When & Then
        mockMvc.perform(get("/send")
                .param("message", message))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Message sent: " + message)));
    }
}
