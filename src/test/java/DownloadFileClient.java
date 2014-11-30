import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class DownloadFileClient {

	public static void main(String[] args) {
		try {
			URL url = new URL("http://localhost:8080/weblog4jdemo/uploadFile");
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data");
			
			if (conn.getResponseCode() == 200) {
				InputStream inputStream = conn.getInputStream();
				OutputStream output = new FileOutputStream(
						"C:/downloads/copyOfTest.txt");
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
				}
				output.close();
			} else {
				Scanner scanner = new Scanner(conn.getErrorStream());
				System.out.println(scanner.next());
				scanner.close();
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
