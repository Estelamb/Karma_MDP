package masterIoT.mdp.karma;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MQTT {
    private static MQTT instance;
    private Mqtt3AsyncClient client;
    private Context context;

    private String serverHost = "192.168.56.1";
    private int serverPort = 1883;

    // Map para guardar múltiples callbacks por topic
    private Map<String, List<MessageCallback>> callbacks;

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
            client.connectWith().send().whenComplete((connAck, throwable) -> {
                if (throwable != null) {
                    // handle failure
                    Log.d("MQTT", "Problem connecting to server:");
                    Log.d("MQTT", throwable.toString());
                } else {
                    // connected -> setup subscribes and publish a message
                    Log.d("MQTT", "Connected to server");
                    subscribe_MQTT("app/users/+/karmaTotal");
                    subscribe_MQTT("app/users/+/mission");
                    subscribe_MQTT("app/users/+/remove");
                }
        });}
    }

    public void subscribe_MQTT(String topic) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(publish -> {
                    Log.d("MQTT", "Message received");
                    Log.d("MQTT", new String(publish.getPayloadAsBytes()));
                    if (publish.getPayloadAsBytes()!=null){
                        String mensaje=new String(publish.getPayloadAsBytes());
                        String[] sep = mensaje.split(":");
                        if (sep.length==2){
                            SharedPreferences prefs = context.getSharedPreferences("UsersKarma", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(sep[0], Integer.parseInt(sep[1]));
                            editor.apply();
                        }if (sep.length>2){
                            SharedPreferences prefs = context.getSharedPreferences("UsersMissions", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            StringBuilder t=new StringBuilder();
                            for(int i=1;i<sep.length;i++){
                                t.append(",").append(sep[i]);
                            }
                            editor.putString(sep[0], t.toString());
                            editor.apply();
                        }
                    }
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        // Handle failure to subscribe
                        Log.d("MQTT", "Problem subscribing to topic:");
                        Log.d("MQTT", throwable.toString());

                    } else {
                        // Handle successful subscription, e.g. logging or incrementing a metric
                        Log.d("MQTT", "Subscribed to topic");
                    }
                });
    }

    // METODO CON RETAINED
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

    private void handleIncomingMessage(Mqtt3Publish publish) {
        String topic = publish.getTopic().toString();
        String message = new String(publish.getPayloadAsBytes());

        Log.d("MQTT", "Mensaje recibido - Topic: " + topic + " - Mensaje: " + message);

        // Ejecutar TODOS los callbacks para este topic
        synchronized (callbacks) {
            List<MessageCallback> topicCallbacks = callbacks.get(topic);
            if (topicCallbacks != null) {
                Log.d("MQTT", "Ejecutando " + topicCallbacks.size() + " callbacks para: " + topic);
                // Crear una copia para evitar ConcurrentModificationException
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

    public void unsubscribe(String topic, MessageCallback callback) {
        synchronized (callbacks) {
            List<MessageCallback> topicCallbacks = callbacks.get(topic);
            if (topicCallbacks != null) {
                boolean removed = topicCallbacks.remove(callback);
                Log.d("MQTT", "Callback removido de: " + topic + " - ¿Éxito? " + removed);

                if (topicCallbacks.isEmpty()) {
                    callbacks.remove(topic);
                    // Opcional: desuscribirse del broker si no hay más callbacks
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

    public void disconnect() {
        if (client != null) {
            client.disconnect();
            synchronized (callbacks) {
                callbacks.clear();
            }
            Log.d("MQTT", "Cliente desconectado y callbacks limpiados");
        }
    }

    public boolean isConnected() {
        return client != null && client.getState().isConnected();
    }

    // Método para debugging
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