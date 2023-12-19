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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.BitmapDescriptorFactory






class SecondActivity : AppCompatActivity(), LocationManager.LocationCallback, OnMapReadyCallback, AccelerometerManager.FallDetectionListener {

    private lateinit var locationManager: LocationManager
    private lateinit var mapView: MapView
    private lateinit var startButton: Button
    private lateinit var messageTextView: TextView
    private lateinit var locationTextView: TextView

    private lateinit var googleMap: GoogleMap
    private var mapViewInitialized = false
    private var currentLocationMarker: Marker? = null

    // Adicione uma variável para armazenar a última localização
    private var lastKnownLocation: LatLng? = null


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
        mapView.getMapAsync(this) // Mantenha esta linha aqui

        // Inicialize o botão
        startButton = findViewById(R.id.startButton)

        // Inicialize as referências aos elementos de texto
        messageTextView = findViewById(R.id.messageTextView)
        locationTextView = findViewById(R.id.locationTextView)


        fun initAccelerometerManager() {
            accelerometerManager = AccelerometerManager(this, object : AccelerometerManager.FallDetectionListener {
                // Este método é chamado quando uma queda é detectada pelo AccelerometerManager


                override fun onFallDetected(latitude: Double, longitude: Double) {
                    Log.d("FallDetection", "Queda detectada!")
                    runOnUiThread {
                        // Atualiza o texto do botão para "Parar" quando uma queda é detectada
                        // Se o sensor já estiver iniciado, pare a escuta
                        accelerometerManager.stopListening()
                        isSensorStarted = false
                        startButton.text = "Iniciar"

                    }
                updateMapLocation(latitude, longitude)
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


    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapViewInitialized = true




        Log.d("MapReady", "Mapa está pronto - sendo chamado")


        // Verificar se a permissão de localização está concedida
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissão concedida, obter a última localização
            locationManager.getLastLocation()

            // Verificar se há uma última localização conhecida
            lastKnownLocation?.let {
                // Se houver, chame updateMapLocation
                updateMapLocation(it.latitude, it.longitude)
                // Limpe a última localização conhecida após usá-la
                lastKnownLocation = null
            }
        } else {
            // Permissão não concedida, solicitar permissão (isso deveria ser tratado anteriormente)
            Log.e("SecondActivity", "Location permission not granted.")
        }
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


    fun updateMapLocation(latitude: Double, longitude: Double) {
        Log.d("MapUpdate", "UpdateMapLocation chamado com latitude: $latitude, longitude: $longitude")
        if (!mapViewInitialized) {
            Log.d("MapUpdate", "MapView não está inicializado")

            // Se o mapa não estiver inicializado, armazene a última localização conhecida
            lastKnownLocation = LatLng(latitude, longitude)
            // Aguarde a inicialização do mapa antes de tentar atualizar a localização
            return
        }

        val currentLocation = LatLng(latitude, longitude)

        if (currentLocationMarker == null) {
            Log.d("MapUpdate", "Criando novo marcador")
            // Se o marcador ainda não foi criado, crie-o e adicione ao mapa
            val markerOptions = MarkerOptions().position(currentLocation).title("Sua Localização Atual")

            // Configurar o marcador para ser uma bolinha azul
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

            currentLocationMarker = googleMap.addMarker(markerOptions)
        } else {
            Log.d("MapUpdate", "Atualizando posição do marcador")
            // Se o marcador já existe, apenas atualize sua posição
            currentLocationMarker?.position = currentLocation
        }

        // Mova a câmera para a nova localização
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        // Opcional: ajuste o nível de zoom
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f))
    }

    //Tem que implementar
    override fun onFallDetected(latitude: Double, longitude: Double) {
        Log.d("FallDetection", "Queda detectada!")
        runOnUiThread {
            // Atualiza o texto do botão para "Parar" quando uma queda é detectada
            // Se o sensor já estiver iniciado, pare a escuta
            accelerometerManager.stopListening()
            isSensorStarted = false
            startButton.text = "Iniciar"

            // Adicione a chamada do método updateMapLocation dentro do runOnUiThread
            //updateMapLocation(latitude, longitude)
        }
    }

}