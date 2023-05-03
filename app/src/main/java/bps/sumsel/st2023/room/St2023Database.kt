package bps.sumsel.st2023.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import bps.sumsel.st2023.room.dao.RutaDao
import bps.sumsel.st2023.room.dao.SlsDao
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity


@Database(
    entities = [SlsEntity::class, RutaEntity::class],
    version = 2,
)
abstract class St2023Database: RoomDatabase() {
    abstract fun slsDao(): SlsDao
    abstract fun rutasDao(): RutaDao

    companion object {
        @Volatile
        private var instance: St2023Database? = null
        fun getInstance(context: Context): St2023Database =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    St2023Database::class.java, "st2023.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
    }
}

//val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL(
//            "ALTER TABLE ruta "
//                    + " ADD COLUMN is_upload INTEGER"
//        )
//    }
//}