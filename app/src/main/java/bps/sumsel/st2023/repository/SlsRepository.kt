package bps.sumsel.st2023.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import bps.sumsel.st2023.api.ApiInterface

class SlsRepository  private constructor(
    private val apiService: ApiInterface,
    private val newsDao: NewsDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<NewsEntity>>>()

    fun getHeadlineNews(): LiveData<Result<List<NewsEntity>>> {
        result.value = Result.Loading
        val client = apiService.getNews(BuildConfig.API_KEY)
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.articles
                    val newsList = ArrayList<NewsEntity>()
                    appExecutors.diskIO.execute {
                        articles?.forEach { article ->
                            val isBookmarked = newsDao.isNewsBookmarked(article.title)
                            val news = NewsEntity(
                                article.title,
                                article.publishedAt,
                                article.urlToImage,
                                article.url,
                                isBookmarked
                            )
                            newsList.add(news)
                        }
                        newsDao.deleteAll()
                        newsDao.insertNews(newsList)
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        val localData = newsDao.getNews()
        result.addSource(localData) { newData: List<NewsEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    companion object {
        @Volatile
        private var instance: SlsRepository? = null
        fun getInstance(
            apiService: ApiInterface,
            newsDao: NewsDao,
            appExecutors: AppExecutors
        ): SlsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService, newsDao, appExecutors)
            }.also { instance = it }
    }
}