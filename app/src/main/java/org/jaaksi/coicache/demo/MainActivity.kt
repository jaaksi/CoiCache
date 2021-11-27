package org.jaaksi.coicache.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jaaksi.coicache.CoiCache
import org.jaaksi.coicache.demo.api.Api
import org.jaaksi.coicache.demo.databinding.ActivityMainBinding
import org.jaaksi.coicache.demo.model.ApiResponse
import org.jaaksi.coicache.demo.model.BannerBean
import org.jaaksi.coicache.demo.util.ToastUtil
import org.jaaksi.coicache.extention.buildApi
import org.jaaksi.coicache.type.CacheType

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.activity_main, null)
        setContentView(view)

        binding = DataBindingUtil.bind(view)!!
        binding.apply {
            btnAdd.setOnClickListener {
                syncSave()
            }

            btnGet.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        CoiCache.get("url", String::class.java)
                    }.let {
                        ToastUtil.toast("rxGet url = $it")
                    }

                    withContext(Dispatchers.IO) {
                        CoiCache.get("banner", object : CacheType<ApiResponse<MutableList<BannerBean>>>() {})
                    }.let {
                        ToastUtil.toast("banner = ${it?.data?.getOrNull(0)?.title}")
                    }

                }
            }

            btnClear.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    CoiCache.clear()
                }
            }

            btnRequest.setOnClickListener {
                request()
            }
        }
    }

    private fun request() {
        lifecycleScope.launch {
            buildApi {
                Api::class.java.create().getBanner()
            }
//                .cacheStrategy(CacheStrategy.CACHE_AND_REMOTE)
                .cacheKey("banner")
                .cacheable { it.hasData() }
//                .buildCache(object : CacheType<ApiResponse<MutableList<BannerBean>>>() {})
                .buildFlowWithWrapper(object : CacheType<ApiResponse<MutableList<BannerBean>>>() {})
                .flowOn(Dispatchers.IO)
                .catch {
//                    ToastUtil.toast(it.message)
                    binding.textview.text = it.toString()
                }
                .collect {
                    ToastUtil.toast("数据是否来自缓存：${it.isFromCache}")
                    binding.textview.text = Gson().toJson(it.data)
                }
        }
    }

    // 同步存储
    private fun syncSave() {
        CoiCache.put("url", "111")
        CoiCache.put("data", BannerBean().apply {
            desc = "flutter"
            title = "flutter 中文社区"
        })
    }

    // 异步存储
    private fun asyncSave() {
        GlobalScope.launch {
            CoiCache.put("url", "111")
            CoiCache.put("data", BannerBean().apply {
                desc = "flutter"
                title = "flutter 中文社区"
            })
        }

    }
}