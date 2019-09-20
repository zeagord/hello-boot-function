package com.example.helloworld;

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocServiceBuilder;
import com.linecorp.armeria.server.docs.DocServiceFilter;
import com.linecorp.armeria.server.grpc.GrpcServiceBuilder;
import com.linecorp.armeria.spring.actuate.ArmeriaSpringActuatorAutoConfiguration;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.reflection.v1alpha.ServerReflectionGrpc;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration(ArmeriaSpringActuatorAutoConfiguration.class)
public class HelloWorldApplication {

  private static Logger logger = LoggerFactory.getLogger(HelloWorldApplication.class);

  public static void main(String[] args) throws Exception {
    SpringApplication.run(HelloWorldApplication.class, args);
    final Server server = newServer(9000, 7443);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      server.stop().join();
      logger.info("Server has been stopped.");
    }));

    server.start().join();
    final InetSocketAddress localAddress = server.activePort().get().localAddress();
    final boolean isLocalAddress = localAddress.getAddress().isAnyLocalAddress() ||
        localAddress.getAddress().isLoopbackAddress();
    logger.info("Server has been started. Serving DocService at http://{}:{}/docs",
        isLocalAddress ? "127.0.0.1" : localAddress.getHostString(), localAddress.getPort());
  }
  //
  //@Bean
  //public Supplier<Flux<String>> greet() {
  //  return () -> Flux.just("Hello");
  //}

  static Server newServer(int httpPort, int httpsPort) throws Exception {
    final Hello.HelloRequest exampleRequest = Hello.HelloRequest.newBuilder().setName("Armeria").build();
    return new ServerBuilder()
        .http(httpPort)
        .https(httpsPort)
        .tlsSelfSigned()
        .service(new GrpcServiceBuilder()
            .addService(new HelloServiceImpl())
            // See https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md
            .addService(ProtoReflectionService.newInstance())
            .supportedSerializationFormats(GrpcSerializationFormats.values())
            .enableUnframedRequests(true)
            // You can set useBlockingTaskExecutor(true) in order to execute all gRPC
            // methods in the blockingTaskExecutor thread pool.
            // .useBlockingTaskExecutor(true)
            .build())
        // You can access the documentation service at http://127.0.0.1:8080/docs.
        // See https://line.github.io/armeria/server-docservice.html for more information.
        .serviceUnder("/docs", new DocServiceBuilder()
            .exampleRequestForMethod(HelloServiceGrpc.SERVICE_NAME,
                "Hello", exampleRequest)
            .exampleRequestForMethod(HelloServiceGrpc.SERVICE_NAME,
                "LazyHello", exampleRequest)
            .exampleRequestForMethod(HelloServiceGrpc.SERVICE_NAME,
                "BlockingHello", exampleRequest)
            .exclude(DocServiceFilter.ofServiceName(ServerReflectionGrpc.SERVICE_NAME))
            .build())
        .build();
  }

}
