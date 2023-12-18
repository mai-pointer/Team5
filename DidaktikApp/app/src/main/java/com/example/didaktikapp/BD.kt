package com.gernika.roomejemplo

import android.app.Application
import androidx.lifecycle.LiveData
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

//CLASES------------------

@Entity
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val juego: String,
    val pantalla: Int
)

//DAO------------------

@Dao
interface ProfesorDao : BaseDao<Partida> {
    @Query("SELECT * FROM Partida")
    fun getAll(): List<Partida>
}

@Dao
interface AsignaturaDao : BaseDao<Partida> {
    @Query("SELECT * FROM Partida")
    fun getAll(): List<Partida>
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

@Database(entities = [Partida::class], version = 1)
abstract class BD : RoomDatabase() {
    abstract fun profesorDao(): ProfesorDao
    abstract fun asignaturaDao(): AsignaturaDao
}

class MyApp : Application() {
    val database by lazy {
        Room.databaseBuilder(this, BD::class.java, "bd").fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }
}
