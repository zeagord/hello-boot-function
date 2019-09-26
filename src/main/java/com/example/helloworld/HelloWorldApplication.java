package com.example.helloworld;

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.docs.DocServiceBuilder;
import com.linecorp.armeria.server.grpc.GrpcServiceBuilder;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import com.linecorp.armeria.spring.actuate.ArmeriaSpringActuatorAutoConfiguration;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ImportAutoConfiguration(ArmeriaSpringActuatorAutoConfiguration.class)
public class HelloWorldApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(HelloWorldApplication.class, args);
  }

  @Bean
  public ArmeriaServerConfigurator armeriaServerConfigurator(HelloServiceImpl helloService) {
    return serverBuilder -> {
      serverBuilder.service(new GrpcServiceBuilder()
          .addService(helloService)
          .addService(ProtoReflectionService.newInstance())
          .supportedSerializationFormats(GrpcSerializationFormats.values())
          .enableUnframedRequests(true)
          .build()
      );
      serverBuilder.serviceUnder("/docs", new DocServiceBuilder()
          .exampleRequestForMethod(HelloServiceGrpc.SERVICE_NAME, "Hello",
              Hello.HelloRequest.newBuilder().setName("HelloWorld").build()).build()
      );
      serverBuilder.decorator(LoggingService.newDecorator());
      serverBuilder.accessLogWriter(AccessLogWriter.combined(), true);
    };
  }
}
