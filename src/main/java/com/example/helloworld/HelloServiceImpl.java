package com.example.helloworld;

import com.example.helloworld.HelloServiceGrpc.HelloServiceImplBase;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl extends HelloServiceImplBase {
  static String toMessage(String name) {
    return "Hello, " + name + '!';
  }

  private static Hello.HelloReply buildReply(Object message) {
    return Hello.HelloReply.newBuilder().setMessage(String.valueOf(message)).build();
  }

  @Override
  public void hello(Hello.HelloRequest request, StreamObserver<Hello.HelloReply> responseObserver) {
    responseObserver.onNext(buildReply(toMessage(request.getName())));
    responseObserver.onCompleted();
  }
}
