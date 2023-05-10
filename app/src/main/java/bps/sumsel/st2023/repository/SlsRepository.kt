package bps.sumsel.st2023.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.room.ColumnInfo
import bps.sumsel.st2023.api.ApiInterface
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.enum.EnumStatusUpload
import bps.sumsel.st2023.response.ResponseSls
import bps.sumsel.st2023.response.ResponseSlsPetugas
import bps.sumsel.st2023.response.ResponseStringData
import bps.sumsel.st2023.room.dao.RutaDao
import bps.sumsel.st2023.room.dao.SlsDao
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlsRepository  private constructor(
    private val apiService: ApiInterface,
    private val slsDao: SlsDao,
    private val rutaDao: RutaDao,
    private val pref: AuthDataStore
) {
    private val _resultData = MutableLiveData<ResultData<List<SlsEntity>?>>()
    val resultData: LiveData<ResultData<List<SlsEntity>?>> = _resultData

    fun syncSls(){
        val dataUser = runBlocking { pref.getUser().first()  }

        _resultData.value = ResultData.Loading

        val client = apiService.listSlsPetugas(
            dataUser!!.jabatan,
            dataUser!!.user
        )

        client.enqueue(object : Callback<ResponseSlsPetugas> {
            override fun onResponse(call: Call<ResponseSlsPetugas>, response: Response<ResponseSlsPetugas>) {
                if (response.isSuccessful) {
                    val sls = response.body()?.datas

                    var slsList = mutableListOf<SlsEntity>()
                    var rutasList = mutableListOf<RutaEntity>()

                    sls?.let{
                        for(item in sls){
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
                                for(itemRuta in rutas){
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
                        slsDao.insert(slsList!!)
                        rutaDao.deleteAll()
                        rutaDao.insert(rutasList)

                        CoroutineScope(Dispatchers.IO).launch {
                            val localData = slsDao.findAll()
                            _resultData.postValue(ResultData.Success(localData))
                        }
                    }
                }
                else{
                    val errorMessage: ResponseStringData = Gson().fromJson(response.errorBody()!!.charStream(), ResponseStringData::class.java)
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

    private val _resultDataRuta = MutableLiveData<ResultData<List<RutaEntity>?>>()
    val resultDataRuta: LiveData<ResultData<List<RutaEntity>?>> = _resultDataRuta
    fun getRuta(data: SlsEntity){
        CoroutineScope(Dispatchers.IO).launch {
            val localData = rutaDao.findBySls(
                data.kode_prov, data.kode_kab,
                data.kode_kec, data.kode_desa,
                data.id_sls, data.id_sub_sls
            )

            _resultDataRuta.postValue(ResultData.Success(localData))
        }
    }

    private val _resultSingleRuta = MutableLiveData<ResultData<RutaEntity?>>()
    val resultSingleRuta: LiveData<ResultData<RutaEntity?>> = _resultSingleRuta

    fun setSingleRuta(data: RutaEntity?){
        _resultSingleRuta.postValue(ResultData.Success(data))
    }

    fun updateRuta(data: RutaEntity, isFinish: Boolean){
        _resultSingleRuta.value = ResultData.Loading

        CoroutineScope(Dispatchers.IO).launch {
            if(data.id==0) rutaDao.insert(listOf(data))
            else rutaDao.update(data)

            val localData = rutaDao.findDetail(
                data.kode_prov, data.kode_kab, data.kode_kec, data.kode_desa,
                data.id_sls, data.id_sub_sls, data.nurt
            )

            if(isFinish)_resultSingleRuta.postValue(ResultData.Success(null))
            else _resultSingleRuta.postValue(ResultData.Success(localData))
        }
    }

    fun deleteRuta(data: RutaEntity){
        _resultSingleRuta.value = ResultData.Loading

        CoroutineScope(Dispatchers.IO).launch {
            rutaDao.delete(data)
            _resultSingleRuta.postValue(ResultData.Success(null))
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