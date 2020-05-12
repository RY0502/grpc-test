package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.baeldung.grpc.HelloRequest;
import org.baeldung.grpc.HelloResponse;
import org.baeldung.grpc.HelloServiceGrpc.HelloServiceImplBase;
import org.baeldung.grpc.VideoRequest;
import org.baeldung.grpc.VideoResponse;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.ByteBufAllocatorMetricProvider;

class HelloServiceImpl extends HelloServiceImplBase {
	Map<Integer, ByteBuffer> map1 = new HashMap<>();

	@Override
	public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

		String val = "";
		switch (request.getValdescCase()) {
		case VALUE:
			val = request.getValue();
			break;

		case VALUE1:
			val = request.getValue1();
		}

		String greeting = new StringBuilder().append("Hello, ").append(request.getFirstName()).append(" ")
				.append(request.getLastName()).append(val).toString();

		HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void video(VideoRequest request, StreamObserver<VideoResponse> response) {
		System.out.println(request.getVideoName());
		System.out.println("Initiating video streaming...");
		InputStream stream = null;
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			File file = new File("F:\\Movies\\Deja Vu (2006)\\Deja.Vu.2006.720p.x264.YIFY.mkv");
			stream = new FileInputStream(file);

			fos = new FileOutputStream("chk.ser");
			oos = new ObjectOutputStream(fos);

			ByteBufAllocator byteBufAllocator = ByteBufAllocator.DEFAULT;

			byte[] buffer = new byte[1024];
			System.out.println("Total direct mem: " + sun.misc.VM.maxDirectMemory());

			int length;
			// int j = 0;
			while ((length = stream.read(buffer)) > 0) {
				VideoResponse resp = VideoResponse.newBuilder().setVideoBytes(ByteString.copyFrom(buffer)).build();
				oos.writeObject(resp);
				response.onNext(resp);
				if (byteBufAllocator instanceof ByteBufAllocatorMetricProvider) {
					ByteBufAllocatorMetric metric = ((ByteBufAllocatorMetricProvider) byteBufAllocator).metric();
					System.out.println(metric.usedDirectMemory() / (1024 * 1024));
				}
				// System.out.println(j++);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			System.out.println("Done sending...");
			response.onCompleted();
			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/*
		 * int length; while ((length = stream.read(buffer)) > 0){
		 * 
		 * //System.out.println("Used direct memory: " +
		 * sun.misc.SharedSecrets.getJavaNioAccess().getDirectBufferPool().
		 * getMemoryUsed());
		 * response.onNext(VideoResponse.newBuilder().setVideoBytes(ByteString.
		 * copyFrom(buffer)).build());
		 * 
		 * 
		 * //Thread.sleep(1); } response.onCompleted(); } catch (Exception e) {
		 * response.onError(e); System.out.println(e); }
		 */
	}
}