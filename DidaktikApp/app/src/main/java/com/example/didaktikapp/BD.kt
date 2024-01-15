package com.example.didaktikapp

import android.content.Context
import android.content.SharedPreferences
import androidx.room.*

@Entity(tableName = "partida_table")
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val juego: String,
    val pantalla: Int,
    val tiempo: Float
)

@Dao
interface PartidaDao : BaseDao<Partida> {
    @Query("SELECT * FROM partida_table WHERE id = :id")
    fun get(id: Int): Partida

    @Query("SELECT * FROM partida_table")
    fun getAll(): List<Partida>

    @Query("SELECT id FROM partida_table")
    fun getAllIds(): List<Int>
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


@Database(entities = [Partida::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun partidaDao(): PartidaDao
}

class AppDatabaseInitializer {
    companion object {
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}

class  BDManager{
    companion object{
        var context: Context? = null

        fun Iniciar(funcion: (PartidaDao, SharedPreferences) -> Unit)
        {
            if(context == null) throw Exception("No se ha inicializado el BDManager")

            // Inicializar la BD
            val appDatabase = AppDatabaseInitializer.getDatabase(context!!)
            val partidaDao = appDatabase.partidaDao()

            // Inicializar SharedPreferences
            val sharedPreferences = context!!.applicationContext.getSharedPreferences("NombrePreferencia", Context.MODE_PRIVATE)
//            var partida_id = sharedPreferences.getInt("partida_id", -1)

            funcion.invoke(partidaDao, sharedPreferences)
        }
    }
}