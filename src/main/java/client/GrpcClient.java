package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

import org.baeldung.grpc.HelloRequest;
import org.baeldung.grpc.HelloResponse;
import org.baeldung.grpc.HelloServiceGrpc;
import org.baeldung.grpc.VideoRequest;
import org.baeldung.grpc.VideoResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("192.168.0.18", 9001)
          .usePlaintext()
          .build();
 
        HelloServiceGrpc.HelloServiceBlockingStub stub 
          = HelloServiceGrpc.newBlockingStub(channel);
        
        System.out.println("Initiating request.....");
 
        HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
          .setFirstName("Baeldung")
          .setLastName("gRPC")
          //.setValue1("newVal")
          .build());
        
        CountDownLatch latch = new CountDownLatch(1);
        
        HelloServiceGrpc.HelloServiceStub streamingStub= HelloServiceGrpc.newStub(channel);
        File receivedFile = new File("./receivedfile");
        System.out.println(receivedFile.getAbsolutePath());
		OutputStream os = new FileOutputStream(receivedFile);
        streamingStub.video(VideoRequest.newBuilder().setVideoName("Give me video...").build(), 
        		new StreamObserver<VideoResponse>() {

					@Override
					public void onNext(VideoResponse value) {
						try {
							//System.out.println("Received batch");
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							os.flush();
							os.write(value.getVideoBytes().toByteArray());
						} catch (IOException e) {
							onError(e);
						}
						
					}

					@Override
					public void onError(Throwable t) {
						System.out.println(t);
						channel.shutdown();
						latch.countDown();
					}

					@Override
					public void onCompleted() {
						try {
							os.flush();
							os.close();
							channel.shutdown();
							latch.countDown();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							onError(e);
						}
						
						System.out.println("Completed....!!!!");
						
					}
				});
        
        
        System.out.println(helloResponse.getGreeting());
        latch.await();
    }
}