package exemplo.skateguard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.MapView
import android.widget.Button
import exemplo.skateguard.AppGlobals.mqttManager


class SecondActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var startButton: Button
    private lateinit var accelerometerManager: AccelerometerManager
    private var isSensorStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Inicialize o MapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // Inicialize o botão
        startButton = findViewById(R.id.startButton)

        fun initAccelerometerManager() {
            accelerometerManager = AccelerometerManager(this, object : AccelerometerManager.FallDetectionListener {
                override fun onFallDetected() {
                    Log.d("FallDetection", "Queda detectada!")
                    runOnUiThread {
                        // Atualiza o texto do botão para "Parar" quando uma queda é detectada
                        // Se o sensor já estiver iniciado, pare a escuta
                        accelerometerManager.stopListening()
                        isSensorStarted = false
                        startButton.text = "Iniciar"
                    }
                }
            }, mqttManager!!)
        }

        // Inicialize o AccelerometerManager
        initAccelerometerManager()

        // Adicione um OnClickListener ao botão
        startButton.setOnClickListener {
            if (isSensorStarted) {
                // Se o sensor já estiver iniciado, pare a escuta
                accelerometerManager.stopListening()
                isSensorStarted = false
                startButton.text = "Iniciar"
            } else {
                // Reinicialize o AccelerometerManager
                initAccelerometerManager()

                // Configurar o nome (substitua "SeuNomeAqui" pelo nome real)
                //accelerometerManager.setUserName("SeuNomeAqui")

                // Se o sensor não estiver iniciado, inicie a escuta
                accelerometerManager.startListening()
                isSensorStarted = true
                startButton.text = "Parar"
            }
        }

        // Inicialize o AccelerometerManager com a detecção de queda
        accelerometerManager = AccelerometerManager(this, object : AccelerometerManager.FallDetectionListener {
            override fun onFallDetected() {
                Log.d("FallDetection", "Queda detectada!")
                runOnUiThread {
                    // Atualiza o texto do botão para "Parar" quando uma queda é detectada
                    // Se o sensor já estiver iniciado, pare a escuta
                    accelerometerManager.stopListening()
                    isSensorStarted = false
                    startButton.text = "Iniciar"
                }
            }
        }, mqttManager!!)

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()

        // Pare a escuta do sensor quando a atividade estiver em pausa
        accelerometerManager.stopListening()
        isSensorStarted = false
        startButton.text = "Iniciar"
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

