package bps.sumsel.st2023.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import bps.sumsel.st2023.api.ApiInterface
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.response.ResponseSls
import bps.sumsel.st2023.room.dao.SlsDao
import bps.sumsel.st2023.room.entity.SlsEntity
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlsRepository  private constructor(
    private val apiService: ApiInterface,
    private val slsDao: SlsDao
) {
//    private val resultData = MediatorLiveData<ResultData<List<SlsEntity>>>()
    private val _resultData = MutableLiveData<ResultData<List<SlsEntity>?>>()
    val resultData: LiveData<ResultData<List<SlsEntity>?>> = _resultData

//    fun syncSls(): LiveData<ResultData<List<SlsEntity>>>{
//        resultData.value = ResultData.Loading
//        val client = apiService.listSls()
//
//        client.enqueue(object : Callback<ResponseSls> {
//            override fun onResponse(call: Call<ResponseSls>, response: Response<ResponseSls>) {
//                if (response.isSuccessful) {
//                    val sls = response.body()?.datas?.data
//                    val slsList = sls?.map { curData ->
//                        SlsEntity(
//                            curData.id ?: 0,
//                            curData.kodeProv ?: "",
//                            curData.kodeKab ?: "",
//                            curData.kodeKec ?: "",
//                            curData.kodeDesa ?: "",
//                            curData.idSls ?: "",
//                            curData.idSubSls ?: "",
//                            curData.namaSls ?: "",
//                            curData.slsOp ?: 0,
//                            curData.jenisSls ?: 0,
//                            curData.jmlArtTani ?: 0,
//                            curData.jmlKeluargaTani ?: 0,
//                            curData.sektor1 ?: 0,
//                            curData.sektor2 ?: 0,
//                            curData.sektor3 ?: 0,
//                            curData.sektor4 ?: 0,
//                            curData.sektor5 ?: 0,
//                            curData.sektor6 ?: 0,
//                            curData.jmlKeluargaTaniSt2023 ?: 0,
//                            curData.jmlNr ?: 0,
//                            curData.jmlDokKePml ?: 0,
//                            curData.jmlDokKeKoseka ?: 0,
//                            curData.jmlDokKeBps ?: 0,
//                            curData.statusSelesaiPcl ?: 0,
//                            curData.kodePcl ?: "",
//                            curData.kodePml ?: "",
//                            curData.kodeKoseka ?: "",
//                            curData.statusSls ?: 0
//                        )
//                    }
//
//                    runBlocking {
//                        slsDao.deleteAll()
//                        slsDao.insert(slsList!!)
//
//                        val localData = slsDao.findAll()
//                        resultData.addSource(localData) { newData: List<SlsEntity> ->
//                            resultData.value = ResultData.Success(newData)
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseSls>, t: Throwable) {
//                resultData.value = ResultData.Error(t.message.toString())
//            }
//        })
//
//        return resultData
//    }

//    fun getSls(): LiveData<ResultData<List<SlsEntity>>> {
//        val localData = slsDao.findAll()
//        _resultData.value = ResultData.Success(localData)
////        resultData.addSource(localData) { newData: List<SlsEntity> ->
////            resultData.value = ResultData.Success(newData)
////        }
////        return resultData
//    }

    fun syncSls(){
        _resultData.value = ResultData.Loading
        val client = apiService.listSls()

        client.enqueue(object : Callback<ResponseSls> {
            override fun onResponse(call: Call<ResponseSls>, response: Response<ResponseSls>) {
                if (response.isSuccessful) {
                    val sls = response.body()?.datas?.data
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

                        val localData = slsDao.findAll()
                        _resultData.value = ResultData.Success(localData.value)
//                        _resultData.addSource(localData) { newData: List<SlsEntity> ->
//                            _resultData.value = ResultData.Success(newData)
//                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSls>, t: Throwable) {
                _resultData.value = ResultData.Error(t.message.toString())
            }
        })
    }

    fun getSls() {
        val localData = slsDao.findAll()
        _resultData.value = ResultData.Success(localData.value)
    }

    companion object {
        @Volatile
        private var instance: SlsRepository? = null
        fun getInstance(
            apiService: ApiInterface,
            slsDao: SlsDao,
        ): SlsRepository =
            instance ?: synchronized(this) {
                instance ?: SlsRepository(apiService, slsDao)
            }.also { instance = it }
    }
}