package com.example.helloworld;

import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.ClientFactoryBuilder;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.docs.DocServiceBuilder;
import com.linecorp.armeria.server.grpc.GrpcServiceBuilder;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import com.linecorp.armeria.spring.web.reactive.ArmeriaClientConfigurator;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
  final Hello.HelloRequest exampleRequest = Hello.HelloRequest.newBuilder().setName("Armeria").build();

  //@Bean
  //public ArmeriaServerConfigurator armeriaServerConfigurator(){
  //  return serverBuilder -> {
  //    serverBuilder.serviceUnder("/docs", new DocServiceBuilder()
  //        .exampleRequestForMethod(HelloServiceGrpc.SERVICE_NAME, "Hello", exampleRequest).build()
  //    );
  //    //serverBuilder.annotatedService("/greet", new )
  //    serverBuilder.decorator(LoggingService.newDecorator());
  //    serverBuilder.accessLogWriter(AccessLogWriter.combined(), false);
  //    serverBuilder.service(new GrpcServiceBuilder()
  //        .addService(new HelloServiceImpl())
  //        .addService(ProtoReflectionService.newInstance())
  //        .supportedSerializationFormats(GrpcSerializationFormats.values())
  //        .enableUnframedRequests(true)
  //        .build()
  //    );
  //  };
  //}

  @Bean
  public ClientFactory clientFactory() {
    return new ClientFactoryBuilder().sslContextCustomizer(
        b -> b.trustManager(InsecureTrustManagerFactory.INSTANCE)).build();
  }

  @Bean
  public ArmeriaClientConfigurator armeriaClientConfigurator(ClientFactory clientFactory) {
    return builder -> {
      builder.factory(clientFactory);
    };
  }

}
