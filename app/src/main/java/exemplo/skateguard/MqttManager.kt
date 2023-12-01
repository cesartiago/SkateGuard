package exemplo.skateguard

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttManager(context: Context) {
    private val brokerUrl = "ssl://7451da45137e473cb02be70947e7ec59.s2.eu.hivemq.cloud:8883"
    private val clientId = "android"

    // criação do cliente MQTT com persistência em memória
    private val mqttClient = MqttAndroidClient(context, brokerUrl, clientId, MemoryPersistence())
    private var TAG = "MQTT"

    init {
        // configura o cliente MQTT
        configureMqttClient()
    }

    // configura o callback MQTT
    private fun configureMqttClient() {
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                // callback chamado quando uma mensagem é recebida
                Log.d(TAG, "Mensagem recebida: ${message.toString()} do tópico: $topic")
            }

            override fun connectionLost(cause: Throwable?) {
                // callback chamado quando a conexão MQTT é perdida
                Log.d(TAG, "Conexão perdida: ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // callback chamado quando uma mensagem é entregue com sucesso
            }
        })
    }

    // função para conectar ao broker MQTT
    fun connect(context: Context) {
        if (!mqttClient.isConnected) {
            val options = MqttConnectOptions()
            options.userName = "TrabalhoSD"
            options.password = "123456789Sd".toCharArray()

            try {
                // conecta ao broker MQTT
                mqttClient.connect(options, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // callback chamado em caso de conexão bem-sucedida
                        Log.d(TAG, "Conexão bem-sucedida")

                        // inscreve-se em um tópico e publica uma mensagem de teste
                        subscribe("Conexão")
                        publish("Conexão", "Conexão Estabelecida!")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        // callback chamado em caso de falha na conexão
                        Log.e(TAG, "Falha na conexão", exception)
                        exception?.printStackTrace()
                    }
                })
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        } else {
            // o cliente MQTT já está conectado
            val message = "O cliente MQTT já está conectado."
            val show = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "O cliente MQTT já está conectado.")
        }
    }

    // função para se inscrever em um tópico MQTT
    fun subscribe(topic: String, qos: Int = 1) {
        try {
            if (mqttClient.isConnected) {
                // se o cliente MQTT está conectado, inscreve-se no tópico
                mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // callback chamado em caso de sucesso na inscrição
                        Log.d(TAG, "Inscrito em $topic")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        // callback chamado em caso de falha na inscrição
                        Log.d(TAG, "Falha ao se inscrever em $topic")
                    }
                })
            } else {
                // o cliente MQTT não está conectado
                Log.d(TAG, "O cliente MQTT não está conectado.")
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // função para publicar uma mensagem em um tópico MQTT
    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            if (mqttClient.isConnected) {
                // se o cliente MQTT está conectado, publica a mensagem no tópico
                val message = MqttMessage()
                message.payload = msg.toByteArray()
                message.qos = qos
                message.isRetained = retained

                mqttClient.publish(topic, message, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // callback chamado em caso de sucesso na publicação
                        Log.d(TAG, "$msg publicado em $topic")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        // callback chamado em caso de falha na publicação
                        Log.d(TAG, "Falha ao publicar $msg em $topic")
                    }
                })
            } else {
                // o cliente MQTT não está conectado
                Log.d(TAG, "O cliente MQTT não está conectado.")
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // função para desconectar do broker MQTT
    fun disconnect() {
        try {
            if (mqttClient.isConnected) {
                // se o cliente MQTT está conectado, desconecta
                mqttClient.disconnect(null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // callback chamado em caso de sucesso na desconexão
                        Log.d(TAG, "Desconectado")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        // callback chamado em caso de falha na desconexão
                        Log.d(TAG, "Falha ao desconectar")
                    }
                })
            } else {
                // o cliente MQTT não está conectado
                Log.d(TAG, "O cliente MQTT não está conectado.")
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // verifica se o cliente MQTT está conectado
    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }
}






