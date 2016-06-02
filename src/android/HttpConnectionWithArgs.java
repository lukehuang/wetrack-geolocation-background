package studio.wetrack.geolocation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnectionWithArgs {

    public interface HttpResultListener {
        void onSuccess(String result);
        void onError(String result);
    }

    public static String doPost(String url , String contentType , String content , HttpResultListener listener) {
        String result = "";
        DataOutputStream out = null;
        BufferedReader br = null;
        try {
            URL postUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) postUrl
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type",
                    contentType);
            connection.setReadTimeout(5000);
            connection.connect();

            out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(content);
            out.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                    ||connection.getResponseCode() == HttpURLConnection.HTTP_CREATED
                    ||connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "utf-8"));
                String line = "";
                while ((line = br.readLine()) != null) {
                    result += line;
                }
                if(listener != null){
                    listener.onSuccess(result);
                }
            }else{
                if(listener != null){
                    listener.onError("" + connection.getResponseCode());
                }
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

	public static String doGet(String url) {
		String result = "";
		try {
			URL getUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) getUrl
					.openConnection();
			InputStreamReader in = new InputStreamReader(
					connection.getInputStream());
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = "";
			while (((inputLine = buffer.readLine()) != null)) {
				result += inputLine;
			}
			in.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
