package com.example.recyclersqlite041124

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.recyclersqlite041124.adapters.ContactoAdapter
import com.example.recyclersqlite041124.databinding.ActivityMainBinding
import com.example.recyclersqlite041124.models.ContactoModel
import com.example.recyclersqlite041124.providers.db.CrudContactos

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var lista= mutableListOf<ContactoModel>()
    private lateinit var adapter: ContactoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setListeners()
        setRecycler()
        title="Mi Agenda"
    }

    private fun setRecycler() {
        val layoutManger=LinearLayoutManager(this)
        binding.recyclerView.layoutManager=layoutManger
        traerRegistros()
        adapter=ContactoAdapter(lista, {position->borrarContacto(position)}, {c->update(c)})
        binding.recyclerView.adapter=adapter
    }
    //----------------------------------------------------------------------------------------------
    private fun update(c: ContactoModel){
        val i=Intent(this, AddActivity::class.java).apply {
            putExtra("CONTACTO", c)
        }
        startActivity(i)
    }
    //----------------------------------------------------------------------------------------------
    private fun borrarContacto(p: Int){
        val id=lista[p].id
        //Lo elimino de la lisa
        lista.removeAt(p)
        //lo elimino de la base de datos
        if(CrudContactos().borrar(id)){
            adapter.notifyItemRemoved(p)
        }else{
            Toast.makeText(this, "No se eliminó ningún registro", Toast.LENGTH_SHORT).show()
        }
    }
    //----------------------------------------------------------------------------------------------

    private fun traerRegistros() {
        lista=CrudContactos().read()
        if(lista.size>0){
            binding.ivContactos.visibility=View.INVISIBLE
        }else{
            binding.ivContactos.visibility=View.VISIBLE
        }

    }

    private fun setListeners() {
        binding.fabAdd.setOnClickListener{
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    override fun onRestart() {
        super.onRestart()
        setRecycler()
    }
    /////////////////////Menu principal
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_salir->{
                finish()
            }
            R.id.item_borrar_todo->{
                confirmarBorrado()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmarBorrado(){
        val builder=AlertDialog.Builder(this)
            .setTitle("¿Borrar Agenda?")
            .setMessage("¿Borrar todos los contactos?")
            .setNegativeButton("CANCELAR"){
                dialog,_->dialog.dismiss()
            }
            .setPositiveButton("ACEPTAR"){
                _,_->
                CrudContactos().borrarTodo()
                setRecycler()
            }
            .create()
            .show()










    }
}