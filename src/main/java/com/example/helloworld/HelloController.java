package com.example.helloworld;

import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {
  private final WebClient webClient;

  @Inject
  public HelloController (WebClient.Builder builder
      , @Value("${server.port}") int port) {
    this(builder.baseUrl("http://127.0.0.1:" + port).build());
  }

  public HelloController(WebClient webClient) {
    this.webClient = webClient;
  }

  public static void main(String[] args) {
    SpringApplication.run(HelloWorldApplication.class, args);
  }

  @GetMapping("/greet")
  public String greet() {
    return "Hello";
  }

  @GetMapping("/hello")
  public Mono<String> sayHello() {
    return webClient
        .get()
        .uri("/greet")
        .retrieve()
        .bodyToMono(String.class);
  }
}
