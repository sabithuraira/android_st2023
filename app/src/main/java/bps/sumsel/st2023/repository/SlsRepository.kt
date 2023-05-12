package bps.sumsel.st2023.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Query
import androidx.sqlite.db.SimpleSQLiteQuery
import bps.sumsel.st2023.api.ApiInterface
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.enum.EnumStatusUpload
import bps.sumsel.st2023.request.RequestRuta
import bps.sumsel.st2023.request.RequestRutaMany
import bps.sumsel.st2023.response.ResponseSlsPetugas
import bps.sumsel.st2023.response.ResponseStringData
import bps.sumsel.st2023.response.ResponseStringStatus
import bps.sumsel.st2023.room.dao.RutaDao
import bps.sumsel.st2023.room.dao.SlsDao
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlsRepository private constructor(
    private val apiService: ApiInterface,
    private val slsDao: SlsDao,
    private val rutaDao: RutaDao,
    private val pref: AuthDataStore
) {
    private val _resultData = MutableLiveData<ResultData<List<SlsEntity>?>>()
    val resultData: LiveData<ResultData<List<SlsEntity>?>> = _resultData

    fun syncSls() {
        val dataUser = runBlocking { pref.getUser().first() }

        _resultData.value = ResultData.Loading

        val client = apiService.listSlsPetugas(
            dataUser.jabatan,
            dataUser.user
        )

        client.enqueue(object : Callback<ResponseSlsPetugas> {
            override fun onResponse(
                call: Call<ResponseSlsPetugas>,
                response: Response<ResponseSlsPetugas>
            ) {
                if (response.isSuccessful) {
                    val sls = response.body()?.datas

                    var slsList = mutableListOf<SlsEntity>()
                    var rutasList = mutableListOf<RutaEntity>()

                    sls?.let {
                        for (item in sls) {
                            slsList.add(
                                SlsEntity(
                                    item.id ?: 0,
                                    item.encId ?: "",
                                    item.kodeProv ?: "",
                                    item.kodeKab ?: "",
                                    item.kodeKec ?: "",
                                    item.kodeDesa ?: "",
                                    item.idSls ?: "",
                                    item.idSubSls ?: "",
                                    item.namaSls ?: "",
                                    item.slsOp ?: 0,
                                    item.jenisSls ?: 0,
                                    item.jmlArtTani ?: 0,
                                    item.jmlKeluargaTani ?: 0,
                                    item.sektor1 ?: 0,
                                    item.sektor2 ?: 0,
                                    item.sektor3 ?: 0,
                                    item.sektor4 ?: 0,
                                    item.sektor5 ?: 0,
                                    item.sektor6 ?: 0,
                                    item.jmlKeluargaTaniSt2023 ?: 0,
                                    item.jmlNr ?: 0,
                                    item.jmlDokKePml ?: 0,
                                    item.jmlDokKeKoseka ?: 0,
                                    item.jmlDokKeBps ?: 0,
                                    item.statusSelesaiPcl ?: 0,
                                    item.kodePcl ?: "",
                                    item.kodePml ?: "",
                                    item.kodeKoseka ?: "",
                                    item.statusSls ?: 0
                                )
                            )

                            val rutas = item.daftar_ruta
                            rutas?.let {
                                for (itemRuta in rutas) {
                                    val ruta = RutaEntity(
                                        itemRuta.id ?: 0,
                                        itemRuta.encId ?: "",
                                        itemRuta.kodeProv ?: "",
                                        itemRuta.kodeKab ?: "",
                                        itemRuta.kodeKec ?: "",
                                        itemRuta.kodeDesa ?: "",
                                        itemRuta.idSls ?: "",
                                        itemRuta.idSubSls ?: "",

                                        itemRuta.nurt ?: 0,
                                        itemRuta.kepalaRuta ?: "",
                                        itemRuta.jumlahArt ?: 0,
                                        itemRuta.jumlahUnitUsaha ?: 0,

                                        itemRuta.subsektor1A ?: 0,
                                        itemRuta.subsektor1B ?: 0,
                                        itemRuta.subsektor2A ?: 0,
                                        itemRuta.subsektor2B ?: 0,
                                        itemRuta.subsektor3A ?: 0,
                                        itemRuta.subsektor3B ?: 0,
                                        itemRuta.subsektor4A ?: 0,
                                        itemRuta.subsektor4B ?: 0,
                                        itemRuta.subsektor4C ?: 0,
                                        itemRuta.subsektor5A ?: 0,
                                        itemRuta.subsektor5B ?: 0,
                                        itemRuta.subsektor5C ?: 0,
                                        itemRuta.subsektor6A ?: 0,
                                        itemRuta.subsektor6B ?: 0,
                                        itemRuta.subsektor6C ?: 0,
                                        itemRuta.subsektor7A ?: 0,

                                        itemRuta.jml308Sawah ?: 0,
                                        itemRuta.jml308BukanSawah ?: 0,
                                        itemRuta.jml308RumputSementara ?: 0,
                                        itemRuta.jml308RumputPermanen ?: 0,
                                        itemRuta.jml308BelumTanam ?: 0,
                                        itemRuta.jml308TernakBangunanLain ?: 0,
                                        itemRuta.jml308Kehutanan ?: 0,
                                        itemRuta.jml308Budidaya ?: 0,
                                        itemRuta.jml308LahanLainnya ?: 0,

                                        itemRuta.daftarKomoditas ?: "",

                                        itemRuta.startTime ?: "",
                                        itemRuta.endTime ?: "",
                                        itemRuta.startLatitude ?: 0.0,
                                        itemRuta.endLatitude ?: 0.0,
                                        itemRuta.startLongitude ?: 0.0,
                                        itemRuta.endLongitude ?: 0.0,

                                        EnumStatusUpload.UPLOADED.kode,

                                        itemRuta.createdBy ?: 0,
                                        itemRuta.updatedBy ?: 0,
                                        itemRuta.createdAt ?: "",
                                        itemRuta.updatedAt ?: "",
                                    )
                                    rutasList.add(ruta)
                                }
                            }
                        }
                    }

                    runBlocking {
                        slsDao.deleteAll()
                        slsDao.insert(slsList)
                        rutaDao.deleteAll()
                        rutaDao.insert(rutasList)

                        CoroutineScope(Dispatchers.IO).launch {
                            val localData = slsDao.findAll()
                            _resultData.postValue(ResultData.Success(localData))
                        }
                    }
                } else {
                    val errorMessage: ResponseStringData = Gson().fromJson(
                        response.errorBody()!!.charStream(),
                        ResponseStringData::class.java
                    )
                    _resultData.value = ResultData.Error(errorMessage.datas ?: "")
                }
            }

            override fun onFailure(call: Call<ResponseSlsPetugas>, t: Throwable) {
                _resultData.value = ResultData.Error(t.message.toString())
            }
        })
    }

    fun getSls() {
        CoroutineScope(Dispatchers.IO).launch {
            val localData = slsDao.findAll()
            _resultData.postValue(ResultData.Success(localData))
        }
    }

    private val _resultSingleData = MutableLiveData<ResultData<SlsEntity?>>()
    val resultSingleData: LiveData<ResultData<SlsEntity?>> = _resultSingleData

    fun setSingleData(data: SlsEntity?) {
        _resultSingleData.postValue(ResultData.Success(data))
    }

    fun updateSls(data: SlsEntity) {
        _resultData.value = ResultData.Loading

        CoroutineScope(Dispatchers.IO).launch {
            slsDao.update(data)

            val localData = slsDao.findAll()

            _resultData.postValue(ResultData.Success(localData))
        }
    }

    private val _resultDataRuta = MutableLiveData<ResultData<List<RutaEntity>?>>()
    val resultDataRuta: LiveData<ResultData<List<RutaEntity>?>> = _resultDataRuta
    fun getRuta(data: SlsEntity, keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val query = StringBuilder().append("SELECT * from ruta ")
            query.append(" WHERE kode_prov='${data.kode_prov}'")
            query.append(" AND kode_kab='${data.kode_kab}'")
            query.append(" AND kode_kec='${data.kode_kec}'")
            query.append(" AND kode_desa='${data.kode_desa}'")
            query.append(" AND id_sls='${data.id_sls}'")
            query.append(" AND id_sub_sls='${data.id_sub_sls}'")

            if(keyword.isNotEmpty()){
                query.append(" AND (kepala_ruta LIKE '%$keyword%'")
                query.append(" OR nurt LIKE '%$keyword%')")
            }

            query.append(" ORDER BY id ASC")

            val sqlQuery = SimpleSQLiteQuery(query.toString())

            val localData = rutaDao.findWithCondition(sqlQuery)

            _resultDataRuta.postValue(ResultData.Success(localData))
        }
    }

    private val _resultSingleRuta = MutableLiveData<ResultData<RutaEntity?>>()
    val resultSingleRuta: LiveData<ResultData<RutaEntity?>> = _resultSingleRuta

    fun setSingleRuta(data: RutaEntity?) {
        _resultSingleRuta.postValue(ResultData.Success(data))
    }

    fun updateRuta(data: RutaEntity, isFinish: Boolean) {
        _resultSingleRuta.value = ResultData.Loading

        CoroutineScope(Dispatchers.IO).launch {
            if (data.id == 0) rutaDao.insert(listOf(data))
            else rutaDao.update(data)

            val localData = rutaDao.findDetail(
                data.kode_prov, data.kode_kab, data.kode_kec, data.kode_desa,
                data.id_sls, data.id_sub_sls, data.nurt
            )

            if (isFinish) _resultSingleRuta.postValue(ResultData.Success(null))
            else _resultSingleRuta.postValue(ResultData.Success(localData))
        }
    }

    fun deleteRuta(data: RutaEntity) {
        _resultSingleRuta.value = ResultData.Loading

        CoroutineScope(Dispatchers.IO).launch {
            rutaDao.delete(data)
            _resultSingleRuta.postValue(ResultData.Success(null))
        }
    }

    private val _resultUpload = MutableLiveData<ResultData<String>>()
    val resultUpload: LiveData<ResultData<String>> = _resultUpload
    fun storeRutaMany() {
        _resultUpload.value = ResultData.Loading

        CoroutineScope(Dispatchers.IO).launch {
            val rutaToUpload = rutaDao.findAllToUpload()

            val rutaList = mutableListOf<RequestRuta>()

            rutaToUpload.forEach {
                val requestRuta = RequestRuta(
                    it.encId,
                    it.kode_prov,
                    it.kode_kab,
                    it.kode_kec,
                    it.kode_desa,
                    it.id_sls,
                    it.id_sub_sls,
                    it.nurt,
                    it.kepala_ruta,
                    it.jumlah_art,
                    it.jumlah_unit_usaha,
                    it.subsektor1_a,
                    it.subsektor1_b,
                    it.subsektor2_a,
                    it.subsektor2_b,
                    it.subsektor3_a,
                    it.subsektor3_b,
                    it.subsektor4_a,
                    it.subsektor4_b,
                    it.subsektor4_c,
                    it.subsektor5_a,
                    it.subsektor5_b,
                    it.subsektor5_c,
                    it.subsektor6_a,
                    it.subsektor6_b,
                    it.subsektor6_c,
                    it.subsektor7_a,
                    it.jml_308_sawah,
                    it.jml_308_bukan_sawah,
                    it.jml_308_rumput_sementara,
                    it.jml_308_rumput_permanen,
                    it.jml_308_belum_tanam,
                    it.jml_308_ternak_bangunan_lain,
                    it.jml_308_kehutanan,
                    it.jml_308_budidaya,
                    it.jml_308_lahan_lainnya,
                    it.daftar_komoditas,
                    it.start_time,
                    it.end_time,
                    it.start_latitude,
                    it.end_latitude,
                    it.start_longitude,
                    it.end_longitude,
                    it.status_upload
                )

                rutaList.add(requestRuta)
            }

            val requestRutaMany = RequestRutaMany(rutaList)

            val dataUser = runBlocking { pref.getUser().first() }

            val client = apiService.storeRutaMany("Bearer " + dataUser.token, requestRutaMany)

            client.enqueue(object : Callback<ResponseStringStatus> {
                override fun onResponse(
                    call: Call<ResponseStringStatus>,
                    response: Response<ResponseStringStatus>
                ) {
                    if (response.isSuccessful) {
                        runBlocking { syncSls() }

                        _resultUpload.postValue(ResultData.Success("success"))
                    }
                }

                override fun onFailure(call: Call<ResponseStringStatus>, t: Throwable) {
                    _resultUpload.value = ResultData.Error(t.message.toString())
                }
            })
        }
    }

    companion object {
        @Volatile
        private var instance: SlsRepository? = null
        fun getInstance(
            apiService: ApiInterface,
            slsDao: SlsDao,
            rutaDao: RutaDao,
            pref: AuthDataStore
        ): SlsRepository =
            instance ?: synchronized(this) {
                instance ?: SlsRepository(apiService, slsDao, rutaDao, pref)
            }.also { instance = it }
    }
}