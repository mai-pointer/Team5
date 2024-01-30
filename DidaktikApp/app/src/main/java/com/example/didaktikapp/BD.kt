package com.example.didaktikapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.*

@Entity(tableName = "partida_table")
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val juego: String,
    val pantalla: Int,
    val juegoMapa: Int,
    val hj: Boolean
)

@Entity(
    tableName = "competitivo_table",
    foreignKeys = [
        ForeignKey(
            entity = Partida::class,
            parentColumns = ["id"],
            childColumns = ["partidaId"],
            onDelete = ForeignKey.CASCADE // Esto activa la eliminación en cascada
        )
    ]
)
data class Competitivo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tiempo: Long,
    val partidaId: Int
)

data class Relacion(
    @Embedded val partida: Partida,
    @Relation(
        parentColumn = "id",
        entityColumn = "partidaId"
    )
    val competitivos: List<Competitivo>
)

@Dao
interface PartidaDao : BaseDao<Partida> {
    @Query("SELECT * FROM partida_table WHERE id = :id")
    fun get(id: Int): Partida

    @Query("SELECT * FROM partida_table")
    fun getAll(): List<Partida>

    @Query("SELECT id FROM partida_table")
    fun getAllIds(): List<Int>

    // Query para obtener todos los tiempos relacionados con una partida
    @Query("SELECT * FROM competitivo_table WHERE partidaId = :partidaId")
    fun getTiempos(partidaId: Int): List<Competitivo>
}

@Dao
interface CompetitivoDao : BaseDao<Competitivo>

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


@Database(entities = [Partida::class, Competitivo::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun partidaDao(): PartidaDao
    abstract fun competitivoDao(): CompetitivoDao
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
                ).fallbackToDestructiveMigration().build()
                instance = newInstance
                newInstance
            }
        }
    }
}

class  BDManager{
    companion object{
        //var context: Context? = null
        fun Iniciar( context: Context, funcion: (PartidaDao, CompetitivoDao , SharedPreferences) -> Unit)
        {
            if(context == null) Log.d("BDManager", "Contexto nulo")

            Log.d("BDManager", context.toString())

            // Inicializar la BD
            val appDatabase = AppDatabaseInitializer.getDatabase(context!!)
            val partidaDao = appDatabase.partidaDao()
            val competitivoDao = appDatabase.competitivoDao()

            // Inicializar SharedPreferences
            val sharedPreferences = context!!.applicationContext.getSharedPreferences("NombrePreferencia", Context.MODE_PRIVATE)
//            var partida_id = sharedPreferences.getInt("partida_id", -1)

            funcion.invoke(partidaDao, competitivoDao, sharedPreferences)

        }
    }
}