package bps.sumsel.st2023.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity

@Dao
interface RutaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: List<RutaEntity>)

    @Update
    suspend fun update(data: RutaEntity)

    @Delete
    suspend fun delete(data: RutaEntity)

    @Query("DELETE FROM ruta")
    suspend fun deleteAll()

    @Query("SELECT * from ruta ORDER BY id ASC")
    fun findAll(): List<RutaEntity>

    @Query("SELECT * from ruta " +
            "WHERE kode_prov=:kode_prov" +
            " AND kode_kab=:kode_kab" +
            " AND kode_kec=:kode_kec" +
            " AND kode_desa=:kode_desa" +
            " AND id_sls=:id_sls" +
            " AND id_sub_sls=:id_sub_sls" +
            " ORDER BY id ASC")
    fun findBySls(kode_prov: String,
                  kode_kab: String,
                  kode_kec: String,
                  kode_desa: String,
                  id_sls: String,
                  id_sub_sls: String, ): List<RutaEntity>
}