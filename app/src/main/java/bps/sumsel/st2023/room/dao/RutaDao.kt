package bps.sumsel.st2023.room.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import bps.sumsel.st2023.room.entity.RekapRutaEntity
import bps.sumsel.st2023.room.entity.RutaEntity

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

    @Query("SELECT * from ruta WHERE status_upload != 1 ORDER BY id ASC")
    fun findAllToUpload(): List<RutaEntity>

    @Query(
        "SELECT * from ruta " +
                "WHERE kode_prov=:kode_prov" +
                " AND kode_kab=:kode_kab" +
                " AND kode_kec=:kode_kec" +
                " AND kode_desa=:kode_desa" +
                " AND id_sls=:id_sls" +
                " AND id_sub_sls=:id_sub_sls" +
                " ORDER BY id ASC"
    )
    fun findBySls(
        kode_prov: String,
        kode_kab: String,
        kode_kec: String,
        kode_desa: String,
        id_sls: String,
        id_sub_sls: String
    ): List<RutaEntity>

    @RawQuery(observedEntities = [RutaEntity::class])
    fun findWithCondition(query: SupportSQLiteQuery): List<RutaEntity>

    @Query(
        "SELECT * from ruta " +
                "WHERE kode_prov=:kode_prov" +
                " AND kode_kab=:kode_kab" +
                " AND kode_kec=:kode_kec" +
                " AND kode_desa=:kode_desa" +
                " AND id_sls=:id_sls" +
                " AND id_sub_sls=:id_sub_sls" +
                " AND nurt=:nurt" +
                " ORDER BY id ASC LIMIT 1"
    )
    fun findDetail(
        kode_prov: String,
        kode_kab: String,
        kode_kec: String,
        kode_desa: String,
        id_sls: String,
        id_sub_sls: String,
        nurt: Int
    ): RutaEntity

    @Query("SELECT COUNT(status_upload) AS jumlah, kode_prov, kode_kab, kode_kec, kode_desa, id_sls, id_sub_sls FROM ruta WHERE status_upload = 1 GROUP BY kode_prov, kode_kab, kode_kec, kode_desa, id_sls, id_sub_sls")
    fun rekapRuta(): List<RekapRutaEntity>
}