package bryan.miranda.crudbryan2a

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class detallesProducto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalles_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productoId = intent.getStringExtra("productoId")
        val nombre = intent.getStringExtra("nombre")
        val precio = intent.getIntExtra("precio", 0)
        val cantidad = intent.getIntExtra("cantidad",0)

        val txtUUIDDetalle = findViewById<TextView>(R.id.txtUUIDDetalle)
        val txtNombreDetalle = findViewById<TextView>(R.id.txtNombreDetalle)
        val txtPrecioDetalle = findViewById<TextView>(R.id.txtPrecioDetalle)
        val txtCantidadDetalle = findViewById<TextView>(R.id.txtCantidadDetalle)

        txtUUIDDetalle.text = productoId
        txtNombreDetalle.text = nombre
        txtPrecioDetalle.text = precio.toString()
        txtCantidadDetalle.text = cantidad.toString()

    }
}