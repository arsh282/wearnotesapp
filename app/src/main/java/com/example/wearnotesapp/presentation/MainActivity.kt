package com.example.wearnotesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : ComponentActivity() {
    private val fileName = "notes.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesApp()
        }
    }

    @Composable
    fun NotesApp() {
        var notes by remember { mutableStateOf(loadNotes()) }
        var newNote by remember { mutableStateOf("") }

        fun saveNotesToFile(updatedNotes: List<String>) {
            val json = Gson().toJson(updatedNotes)
            File(filesDir, fileName).writeText(json)
        }

        fun addNote(note: String) {
            if (note.isNotBlank()) {
                val updatedNotes = notes + note
                notes = updatedNotes
                saveNotesToFile(updatedNotes)
                newNote = ""
            }
        }

        fun deleteNote(index: Int) {
            val updatedNotes = notes.toMutableList().apply { removeAt(index) }
            notes = updatedNotes
            saveNotesToFile(updatedNotes)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(), // Important for Wear Compose layouts
            topBar = {
                Text("Wear Notes", modifier = Modifier.padding(8.dp))
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BasicTextField(
                        value = newNote,
                        onValueChange = { newNote = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                    Button(onClick = { addNote(newNote) }, modifier = Modifier.padding(4.dp)) {
                        Text("Add Note")
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(notes.size) { index ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically // Added for better alignment
                            ) {
                                Text(notes[index])
                                Button(onClick = { deleteNote(index) }) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    private fun loadNotes(): List<String> {
        val file = File(filesDir, fileName)
        return if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
}