package com.learning.activemq_camel_producer.producer;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    @Autowired
    private ProducerTemplate producerTemplate;

    public void sendMessage(String message) {
        producerTemplate.sendBody("direct:start", message);
    }

}
