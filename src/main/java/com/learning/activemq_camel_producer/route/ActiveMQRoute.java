package com.learning.activemq_camel_producer.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'configure'");
        onException(Exception.class)
                .log("Exception in route: ${exception.message}")
                .handled(true);
        from("direct:start")
                .to("activemq:queue:myQueue")
                .to("mock:myQueue");
    }

}
