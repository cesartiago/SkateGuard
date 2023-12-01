package exemplo.skateguard

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // inicialização do MqttManager usando lazy initialization
    private val mqttManager: MqttManager by lazy {
        MqttManager(this)
    }

    // inicialização do EditText para o nome do usuário usando lazy initialization
    private val nameEditText: EditText by lazy {
        findViewById(R.id.nameEditText)
    }

    // inicialização do EditText para o número de telefone usando lazy initialization
    private val phoneEditText: EditText by lazy {
        findViewById(R.id.phoneEditText)
    }

    // inicialização do Button usando lazy initialization
    private val enterButton: Button by lazy {
        findViewById(R.id.enterButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // configura o listener de clique para o botão
        enterButton.setOnClickListener {
            try {
                // obtém o nome e número de telefone inseridos pelo usuário
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()

                // verifica se o cliente MQTT já está conectado
                if (!mqttManager.isConnected()) {
                    // conectar ao broker MQTT usando o MqttManager
                    mqttManager.connect(this)
                } else {
                    // o cliente MQTT já está conectado, apenas registra no log
                    Log.d("MyApp", "O cliente MQTT já está conectado.")
                }

                // exibe uma mensagem de boas-vindas com os dados inseridos, ainda pensando no q fazer com os dados
                // porem ainda não faz nada!!!
                val message = "Bem-vindo ao SkateGuard!"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // em caso de erro, imprime o stack trace no log
                e.printStackTrace()
                Log.e("APP", "Erro ao clicar no botão Entrar", e)
            }
        }
    }

    // método chamado ao destruir a atividade
    override fun onDestroy() {
        super.onDestroy()
        // desconectar do broker MQTT ao destruir a atividade
        mqttManager.disconnect()
    }
}



