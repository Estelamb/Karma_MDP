package masterIoT.mdp.karma;

import static android.os.SystemClock.sleep;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import masterIoT.mdp.karma.missions.Mission;
import masterIoT.mdp.karma.missions.MissionsDataset;

/**
 * @class MQTT
 * @brief Singleton class managing MQTT communication for the application.
 *
 * This class encapsulates all MQTT logic, including connection handling,
 * subscriptions, publishing, and callback dispatching. It supports multiple
 * callbacks per topic and stores received values in SharedPreferences.
 */
public class MQTT {

    /** Singleton instance of the MQTT manager. */
    private static MQTT instance;

    /** Asynchronous MQTT client from HiveMQ. */
    private Mqtt3AsyncClient client;

    /** Application context used for SharedPreferences and persistence. */
    private Context context;

    /** Host address of the MQTT broker. */
    private String serverHost = "192.168.56.1";

    /** Port number of the MQTT broker. */
    private int serverPort = 1883;
    MissionsDataset missionsDataset;
    /**
     * Map storing lists of callbacks for each subscribed topic.
     * Allows multiple listeners per topic.
     */
    private Map<String, List<MessageCallback>> callbacks;

    /**
     * @interface MessageCallback
     * @brief Callback interface for receiving MQTT messages.
     */
    public interface MessageCallback {
        /**
         * Called when a message is received on a subscribed topic.
         * @param topic Topic on which the message was received.
         * @param message Payload of the MQTT message.
         */
        void onMessageReceived(String topic, String message);
    }

    /**
     * @brief Private constructor for the Singleton pattern.
     * @param context Application context.
     */
    private MQTT(Context context) {
        this.context = context.getApplicationContext();
        this.callbacks = new HashMap<>();
        createMQTTclient();
        missionsDataset = MissionsDataset.getInstance();
    }

    /**
     * @brief Returns the Singleton instance of the MQTT manager.
     * @param context Application context.
     * @return Singleton instance of MQTT.
     */
    public static synchronized MQTT getInstance(Context context) {
        if (instance == null) {
            instance = new MQTT(context);
        }
        return instance;
    }

