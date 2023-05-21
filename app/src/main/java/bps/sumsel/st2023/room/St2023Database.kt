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
    version = 4,
//    autoMigrations = [
//        AutoMigration(from = 4, to = 5),
//    ],
//    exportSchema = true
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
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .addMigrations(MIGRATION_3_4)
                .build()
            }
    }
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE ruta "
                    + " ADD COLUMN apakah_menggunakan_lahan INTEGER NOT NULL DEFAULT(0)"
        )
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE sls "
                    + " ADD COLUMN nama_desa VARCHAR(150) NOT NULL DEFAULT ''"
        )
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE sls "
                    + " ADD COLUMN nama_kec VARCHAR(150) NOT NULL DEFAULT ''"
        )
    }
}