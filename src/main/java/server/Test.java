package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Test {

	static Map<Integer, byte[]> fileMap = new HashMap<Integer, byte[]>();
	public static void main(String[] args) throws IOException {
		InputStream stream = null;
		OutputStream out = null;
		
		
		try {
			File file = new File("F:\\Movies\\Deja Vu (2006)\\Deja.Vu.2006.720p.x264.YIFY.mkv");
			File file1 = new File("F:\\Movies\\Deja Vu (2006)\\newfile.mkv");
			stream = new FileInputStream(file);
			out = new FileOutputStream(file1);
			byte[] buffer = new byte[1024];
			int length;
			int j = 0;
			while ((length = stream.read(buffer)) > 0) {
				j++;
				System.out.println(j);
				fileMap.put(j, buffer);
				//out.write(buffer);
			}
		} catch (Exception e) {
		} finally {
			stream.close();
			out.flush();
			out.close();
		}
	}
}
