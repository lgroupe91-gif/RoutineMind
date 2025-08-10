
package com.routinemind.clean

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

data class Task(val text: String, var done: Boolean)
data class Routine(val id: Long, val name: String, val category: String, val time: String, val tasks: MutableList<Task>)

class RoutineAdapter(private val items: MutableList<Routine>, private val onClick: (Routine)->Unit) :
    RecyclerView.Adapter<RoutineAdapter.VH>() {
    class VH(v: android.view.View) : RecyclerView.ViewHolder(v) {
        val t1: TextView = v.findViewById(R.id.item_title)
        val t2: TextView = v.findViewById(R.id.item_sub)
    }
    override fun onCreateViewHolder(p: android.view.ViewGroup, vt: Int): VH {
        val v = android.view.LayoutInflater.from(p.context).inflate(R.layout.item_routine, p, false)
        return VH(v)
    }
    override fun onBindViewHolder(h: VH, i: Int) {
        val r = items[i]
        h.t1.text = r.name
        h.t2.text = r.category + " • " + r.time
        h.itemView.setOnClickListener { onClick(r) }
    }
    override fun getItemCount() = items.size
}

class MainActivity : AppCompatActivity() {
    private val quotes = listOf(
        "Discipline first, freedom follows.","Chaque jour, une petite victoire.","Le futur se construit aujourd’hui, pas demain.",
        "Tu n’as pas besoin d’être parfaite, juste constante.","Tiny steps, big results.","Agis comme la femme que tu veux devenir."
    )
    private val routines = mutableListOf<Routine>()
    private lateinit var adapter: RoutineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val quote: TextView = findViewById(R.id.txt_quote)
        quote.text = quotes[Random.nextInt(quotes.size)]

        val nameEt: EditText = findViewById(R.id.et_name)
        val timeEt: EditText = findViewById(R.id.et_time)
        val catSp: Spinner = findViewById(R.id.sp_cat)
        val taskEt: EditText = findViewById(R.id.et_task)
        val addTaskBtn: Button = findViewById(R.id.btn_add_task)
        val tasksTv: TextView = findViewById(R.id.txt_tasks)
        val saveBtn: Button = findViewById(R.id.btn_save)
        val musicBtn: Button = findViewById(R.id.btn_music)
        val musicPickBtn: Button = findViewById(R.id.btn_pick)
        val rv: RecyclerView = findViewById(R.id.rv)

        catSp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Matin","Soir","Autre"))

        val tmpTasks = mutableListOf<String>()
        addTaskBtn.setOnClickListener {
            val t = taskEt.text.toString().trim()
            if (t.isNotEmpty()) {
                tmpTasks.add(t)
                taskEt.text.clear()
                tasksTv.text = tmpTasks.joinToString(" • ")
            }
        }

        saveBtn.setOnClickListener {
            val name = nameEt.text.toString().trim()
            val time = timeEt.text.toString().ifBlank { "07:00" }
            val cat = catSp.selectedItem.toString()
            if (name.isEmpty()) {
                Toast.makeText(this, "Nom requis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val r = Routine(System.currentTimeMillis(), name, cat, time, tmpTasks.map { Task(it, false) }.toMutableList())
            routines.add(r)
            tmpTasks.clear(); tasksTv.text = ""; nameEt.text.clear(); timeEt.setText("07:00")
            adapter.notifyDataSetChanged()
        }

        musicBtn.setOnClickListener {
            openUrl("https://open.spotify.com/playlist/37i9dQZF1DX8FwnYE6PRvL")
        }
        musicPickBtn.setOnClickListener {
            val urls = arrayOf(
                "https://open.spotify.com/playlist/37i9dQZF1DX8FwnYE6PRvL",
                "https://open.spotify.com/playlist/37i9dQZF1DXdxcBWuJkbcy"
            )
            android.app.AlertDialog.Builder(this)
                .setTitle("Choisir un titre — Motivation")
                .setItems(urls) { _, which -> openUrl(urls[which]) }
                .show()
        }

        adapter = RoutineAdapter(routines) { r -> Toast.makeText(this, r.name, Toast.LENGTH_SHORT).show() }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun openUrl(u: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u)))
    }
}
