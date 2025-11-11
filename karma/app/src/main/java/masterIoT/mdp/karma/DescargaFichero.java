package masterIoT.mdp.karma;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DescargaFichero implements Runnable{

    private Handler handler;

    private String strURL;

    public DescargaFichero (Handler handler, String strURL){
        this.handler = handler;
        this.strURL = strURL;
    }
    @Override
    public void run() {

        Message msg = handler.obtainMessage();
        Bundle msg_data = msg.getData();

        String response = "";
        StringBuilder textBuilder = new StringBuilder();
        HttpURLConnection urlConnection;

        try {
            URL url = new URL(strURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(reader);
            // We read the text contents line by line and add them to a StringBuilder:
            String line = in.readLine();
            while (line != null) {
                textBuilder.append(line).append("\n");
                line = in.readLine();
            }
            response = textBuilder.toString();

        } catch (Exception e) {
            response = e.toString();
        }
        msg_data.putString("response", response);
        handler.sendMessage(msg);

    }
}
