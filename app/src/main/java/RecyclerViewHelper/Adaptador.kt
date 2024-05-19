package RecyclerViewHelper

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.media.RouteListingPreference
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import bryan.miranda.crudbryan2a.MainActivity
import bryan.miranda.crudbryan2a.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassProductos
import java.util.UUID

class Adaptador(private var Datos: List<dataClassProductos>) : RecyclerView.Adapter<ViewHolder>() {
    fun actualizarLista(nuevaLista: List<dataClassProductos>) {
        Datos = nuevaLista
        notifyDataSetChanged() // Notificar al adaptador sobre los cambios
    }

    fun actualizarItem(uuid: String, nuevoNombre: String) {
            val index = Datos.indexOfFirst { it.uuid == uuid }
            Datos[index].nombre = nuevoNombre
            notifyItemChanged(index)
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


    /////////////////TODO: Editar datos////////////////////////////////
    fun actualizarRegistro(nombreProducto: String, uuid: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val claseC = ClaseConexion().cadenaConexion()
            val addProducto = claseC?.prepareStatement("update tbproductos set nombreProducto = ? where uuid = ?")!!
            addProducto.setString(1, nombreProducto)
            addProducto.setString(2, uuid)
            addProducto.executeUpdate()

            val commit = claseC.prepareStatement("commit")!!
            commit.executeUpdate()

            withContext(Dispatchers.Main) {
                actualizarItem(uuid, nombreProducto)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.iitem_card, parent, false)

        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Datos[position]
        holder.textView.text = item.nombre

        //TODO: icono de Borrar
        holder.imgBorrar.setOnClickListener {

            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar")
            builder.setMessage("¿Quieres editar este elemento?")

            builder.setPositiveButton("Sí") { dialog, which ->
                eliminarRegistroDeBaseDeDatos(item.nombre, position)
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        //TODO: icono de Editar
        holder.imgEditar.setOnClickListener {
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar nombre")

            val input = EditText(context)
            input.setHint(item.nombre)
            builder.setView(input)

            builder.setPositiveButton("Actualizar") { dialog, which ->
                actualizarRegistro(input.text.toString(), item.uuid)
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
            }
        }
    }
