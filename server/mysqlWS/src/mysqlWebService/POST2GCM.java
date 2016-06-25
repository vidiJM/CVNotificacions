package mysqlWebService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;

import org.codehaus.jackson.map.ObjectMapper;

public class POST2GCM {
	
	public static void post(String apiKey, String regID, String nomPract, double nota){ try {

		URL url = new URL("https://android.googleapis.com/gcm/send");
        String json ="{\"to\": \""+regID+"\",\"notification\":{\"body\":\"En la practica "+nomPract+" has obtingut un: "+nota+"\"}}";
        
        
        
        String length = String.valueOf(json.length());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
        conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Content-Length", length);
		conn.setRequestProperty("Authorization", "key="+apiKey);
		
        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes("UTF-8"));
        os.flush();
        os.close();
		
		System.out.println(conn.getResponseCode());
		InputStream stream;
		if (conn.getResponseCode() >= 400){
			 stream = (InputStream) conn.getErrorStream();
		}else {
			 stream = (InputStream) conn.getInputStream();
	        
		}
		InputStreamReader isReader = new InputStreamReader(stream);

        //put output stream into a string
        BufferedReader br = new BufferedReader(isReader);
        String line;
        while((line = br.readLine()) != null){
            System.out.println(line);
        }

        System.out.println(conn.getResponseCode());

		if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ conn.getResponseCode());
		}

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	 }

	}
	
	
}
