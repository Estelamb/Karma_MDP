package masterIoT.mdp.karma;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @class FileDownload
 * @brief Runnable class that performs an HTTP GET request in a background thread.
 *
 * The download result (success or exception) is sent back through a Handler,
 * inside a message bundle with key `"response"`.
 */
public class FileDownload implements Runnable {

    /**
     * @brief Handler used to return the downloaded content to the main thread.
     */
    private Handler handler;

    /**
     * @brief URL string from which the content will be downloaded.
     */
    private String strURL;

    /**
     * @brief Constructor of the FileDownload class.
     *
     * @param handler Handler to deliver the result back to the main thread.
     * @param strURL String containing the URL to download.
     */
    public FileDownload(Handler handler, String strURL) {
        this.handler = handler;
        this.strURL = strURL;
    }

    /**
     * @brief Performs the HTTP GET request and sends the result through the Handler.
     *
     * The result is returned in a Message bundle with key:
     * - `"response"` → Contains the downloaded text or an exception message.
     *
     * @note This method runs automatically when the thread starts.
     */
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

            // Read content line by line
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
