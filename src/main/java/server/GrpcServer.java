package server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.NettyServerBuilder;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
    	//Pass this as VM arg before running	
    	//-XX:MaxDirectMemorySize=4g
        Server server = ServerBuilder
          .forPort(9001)
          .addService(new HelloServiceImpl())
          .build();
 
        server.start();
        System.out.println("Server started");
        server.awaitTermination();
        System.out.println("Server stopped");
    }
}
