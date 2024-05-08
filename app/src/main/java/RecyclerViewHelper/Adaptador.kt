package RecyclerViewHelper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bryan.miranda.crudbryan2a.MainActivity
import bryan.miranda.crudbryan2a.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassProductos

class Adaptador(private var Datos: List<dataClassProductos>) : RecyclerView.Adapter<ViewHolder>() {
    fun actualizarLista(nuevaLista: List<dataClassProductos>) {
        Datos = nuevaLista
        notifyDataSetChanged() // Notificar al adaptador sobre los cambios
    }

    ///////////////////TODO: Eliminar datos////////////////////////
    fun eliminarRegistroDeBaseDeDatos(nombreProducto: String, posicion: Int) {
        // Actualizar lista de datos y notificar al adaptador
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            //1- Creo un objeto de la clase conexion
            val claseC = ClaseConexion().cadenaConexion()

            //2- creo una variable que contenga un PrepareStatement
            val addProducto =
                claseC?.prepareStatement("delete tbproductos where nombreProducto = ?")!!
            addProducto.setString(1, nombreProducto)
            addProducto.executeUpdate()

            //Hago un commit para que amarre
            val commit = claseC.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        //Quito los datos de la lista
        notifyItemRemoved(posicion)
        //Le notifico al adaptador
        notifyDataSetChanged()
    }
    ////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.iitem_card, parent, false)

        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = Datos[position]
        holder.textView.text = producto.nombre

        val item = Datos[position]
        holder.imgBorrar.setOnClickListener {
            eliminarRegistroDeBaseDeDatos(item.nombre, position)
        }
    }

}
