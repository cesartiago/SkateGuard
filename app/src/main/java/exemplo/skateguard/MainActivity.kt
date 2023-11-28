package exemplo.skateguard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val phoneEditText: EditText = findViewById(R.id.phoneEditText)
        val enterButton: Button = findViewById(R.id.enterButton)

        enterButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()

            // Exemplo: exibe uma mensagem com os dados inseridos
            val message = "Nome: $name\nTelefone: $phone"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