    /**
     * @brief Creates the HiveMQ asynchronous MQTT client.
     */
    private void createMQTTclient() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("android-client-" + new Date().getTime())
                .serverHost(serverHost)
                .serverPort(serverPort)
                .buildAsync();
    }

    /**
     * @brief Connects to the MQTT broker and subscribes to default topics.
     */
    public void connect() {
        if (client != null) {
            client.connectWith().send().whenComplete((connAck, throwable) -> {
                if (throwable != null) {
                    Log.d("MQTT", "Problem connecting to server:");
                    Log.d("MQTT", throwable.toString());
                } else {
                    Log.d("MQTT", "Connected to server");
                    //sleep(1000);
                    subscribe_MQTT("app/users/+/karmaTotal");
                    subscribe_MQTT("app/users/user/missionPublish");
                    subscribe_MQTT("app/users/del/missionDelete");
                }
            });}
    }

    /**
     * @brief Subscribes to an MQTT topic with a dynamic callback dispatcher.
     * @param topic Topic string to subscribe to (may include wildcards).
     */
    public void subscribe_MQTT(String topic) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(publish -> {
                    Log.d("MQTT", "Message received");
                    Log.d("MQTT", new String(publish.getPayloadAsBytes()));

                    if (publish.getPayloadAsBytes()!=null){
                        String mensaje=new String(publish.getPayloadAsBytes());
                        String[] sep = mensaje.split(":");
                        String tp = publish.getTopic().toString();
                        String[] parts = tp.split("/");
                        if (parts[3].equals("karmaTotal")){
                            SharedPreferences prefs = context.getSharedPreferences("UsersKarma", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(sep[0], Integer.parseInt(sep[1]));
                            editor.apply();
                        }
                        if (parts[3].equals("missionPublish")){
                            SharedPreferences prefs = context.getSharedPreferences("UsersMissions", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            //Log.d("MQTT", Arrays.toString(sep));
                            //Log.d("MQTT", "key "+Long.getLong(sep[5]));
                            Mission newMission = new Mission(sep[1], Integer.parseInt(sep[2]), Integer.parseInt(sep[3]), sep[4], Long.parseLong(sep[5]), "", false);
                            //Log.d("MQTT", String.valueOf(newMission.getKey()));
                            missionsDataset.addMission(newMission);

                            StringBuilder t=new StringBuilder();
                            for(int i=1;i<sep.length;i++){
                                t.append(",").append(sep[i]);
                            }
                            editor.putString(sep[0], t.toString());
                            editor.apply();
                        }
                        if (parts[3].equals("missionDelete")) {
                            Log.d("MQTT", "Delete");
                            missionsDataset.removeMissionWithKey(Long.parseLong(sep[1]));

                        }
                    }
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        Log.d("MQTT", "Problem subscribing to topic:");
                        Log.d("MQTT", throwable.toString());
                    } else {
                        Log.d("MQTT", "Subscribed to topic");
                    }
                });
    }

    /**
     * @brief Publishes a message to a topic, optionally retained.
     * @param topic Topic to publish to.
     * @param message String payload to send.
     * @param retained Whether the message should be retained by the broker.
     */
    public void publish(String topic, String message, boolean retained) {
        if (client != null && client.getState().isConnected()) {
            client.publishWith()
                    .topic(topic)
                    .payload(message.getBytes())
                    .retain(retained)
                    .send();
            Log.d("MQTT", "Mensaje publicado en: " + topic + " - " + message);
        } else {
            Log.e("MQTT", "Cliente no conectado, no se puede publicar en: " + topic);
        }
    }

    /**
     * @brief Dispatches an incoming MQTT message to registered callbacks.
     * @param publish Received MQTT publish object.
     */
    private void handleIncomingMessage(Mqtt3Publish publish) {
        String topic = publish.getTopic().toString();
        String message = new String(publish.getPayloadAsBytes());

        Log.d("MQTT", "Mensaje recibido - Topic: " + topic + " - Mensaje: " + message);

        synchronized (callbacks) {
            List<MessageCallback> topicCallbacks = callbacks.get(topic);
            if (topicCallbacks != null) {
                Log.d("MQTT", "Ejecutando " + topicCallbacks.size() + " callbacks para: " + topic);

                List<MessageCallback> callbacksCopy = new ArrayList<>(topicCallbacks);
                for (MessageCallback callback : callbacksCopy) {
                    try {
                        callback.onMessageReceived(topic, message);
                    } catch (Exception e) {
                        Log.e("MQTT", "Error en callback para topic: " + topic, e);
                    }
                }
            } else {
                Log.w("MQTT", "No hay callbacks registrados para topic: " + topic);
            }
        }
    }

    /**
     * @brief Unsubscribes a single callback from a topic.
     * @param topic Topic from which to remove the callback.
     * @param callback Callback instance to remove.
     */
    public void unsubscribe(String topic, MessageCallback callback) {
        synchronized (callbacks) {
            List<MessageCallback> topicCallbacks = callbacks.get(topic);
            if (topicCallbacks != null) {
                boolean removed = topicCallbacks.remove(callback);
                Log.d("MQTT", "Callback removido de: " + topic + " - ¿Éxito? " + removed);

                if (topicCallbacks.isEmpty()) {
                    callbacks.remove(topic);

                    if (client != null && client.getState().isConnected()) {
                        client.unsubscribeWith()
                                .topicFilter(topic)
                                .send();
                        Log.d("MQTT", "Desuscrito del broker: " + topic);
                    }
                }
            }
        }
    }

    /**
     * @brief Removes all callbacks and unsubscribes a topic.
     * @param topic Topic to remove from the callback map.
     */
    public void unsubscribeAll(String topic) {
        synchronized (callbacks) {
            callbacks.remove(topic);
            if (client != null && client.getState().isConnected()) {
                client.unsubscribeWith()
                        .topicFilter(topic)
                        .send();
                Log.d("MQTT", "Todos los callbacks removidos y desuscrito: " + topic);
            }
        }
    }

    /**
     * @brief Disconnects from the MQTT broker and clears callbacks.
     */
    public void disconnect() {
        if (client != null) {
            client.disconnect();
            synchronized (callbacks) {
                callbacks.clear();
            }
            Log.d("MQTT", "Cliente desconectado y callbacks limpiados");
        }
    }

    /**
     * @brief Checks if the MQTT client is currently connected.
     * @return True if connected, false otherwise.
     */
    public boolean isConnected() {
        return client != null && client.getState().isConnected();
    }

    /**
     * @brief Prints all registered callbacks for debugging purposes.
     */
    public void printCallbacks() {
        synchronized (callbacks) {
            Log.d("MQTT", "=== CALLBACKS REGISTRADOS ===");
            for (Map.Entry<String, List<MessageCallback>> entry : callbacks.entrySet()) {
                Log.d("MQTT", "Topic: " + entry.getKey() + " - Callbacks: " + entry.getValue().size());
            }
            Log.d("MQTT", "=============================");
        }
    }
}
