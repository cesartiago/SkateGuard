package exemplo.skateguard

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.MapView
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import exemplo.skateguard.AppGlobals.mqttManager

class SecondActivity : AppCompatActivity(), LocationManager.LocationCallback {

    private lateinit var locationManager: LocationManager
    private lateinit var mapView: MapView
    private lateinit var startButton: Button
    private lateinit var messageTextView: TextView
    private lateinit var locationTextView: TextView

    private lateinit var accelerometerManager: AccelerometerManager
    private var isSensorStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Inicializar a classe LocationManager
        locationManager = LocationManager(this, this)

        // Solicitar permissão de localização
        locationManager.requestLocationPermission()

        // Inicialize o MapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // Inicialize o botão
        startButton = findViewById(R.id.startButton)

        // Inicialize as referências aos elementos de texto
        messageTextView = findViewById(R.id.messageTextView)
        locationTextView = findViewById(R.id.locationTextView)


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
            }, locationManager,
                mqttManager!!)
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
                Log.d("LocationOn", "Queda detectada!")
                runOnUiThread {
                    // Atualiza o texto do botão para "Parar" quando uma queda é detectada
                    // Se o sensor já estiver iniciado, pare a escuta
                    accelerometerManager.stopListening()
                    isSensorStarted = false
                    startButton.text = "Iniciar"
                }
            }
        }, locationManager,
            mqttManager!!)

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

    override fun onLocationPermissionGranted() {
        Log.d("SecondActivity", "Location permission granted in SecondActivity")

        // Verificar se a permissão de localização está concedida
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissão concedida, obter a última localização
            locationManager.getLastLocation()
        } else {
            // Permissão não concedida, solicitar permissão (isso deveria ser tratado anteriormente)
            Log.e("SecondActivity", "Location permission not granted.")
        }
    }
}

