package bps.sumsel.st2023.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import bps.sumsel.st2023.api.ApiInterface
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.response.ResponseSls
import bps.sumsel.st2023.response.ResponseSlsPetugas
import bps.sumsel.st2023.response.ResponseStringData
import bps.sumsel.st2023.room.dao.SlsDao
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
                    val slsList = sls?.map { curData ->
                        SlsEntity(
                            curData.id ?: 0,
                            curData.kodeProv ?: "",
                            curData.kodeKab ?: "",
                            curData.kodeKec ?: "",
                            curData.kodeDesa ?: "",
                            curData.idSls ?: "",
                            curData.idSubSls ?: "",
                            curData.namaSls ?: "",
                            curData.slsOp ?: 0,
                            curData.jenisSls ?: 0,
                            curData.jmlArtTani ?: 0,
                            curData.jmlKeluargaTani ?: 0,
                            curData.sektor1 ?: 0,
                            curData.sektor2 ?: 0,
                            curData.sektor3 ?: 0,
                            curData.sektor4 ?: 0,
                            curData.sektor5 ?: 0,
                            curData.sektor6 ?: 0,
                            curData.jmlKeluargaTaniSt2023 ?: 0,
                            curData.jmlNr ?: 0,
                            curData.jmlDokKePml ?: 0,
                            curData.jmlDokKeKoseka ?: 0,
                            curData.jmlDokKeBps ?: 0,
                            curData.statusSelesaiPcl ?: 0,
                            curData.kodePcl ?: "",
                            curData.kodePml ?: "",
                            curData.kodeKoseka ?: "",
                            curData.statusSls ?: 0
                        )
                    }

                    runBlocking {
                        slsDao.deleteAll()
                        slsDao.insert(slsList!!)

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
        val localData = slsDao.findAll()
        _resultData.value = ResultData.Success(localData)
    }

    companion object {
        @Volatile
        private var instance: SlsRepository? = null
        fun getInstance(
            apiService: ApiInterface,
            slsDao: SlsDao,
            pref: AuthDataStore
        ): SlsRepository =
            instance ?: synchronized(this) {
                instance ?: SlsRepository(apiService, slsDao, pref)
            }.also { instance = it }
    }
}