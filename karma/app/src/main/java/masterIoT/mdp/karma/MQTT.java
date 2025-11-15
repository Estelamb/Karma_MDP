package masterIoT.mdp.karma;

import android.content.Context;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MQTT {
    private static MQTT instance;
    private Mqtt3AsyncClient client;
    private Context context;

    private String serverHost = "192.168.56.1";
    private int serverPort = 1883;

    // Map para guardar callbacks por topic
    private Map<String, MessageCallback> callbacks;

    public interface MessageCallback {
        void onMessageReceived(String topic, String message);
    }

    // Constructor privado para Singleton
    private MQTT(Context context) {
        this.context = context.getApplicationContext();
        this.callbacks = new HashMap<>();
        createMQTTclient();
    }

    // Patrón Singleton
    public static synchronized MQTT getInstance(Context context) {
        if (instance == null) {
            instance = new MQTT(context);
        }
        return instance;
    }

    private void createMQTTclient() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("android-client-" + new Date().getTime())
                .serverHost(serverHost)
                .serverPort(serverPort)
                .buildAsync();
    }

    public void connect() {
        if (client != null) {
            client.connectWith().send();
        }
    }

    public void subscribe(String topic, MessageCallback callback) {
        if (client != null && client.getState().isConnected()) {
            // Guardar callback para este topic
            callbacks.put(topic, callback);

            client.subscribeWith()
                    .topicFilter(topic)
                    .callback(publish -> {
                        handleIncomingMessage(publish);
                    })
                    .send();
        }
    }

    public void publish(String topic, String message) {
        if (client != null && client.getState().isConnected()) {
            client.publishWith()
                    .topic(topic)
                    .payload(message.getBytes())
                    .send();
        }
    }

    private void handleIncomingMessage(Mqtt3Publish publish) {
        String topic = publish.getTopic().toString();
        String message = new String(publish.getPayloadAsBytes());

        // Buscar callback para este topic y ejecutarlo
        MessageCallback callback = callbacks.get(topic);
        if (callback != null) {
            callback.onMessageReceived(topic, message);
        }
    }

    public void unsubscribe(String topic) {
        callbacks.remove(topic);
    }

    public void disconnect() {
        if (client != null) {
            client.disconnect();
            callbacks.clear();
        }
    }

    public boolean isConnected() {
        return client != null && client.getState().isConnected();
    }
}