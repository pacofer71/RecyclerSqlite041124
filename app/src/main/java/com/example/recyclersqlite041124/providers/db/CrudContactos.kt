package com.example.recyclersqlite041124.providers.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.recyclersqlite041124.Aplicacion
import com.example.recyclersqlite041124.models.ContactoModel

class CrudContactos {
    fun create(c: ContactoModel): Long{
        val con=Aplicacion.llave.writableDatabase //abrimos la bbdd en modo escritura
        return try{
            con.insertWithOnConflict(
                Aplicacion.TABLA,
                null,
                c.toContentValues(),
                SQLiteDatabase.CONFLICT_IGNORE
            )
        }catch(ex: Exception){
            ex.printStackTrace()
            -1L
        }finally {
            con.close()
        }
    }

    fun read(): MutableList<ContactoModel>{
        val lista = mutableListOf<ContactoModel>()
        val con =Aplicacion.llave.readableDatabase
        try{
            val cursor=con.query(
                Aplicacion.TABLA,
                arrayOf("id", "nombre", "apellidos", "email", "imagen"),
                null,
                null,
                null,
                null,
                null
            )
            while(cursor.moveToNext()){
                val contacto=ContactoModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
                )
                lista.add(contacto)
            }
        }catch(ex: Exception){
            ex.printStackTrace()
        }finally {
            con.close()
        }
        return lista
    }

    public fun borrar(id: Int): Boolean{
        val con=Aplicacion.llave.writableDatabase
        val contatoBorrado=con.delete(Aplicacion.TABLA, "id=?", arrayOf(id.toString()))
        con.close()
        return contatoBorrado>0
    }

    public fun update(c: ContactoModel): Boolean{
        val con = Aplicacion.llave.writableDatabase
        val values=c.toContentValues()
        var filasAfectadas=0
        val q="select id from ${Aplicacion.TABLA} where email=? AND id <> ?"
        val cursor=con.rawQuery(q, arrayOf(c.email, c.id.toString()))
        val existeEmail=cursor.moveToFirst()
        cursor.close()
        if(!existeEmail){
            filasAfectadas=con.update(Aplicacion.TABLA, values, "id=?", arrayOf(c.id.toString()))
        }
        con.close()
        return  filasAfectadas>0
    }

    public fun borrarTodo(){
        val con=Aplicacion.llave.writableDatabase
        con.execSQL("delete from ${Aplicacion.TABLA}")
        con.close()
    }


    private fun ContactoModel.toContentValues(): ContentValues{
        return  ContentValues().apply {
            put("nombre", nombre)
            put("apellidos", apellidos)
            put("email", email)
            put("imagen", imagen)
        }
    }
}