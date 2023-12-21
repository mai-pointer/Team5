package com.example.didaktikapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//CLASES------------------

@Entity
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var juego: String,
    var pantalla: Int
)

//DAO------------------

@Dao
interface PartidaDao : BaseDao<Partida> {
    @Query("SELECT * FROM Partida")
    fun getAll(): List<Partida>

    @Query("SELECT * FROM Partida Where id = :id")
    fun get(id: Int): Partida
}

interface BaseDao<T> {
    //BASICOS
    @Insert
    fun insert(element: T)

    @Insert
    fun insertAll(elements: List<T>)

    @Update
    fun update(element: T)

    @Update
    fun updateAll(elements: List<T>)

    @Delete
    fun delete(element: T)

    @Delete
    fun deleteAll(elements: List<T>)
}

//BD------------------

@Database(entities = [Partida::class], version = 2)
abstract class BD : RoomDatabase() {
    abstract fun PartidaDao(): PartidaDao
}

class MyApp : Application() {
    val database by lazy {
        Room.databaseBuilder(this, BD::class.java, "bd").fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }
}

//FUNCIONES------------------
class BDManager {
    companion object {
        var coroutineScope: CoroutineScope? = null
        var context: Context? = null
        var database: BD? = null

        fun inicializar(context: Context, coroutineScope: CoroutineScope, database: BD) {
            this.context = context
            this.coroutineScope = coroutineScope
            this.database = database
        }

        fun partida(
            callback: (sharedPreferences: SharedPreferences, partidaDao: PartidaDao) -> Unit
        ) {
            if (context == null || coroutineScope == null || database == null) {
                throw Exception("BDManager no inicializado")
                return
            }
            coroutineScope!!.launch{
                try {
                    val partidaBD = database!!.PartidaDao()
                    val sharedPreferences =
                        context!!.getSharedPreferences("Lezama", Context.MODE_PRIVATE)

                    callback(sharedPreferences, partidaBD)

                } catch (e: Exception) {
                    Log.e("room", "Error: ${e.message}")
                }
            }
        }
    }
}
