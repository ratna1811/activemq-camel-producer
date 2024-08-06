package com.learning.activemq_camel_producer.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DecisionMakingRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // from("timer:foo?period=10000")
        // .id("DecisionMaking")
        // .log("starting the route")
        // .setHeader("dummy").constant(1)
        // .choice()
        // .when(header("dummy").isEqualTo(1))
        // .log("Inside First Condition!!")
        // .to("direct:test1")
        // .when(header("dummy").isEqualTo(2))
        // .log("inside second condition")
        // .otherwise();
        // .log("inside otherwise")
        // // .endChoice()
        // .end()
        // .to("direct:test1")
        // .log("end of the route!!!");
        from("timer:foo?period=10000")
                .id("DecisionMakingRpute")
                .setHeader("parent").constant(1)

                .choice()
                .when(header("parent").isEqualTo(1))
                .setHeader("dummy").constant(1)
                .choice()
                .when(header("dummy").isEqualTo(1))
                .log("First Condition")
                .to("direct:test1").endChoice()
                .when(header("dummy").isEqualTo(2))
                .log("Second Condition")
                .to("direct:test2").endChoice()
                .otherwise()
                .log("Third Condition").endChoice()
                .when(header("parent").isEqualTo(2))
                .setHeader("dummy2").constant(1)
                .choice()
                .when(header("dummy2").isEqualTo(1))
                .log("inside dummy2 under parent 2")
                .endChoice()
                .otherwise()
                .log("inside parent 2 otherwise")
                .endChoice()
                .otherwise()
                .log("inside unknown parent")
                // // .endChoice()
                // // you should close .choice() block with .end() if there are any further
                // logic
                // // in the same route
                .end()
                .to("direct:testfinal")
                .log("directing to final route");

        from("direct:test1")
                .id("AnotherRouteFromFirstCondition")
                .log("AnotherRoute");

        from("direct:test2")
                .id("secondCondition")
                .log("from Second Condition");

        from("direct:testfinal")
                .id("finalRoute")
                .log("Redirecting to final block");

    }

}
