package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
    public fun cadenaConexion(): Connection? {
        try {
           val url = "jdbc:oracle:thin:@192.168.4.73:1521:xe"
            val usuario = "system"
            val contrasena = "itr2023"

            val connection = DriverManager.getConnection(url, usuario, contrasena)
            return connection
        }catch (e: Exception){
            println("Este es el error en la cadena de conexion: $e")
            return null
        }
    }
}