package exemplo.skateguard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.Log
import kotlin.math.pow
import kotlin.math.sqrt

// definição da classe AccelerometerManager, que implementa SensorEventListener
class AccelerometerManager(
    context: Context, // Contexto é necessário para acessar serviços do sistema, como o SensorManager
    private val currentSpeed: Double = 0.0, // velocidade atual do skate em metros por segundo (m/s)
    private val fallDetectionListener: FallDetectionListener,
    private val mqttManager: MqttManager
) : SensorEventListener {

    // interface para o callback de detecção de queda
    interface FallDetectionListener {
        fun onFallDetected()
    }

    // gerenciador do sensor e instância do acelerômetro
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // variáveis para rastrear a última aceleração e tempo
    private var lastAcceleration: Double = 0.0
    private var lastTime: Long = System.currentTimeMillis()

    // usar um Handler para implementar o temporizador
    private val handler = Handler()

    // inicialização da classe, verifica se o acelerômetro está disponível
    init {
        if (accelerometer == null) {
            throw UnsupportedOperationException("Acelerômetro não está disponível no dispositivo.")
        }
    }

    // método para iniciar a escuta do acelerômetro
    fun startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // método para parar a escuta do acelerômetro
    fun stopListening() {
        Log.d("AccelerometerManager", "Parando a escuta do acelerômetro")
        sensorManager.unregisterListener(this)
    }

    // método chamado quando a precisão do sensor muda (não utilizado neste exemplo)
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não estamos preocupados com mudanças na precisão do sensor neste exemplo
    }

    // método chamado quando os valores do sensor mudam
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == accelerometer) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = (currentTime - lastTime) / 1000.0 // Delta de tempo em segundos

            // cálculo da aceleração usando a fórmula Euclidiana
            val acceleration =
                sqrt(event.values[0].toDouble().pow(2.0) + event.values[1].toDouble().pow(2.0) + event.values[2].toDouble().pow(2.0))

            // calcular a mudança na velocidade usando a aceleração
            val deltaVelocity = (acceleration + lastAcceleration) / 2 * deltaTime

            // atualizar a última aceleração e o último tempo
            lastAcceleration = acceleration
            lastTime = currentTime

            Log.d("AccelerometerManager", "Aceleração X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}")

            // chamada ao método de detecção de queda
            detectFall(deltaVelocity, acceleration)
        }
    }

    // método privado para a lógica de detecção de queda
    private fun detectFall(deltaVelocity: Double, acceleration: Double) {
        // implementar a lógica de detecção de queda, ainda não tá pronto, precisa de ajuste sobre a aceleração, a parada do nada
        //e coisas do tipo.
        // deltaVelocity < -4.0 && acceleration > 10.0
        if (acceleration > 10.0) {
            // a velocidade diminuiu bruscamente e a aceleração é alta, indicando uma possível queda

            // adicionar um atraso de 10 segundos para confirmar a queda
            handler.postDelayed({
                // Chamar o callback de detecção de queda
                fallDetectionListener.onFallDetected()

                // Adicionar a mensagem de log dentro do AccelerometerManager
                Log.d("FallDetection", "Queda detectada!")

                // Adicionar a lógica para publicar a mensagem MQTT aqui
                publishFallMessage()

                Log.d("FallDetection", "Queda detectada!")

            }, 10000) // 10000 milissegundos = 10 segundos
        }
    }

    // Método para publicar a mensagem MQTT de queda
    private fun publishFallMessage() {
        // Substitua "seu_topico" pelo tópico MQTT desejado
        val topic = "/skateguard/falls"
        val message = "Queda detectada!"

        // Verificar se o cliente MQTT está conectado antes de tentar publicar
        if (mqttManager.isConnected()) {
            // Publicar a mensagem MQTT
            mqttManager.publish(topic, message)

            Log.d("MQTT", "Mensagem publicada com sucesso no tópico $topic")
        } else {
            Log.e("MQTT", "Erro: Cliente MQTT não está conectado. A mensagem não foi publicada.")
        }
    }
}
