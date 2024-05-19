package bryan.miranda.crudbryan2a

import RecyclerViewHelper.Adaptador
import modelo.dataClassProductos
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1- Mandar a llamar todos los elementos de la pantalla
        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtPrecio = findViewById<EditText>(R.id.txtPrecio)
        val txtCantidad = findViewById<EditText>(R.id.txtCantidad)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rcvProductos = findViewById<RecyclerView>(R.id.rcvProductos)
        //Asigno un layout al RecyclerView
        rcvProductos.layoutManager = LinearLayoutManager(this)

        //////////////TODO: Mostrar datos//////////////////

        //Funcion para obtener datos
        fun obtenerProductos(): List<dataClassProductos> {
            val connection = ClaseConexion().cadenaConexion()
            val statement = connection?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbproductos")!!
            val productos = mutableListOf<dataClassProductos>()
            while (resultSet.next()) {
                val uuid = resultSet.getString("uuid")
                val nombre = resultSet.getString("nombreProducto")
                val producto = dataClassProductos(nombre, uuid)
                productos.add(producto)
            }
            return productos
        }

        //Asigno un adaptador al RecyclerView
        //El adaptador se encarga de actualizar los datos en la lista
        CoroutineScope(Dispatchers.IO).launch {
            val productosDB = obtenerProductos()
            withContext(Dispatchers.Main) {
                val adapter = Adaptador(productosDB)
                rcvProductos.adapter = adapter
            }
        }

        //////////////TODO: guardar datos//////////////////
        //2- Programar el boton
        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //Guardar datos
                //1- Creo un objeto de la clase conexion
                val claseC = ClaseConexion().cadenaConexion()

                //2- creo una variable que contenga un PrepareStatement
                val addProducto =
                    claseC?.prepareStatement("insert into tbproductos(uuid, nombreProducto, precio, cantidad) values(?, ?, ?, ?)")!!
                addProducto.setString(1, UUID.randomUUID().toString())
                addProducto.setString(2, txtNombre.text.toString())
                addProducto.setInt(3, txtPrecio.text.toString().toInt())
                addProducto.setInt(4, txtCantidad.text.toString().toInt())
                addProducto.executeUpdate()

                //Luego de guardarlos: Actualizar la lista de productos
                val nuevosProductos = obtenerProductos()
                withContext(Dispatchers.Main) {
                    // Actualizar el adaptador con los nuevos datos
                    (rcvProductos.adapter as? Adaptador)?.actualizarLista(nuevosProductos)
                }
            }
        }
    }

    /*
    fun actualizarRegistro(nombreProducto: String, uuid: String, adaptador: Adaptador) {
        GlobalScope.launch(Dispatchers.IO) {
            val claseC = ClaseConexion().cadenaConexion()
            val addProducto = claseC?.prepareStatement("update tbproductos set nombreProducto = ? where uuid = ?")!!
            addProducto.setString(1, nombreProducto)
            addProducto.setString(2, uuid)
            addProducto.executeUpdate()

            val commit = claseC.prepareStatement("commit")!!
            commit.executeUpdate()

            withContext(Dispatchers.Main) {
                adaptador.updateItem(uuid, nombreProducto)
            }
        }
    }

*/

}

