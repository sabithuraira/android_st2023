package bps.sumsel.st2023.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import bps.sumsel.st2023.api.ApiInterface
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
    private val result = MediatorLiveData<ResultData<List<SlsEntity>>>()

    fun syncSls(): LiveData<ResultData<List<SlsEntity>>>{
        result.value = ResultData.Loading
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
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSls>, t: Throwable) {
                result.value = ResultData.Error(t.message.toString())
            }
        })

        val localData = slsDao.findAll()
        result.addSource(localData) { newData: List<SlsEntity> ->
            result.value = ResultData.Success(newData)
        }
        return result
    }

    fun getSls(): LiveData<ResultData<List<SlsEntity>>> {
        val localData = slsDao.findAll()
        result.addSource(localData) { newData: List<SlsEntity> ->
            result.value = ResultData.Success(newData)
        }
        return result
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