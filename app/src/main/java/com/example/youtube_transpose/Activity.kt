package com.example.youtube_transpose

import android.app.SearchManager
import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtube_transpose.databinding.MainBinding
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.BuildConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.*


class Activity: AppCompatActivity() {
    var mBinding: MainBinding? = null
    val binding get() = mBinding!!

    lateinit var exoPlayer: SimpleExoPlayer
    lateinit var transposePage: LinearLayout
    lateinit var floatButton: ExtendedFloatingActionButton
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var pitchSeekBar: SeekBar
    lateinit var tempoSeekBar: SeekBar
    lateinit var searchView: SearchView

    var videoService: VideoService? = null

    private lateinit var popular100PlaylistAdapter: HomePopular100RecyclerViewAdapter
    private lateinit var searchAdapter: SearchSuggestionKeywordRecyclerViewAdapter
    private lateinit var thisYearPlaylistAdapter: HomePlaylistRecyclerViewAdapter
    private lateinit var latestMusicPlaylistAdapter: HomePlaylistRecyclerViewAdapter
    private lateinit var todayHotPlaylistAdapter: HomePlaylistRecyclerViewAdapter
    private lateinit var bestAtmospherePlaylistAdapter: HomePlaylistRecyclerViewAdapter
    private lateinit var bestSituationPlaylistAdapter: HomePlaylistRecyclerViewAdapter

    private val connection = object: ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val b = p1 as VideoService.VideoServiceBinder
            videoService = b.getService()
            videoService!!.initActivity(this@Activity)
            exoPlayer = videoService!!.VideoServiceBinder().getExoPlayerInstance()

        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }


    val API_KEY = com.example.youtube_transpose.BuildConfig.API_KEY
    val suggestionKeywords = ArrayList<String>()
    val playListVideoData = ArrayList<VideoData>()
    val thisYearPlaylistData = ArrayList<PlayListData>()
    val todayHotPlaylistData = ArrayList<PlayListData>()
    val latestMusicPlaylistData = ArrayList<PlayListData>()
    val bestAtmospherePlaylistData = ArrayList<PlayListData>()
    val bestSituationPlaylistData = ArrayList<PlayListData>()
    val thisYearMusicId = arrayListOf("RDCLAK5uy_kFoxHZo_PEVrqVnwKkeucGn4ldSyKHD8A",
        "RDCLAK5uy_lZZ5gBJBR1AYziS_onNkE2m18Peg042fI","RDCLAK5uy_nwmWfUayX21xwr1VB72jjBqDPDxGm9zc0",
        "RDCLAK5uy_mOWD5gzPMEI5Ip97H4ivxpcxE7uglskHo","RDCLAK5uy_kaV_BsACArm2vCjj74y871plTk9F8RIDA",
        "RDCLAK5uy_kvnBKwOS6J1tBqsNmdISJEhJ2a-RhNliI","RDCLAK5uy_keUi-XamVp1RPNUoJ-BPIQy3_DusjC5Mg",
        "RDCLAK5uy_kV8LcjsV6_R-y5ncz_SOyoo1BfCRs79wM","RDCLAK5uy_l5AiprVpfhWBQvu-76GJFd1_T2HZoYZWs",
        "RDCLAK5uy_ln2vAJIQILond713LQLKfxSruLhFB1lIM","RDCLAK5uy_kcIyRk6eceGdn0Zjijw0ARVbHCubof1zM",
        "RDCLAK5uy_k4vBNV3dzeyoMlSagQydjsZ-uyo1x1fIU")

    val todayHotMusicId = arrayListOf("RDCLAK5uy_l7wbVbkC-dG5fyEQQsBfjm_z3dLAhYyvo",
        "RDCLAK5uy_m9ty3WvAucm7-5KsKdro9_HnocE8LSS9o","RDCLAK5uy_kRRqnSpfrRZ9OJyTB2IE5WsAqYluG0uYo",
        "RDCLAK5uy_lYPvoz4gPFnKMw_BFojpMk7xRSIqVBkEE","RDCLAK5uy_l6DCR35xfT9bfeUqP7-uw6kWApcfYeDPY",
        "RDCLAK5uy_k6pZ82Gha0sopanWffXo4iMBVaGR7jQaE","RDCLAK5uy_mMRkzfvFXzNQbSl3K-hE_FJ7g8TqMtSlo",
        "RDCLAK5uy_mjCKq8hnUQJqul0W6YW6x2Ep4P67jQ5Po","RDCLAK5uy_l0nFcbRh2kbs27gleqzu364A9rN-D8Ib8",
        "RDCLAK5uy_ky-kXJCA_i0Gf0k6iNxsRHBhAgugAN8-g","RDCLAK5uy_lBfTSdVdgXeN399Mxt2L3C6hLarC1aaN0")

    val latestMusicId = arrayListOf("RDCLAK5uy_lS4dqGRHszluFAbLsV-sHJCqULtBm2Gfw",
        "RDCLAK5uy_mVBAam6Saaqa_DeJRxGkawqqxwPTBrGXM","RDCLAK5uy_nkjcHIQK0Hgf7ihU25uJc0CEokeGkSNxA",
        "RDCLAK5uy_mWqhoadUUp9crhEkmZZkdExj7YpBuFBEQ","RDCLAK5uy_n0f4tLAkNM233wO0yiTEI7467ovnaGbR8",
        "RDCLAK5uy_lN9xj1RQGmBltmvrzTVHMg-vyVt594KYU","RDCLAK5uy_kITLp-IuXw_winp1mnN9PSNatPBiAK52A",
        "RDCLAK5uy_mn7OLm9QvyB230t7RtLWt0BvUmFVlQ-Hc","RDCLAK5uy_lz175mC_wAtZHK0hbDqLrxb5J28QbUznQ",
        "RDCLAK5uy_nppUVicPb1PRbUZmVEMhqgvyFz33Il4pE"
        )

    val bestAtmosphereMusicId = arrayListOf("RDCLAK5uy_mA88hxo-cmI0-WaaRH8Bb2k0x2NptOPqM",
        "RDCLAK5uy_meEBX-iIBwtXBhkeWzwX6njohWnpMijP8","RDCLAK5uy_kT-sIJz2O-hpkxwjosN2hMt9Y5xevcPYI",
        "RDCLAK5uy_lNJA7PB9DAQEdtTnfuKaC2XEOAE1OoX50","RDCLAK5uy_lv6V83HLaJMQDx8YFtfSAaZ6GGvSqI6PE",
        "RDCLAK5uy_mL0lDqxdKwRtBbzva3yVjVy-BZ9L7KX5I","RDCLAK5uy_nXDnxSmhez06eAnjfT2pWjSpp-p2VBv54",
        "RDCLAK5uy_ksGphJr7YduIL-vDvJBUJQ2_JCYnCkaYI","RDCLAK5uy_kDBL_tFOUos7q3SOifZrMHXKwuebdzf7I"
    )

    val bestSituationMusicId = arrayListOf("RDCLAK5uy_msV9Vc8q_guumIXgLkzYs58uBZHVVBPtE",
        "RDCLAK5uy_lFgjDM5dWvoq0_wkEqx4_M43Nk6wXviaM","RDCLAK5uy_nEcCeflWNpzQNRExtAKjKkkX96wjom9Nc",
        "RDCLAK5uy_kskrFUGb5Tnz3-x4wyK9Q5j8RgfwQvq4k","RDCLAK5uy_kjKtb_RC7LRbxiEmSIzZqJRVcYm8U9KMc",
        "RDCLAK5uy_mS7UhvWzUZdjauupjE5JO6VCn-CCwaRoI","RDCLAK5uy_krjFmKbzWzkGvhqkYvvNnUbdrHy0QN1S8",
        "RDCLAK5uy_kQ09S7a68znbjr7h26ur1RJb2tCXDlruY"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindService(Intent(this, VideoService::class.java), connection, BIND_AUTO_CREATE)
        initView()
        initYoutubeDL()
        initToolbar()
        initRecyclerView()
        getAllData()
        Log.d("update","실행")
    }
    private fun initYoutubeDL(){
        try {
            YoutubeDL.getInstance().init(this)
        } catch (e: YoutubeDLException) {
            Log.e(ContentValues.TAG, "failed to initialize youtubedl-android", e)
        }
    }
    suspend fun getUrl() {
            val youtubeUrl = "https://www.youtube.com/watch?v="
            val url = youtubeUrl.trim()
            val request = YoutubeDLRequest(url)
            // best stream containing video+audio
            request.addOption("-f b", "")
            try {
                val Url = YoutubeDL.getInstance().getInfo(request).url
            }catch (e: YoutubeDLException){
                Log.d("유알엘이","잘못됨")
            }
        }



    private fun startStream(videoData: VideoData){

        val youtubeUrl = "https://www.youtube.com/watch?v=${videoData.videoId}"
        val url = youtubeUrl.trim()
        if (TextUtils.isEmpty(url)){
            Toast.makeText(this, "url오류", Toast.LENGTH_SHORT).show()
        }

        val disposable: Disposable = Observable.fromCallable {
            val request = YoutubeDLRequest(url)
            // best stream containing video+audio
            request.addOption("-f b", "")

            YoutubeDL.getInstance().getInfo(request)
        }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ streamInfo ->
                Log.d("정보","$streamInfo")
                val videoUrl: String = streamInfo.url
                if (TextUtils.isEmpty(videoUrl)) {
                    Toast.makeText(
                        this,
                        "failed to get stream url",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Log.d("유알엘","$videoUrl")
//                    setupVideoView(videoData, videoUrl)
                }
            }) { e ->
                if (BuildConfig.DEBUG) Log.e(ContentValues.TAG, "failed to get stream info", e)
                Toast.makeText(
                    this,
                    "streaming failed. failed to get stream info",
                    Toast.LENGTH_LONG
                ).show()
            }
        CompositeDisposable().add(disposable)
    }

    private fun initView() {
        initToolbar()
        initTranspose()
        initBottomNavigationView()
    }
    fun initTranspose(){
        transposePage = binding.transposePage
        pitchSeekBar = binding.pitchSeekBar
        pitchSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.pitchValue.text = p1.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                for(fragment in supportFragmentManager.fragments) {
                    if(fragment.isVisible && fragment is PlayerFragment) {
                        videoService!!.setPitch(p0?.progress!!)
                    }
                }
            }
        })
        binding.pitchInitButton.setOnClickListener {
            pitchSeekBar.progress = 0
            for(fragment in supportFragmentManager.fragments) {
                if(fragment.isVisible && fragment is PlayerFragment) {
                    videoService!!.setPitch(pitchSeekBar.progress)
                }
            }
        }
        tempoSeekBar = binding.tempoSeekBar
        tempoSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.tempoValue.text = p1.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                for(fragment in supportFragmentManager.fragments) {
                    if(fragment.isVisible && fragment is PlayerFragment) {
                        videoService!!.setTempo(p0?.progress!!)
                    }
                }

            }
        })
        binding.tempoInitButton.setOnClickListener {
            tempoSeekBar.progress = 0
            for(fragment in supportFragmentManager.fragments) {
                if(fragment.isVisible && fragment is PlayerFragment) {
                    videoService!!.setTempo(tempoSeekBar.progress!!)
                }
            }
        }
        binding.transposePage.setOnClickListener {
        }
    }
    fun initToolbar(){
        toolbar = binding.toolBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun initBottomNavigationView(){
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_icon -> {
                    transposePage.visibility = View.INVISIBLE
                    toolbar.collapseActionView()
                    while (supportFragmentManager.backStackEntryCount != 0) {
                        supportFragmentManager.popBackStackImmediate()
                    }
                }
                R.id.transpose_icon -> {
                    transposePage.visibility = View.VISIBLE
                }
            }
            true
        }
    }
    fun initRecyclerView(){
        initSearchRecyclerView()
        initPopularTop100RecyclerView()
        initBestAtmospherePlaylistRecyclerView()
        initBestSituationPlaylistRecyclerView()
        initLatestMusicPlaylistRecyclerView()
        initThisYearPlaylistRecyclerView()
        initTodayHotListPlaylistRecyclerView()
    }
    fun initSearchRecyclerView(){
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchSuggestionKeywordRecyclerViewAdapter(suggestionKeywords)
        searchAdapter.setItemClickListener(object: SearchSuggestionKeywordRecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                var mLastClickTime = 0L
                if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    val searchWord = suggestionKeywords[position]
                    suggestionKeywords.clear()
                    searchAdapter.notifyDataSetChanged()
                    searchView.clearFocus()
                    supportFragmentManager.beginTransaction()
                        .replace(binding.searchResultFragment.id,SearchResultFragment(searchWord))
                        .addToBackStack(null)
                        .commit()
                    binding.searchRecyclerView.visibility = View.INVISIBLE
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        })
        binding.searchRecyclerView.adapter = searchAdapter
    }

    fun initThisYearPlaylistRecyclerView(){
        binding.thisYearPlaylistRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        thisYearPlaylistAdapter = HomePlaylistRecyclerViewAdapter(thisYearPlaylistData)
        thisYearPlaylistAdapter.setItemClickListener(object: HomePlaylistRecyclerViewAdapter.OnItemClickListener{
            var mLastClickTime = 0L
            override fun onClick(v: View, position: Int) {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    supportFragmentManager.beginTransaction().replace(binding.playlistItemsFragment.id,PlaylistItemsFragment(thisYearPlaylistData[position]))
                        .addToBackStack(null)
                        .commit()
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        })
        binding.thisYearPlaylistRecyclerView.adapter = thisYearPlaylistAdapter
    }

    fun initTodayHotListPlaylistRecyclerView(){
        binding.todayHotListPlaylistRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        todayHotPlaylistAdapter = HomePlaylistRecyclerViewAdapter(todayHotPlaylistData)
        todayHotPlaylistAdapter.setItemClickListener(object: HomePlaylistRecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                supportFragmentManager.beginTransaction().replace(binding.playlistItemsFragment.id,PlaylistItemsFragment(todayHotPlaylistData[position]))
                    .addToBackStack(null)
                    .commit()
            }

        })
        binding.todayHotListPlaylistRecyclerView.adapter = todayHotPlaylistAdapter
    }

    fun initLatestMusicPlaylistRecyclerView(){
        binding.latestMusicPlaylistRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        latestMusicPlaylistAdapter = HomePlaylistRecyclerViewAdapter(latestMusicPlaylistData)
        latestMusicPlaylistAdapter.setItemClickListener(object: HomePlaylistRecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                supportFragmentManager.beginTransaction().replace(binding.playlistItemsFragment.id,PlaylistItemsFragment(latestMusicPlaylistData[position]))
                    .addToBackStack(null)
                    .commit()
            }

        })
        binding.latestMusicPlaylistRecyclerView.adapter = latestMusicPlaylistAdapter

    }

    fun initBestAtmospherePlaylistRecyclerView(){
        binding.bestAtmospherePlaylistRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        bestAtmospherePlaylistAdapter = HomePlaylistRecyclerViewAdapter(bestAtmospherePlaylistData)
        bestAtmospherePlaylistAdapter.setItemClickListener(object: HomePlaylistRecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                supportFragmentManager.beginTransaction().replace(binding.playlistItemsFragment.id,PlaylistItemsFragment(bestAtmospherePlaylistData[position]))
                    .addToBackStack(null)
                    .commit()
            }

        })
        binding.bestAtmospherePlaylistRecyclerView.adapter = bestAtmospherePlaylistAdapter

    }

    fun initBestSituationPlaylistRecyclerView(){
        binding.bestSituationPlaylistRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        bestSituationPlaylistAdapter = HomePlaylistRecyclerViewAdapter(bestSituationPlaylistData)
        bestSituationPlaylistAdapter.setItemClickListener(object: HomePlaylistRecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.playlistItemsFragment.id,PlaylistItemsFragment(bestSituationPlaylistData[position]))
                    .addToBackStack(null)
                    .commit()
            }

        })
        binding.bestSituationPlaylistRecyclerView.adapter = bestSituationPlaylistAdapter
    }


    fun initPopularTop100RecyclerView(){
        val gridLayoutManager = GridLayoutManager(this,4,GridLayoutManager.HORIZONTAL,false)
        binding.popularTop100PlaylistVideoRecyclerView.layoutManager = gridLayoutManager
        popular100PlaylistAdapter = HomePopular100RecyclerViewAdapter(playListVideoData)
        binding.popularTop100PlaylistVideoRecyclerView.adapter = popular100PlaylistAdapter

        popular100PlaylistAdapter.setItemClickListener(object: HomePopular100RecyclerViewAdapter.OnItemClickListener{
            var mLastClickTime = 0L
            override fun onClick(v: View, position: Int) {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.playerFragment.id,PlayerFragment(playListVideoData, position, "playlist"),"playerFragment")
                        .commit()
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        })

    }

    private fun getSuggestionKeyword(newText: String){
        val retrofit = RetrofitSuggestionKeyword.initRetrofit()
        retrofit.create(RetrofitService::class.java).getSuggestionKeyword("firefox","yt",newText)
            .enqueue(object : Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    suggestionKeywords.clear()
                    val responseString = convertStringUnicodeToKorean(response.body()?.string()!!)
                    val splitBracketList = responseString.split('[')
                    val splitCommaList = splitBracketList[2].split(',')
                    if (splitCommaList[0] != "]]" && splitCommaList[0] != '"'.toString()){
                        addSubstringToSuggestionKeyword(splitCommaList)
                    }
                    searchAdapter.notifyDataSetChanged()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("실패!","!1")
                }
            })
    }
    /**
    문자열 정보가 이상하게 들어와 알맞게 나눠주고 리스트에 추가
     **/
    private fun addSubstringToSuggestionKeyword(splitList: List<String>){
        for (index in splitList.indices){
            if (splitList[index].isNotEmpty()){
                Log.d("함수","${splitList[index]}")
                if (splitList[index][splitList[index].length-1] == ']')
                    suggestionKeywords.add(splitList[index].substring(1, splitList[index].length-2))
                else
                    suggestionKeywords.add(splitList[index].substring(1, splitList[index].length-1))
            }
        }
    }
    private fun convertStringUnicodeToKorean(data: String): String {
        val sb = StringBuilder() // 단일 쓰레드이므로 StringBuilder 선언
        var i = 0
        /**
         * \uXXXX 로 된 아스키코드 변경
         * i+2 to i+6 을 16진수의 int 계산 후 char 타입으로 변환
         */
        while (i < data.length) {
            if (data[i] == '\\' && data[i + 1] == 'u') {
                val word = data.substring(i + 2, i + 6).toInt(16).toChar()
                sb.append(word)
                i += 5
            } else {
                sb.append(data[i])
            }
            i++
        }

        return sb.toString()
    }

    private fun getAllData(){
        val job = CoroutineScope(Dispatchers.IO).launch {
            async { getPopularTop100MusicData(null) }
            async { getPlaylistData(thisYearMusicId) }
            async { getPlaylistData(todayHotMusicId) }
            async { getPlaylistData(latestMusicId) }
            async { getPlaylistData(bestAtmosphereMusicId) }
            async { getPlaylistData(bestSituationMusicId) }
            getUrl()
        }
    }

    private suspend fun getPlaylistData(musicUrls: ArrayList<String>) {
        for (index in musicUrls.indices) {
            val retrofit = RetrofitData.initRetrofit()
            val response = retrofit.create(RetrofitService::class.java)
                .getPlayLists(API_KEY, "snippet", musicUrls[index], "50")
            if (response.isSuccessful){
                if (response.body()?.items?.size != 0) {
                    withContext(Dispatchers.Main){
                        playlistDataMapping(musicUrls, response.body()!!)
                    }
                }
            }
        }
    }

    private fun playlistDataMapping(musicUrls: ArrayList<String>, responseData: PlayListSearchData){
        val thumbnail = responseData.items[0].snippet?.thumbnails?.medium?.url!!
        val title = responseData.items[0].snippet?.title!!
        val description = responseData.items[0].snippet?.description!!
        val playlistId = responseData.items[0].id!!
        when(musicUrls) {
            thisYearMusicId -> {
                thisYearPlaylistData.add(PlayListData(thumbnail, title, description, playlistId))
                thisYearPlaylistAdapter.notifyDataSetChanged()
                binding.thisYearPlaylistVideoProgressBar.visibility = View.GONE
                binding.thisYearPlaylistRecyclerView.visibility = View.VISIBLE
            }
            todayHotMusicId -> {
                todayHotPlaylistData.add(PlayListData(thumbnail, title, description, playlistId))
                todayHotPlaylistAdapter.notifyDataSetChanged()
                binding.todayHotListPlaylistProgressBar.visibility = View.GONE
                binding.todayHotListPlaylistRecyclerView.visibility = View.VISIBLE
            }
            latestMusicId -> {
                latestMusicPlaylistData.add(PlayListData(thumbnail, title, description, playlistId))
                latestMusicPlaylistAdapter.notifyDataSetChanged()
                binding.latestMusicPlaylistProgressBar.visibility = View.GONE
                binding.latestMusicPlaylistRecyclerView.visibility = View.VISIBLE
            }
            bestAtmosphereMusicId -> {
                bestAtmospherePlaylistData.add(PlayListData(thumbnail, title, description, playlistId))
                bestAtmospherePlaylistAdapter.notifyDataSetChanged()
                binding.bestAtmospherePlaylistProgressBar.visibility = View.GONE
                binding.bestAtmospherePlaylistRecyclerView.visibility = View.VISIBLE
            }
            bestSituationMusicId -> {
                bestSituationPlaylistData.add(PlayListData(thumbnail, title, description, playlistId))
                bestSituationPlaylistAdapter.notifyDataSetChanged()
                binding.bestSituationPlaylistProgressBar.visibility = View.GONE
                binding.bestSituationPlaylistRecyclerView.visibility = View.VISIBLE
            }
        }
    }
    private suspend fun getPopularTop100MusicData(
        nextPageToken: String?
    ) {
        val retrofit = RetrofitData.initRetrofit()
        val response =  retrofit.create(RetrofitService::class.java).getPlayListVideoItems(
            API_KEY,
            "snippet",
            "PL2HEDIx6Li8jGsqCiXUq9fzCqpH99qqHV",
            nextPageToken,
            "50"
        )
        if (response.isSuccessful){
            if (response.body()?.items?.size != 0){
                withContext(Dispatchers.Main){
                    popularTop100MusicDataMapping(response.body()!!)
                }
                if (response.body()?.nextPageToken != null)
                    getPopularTop100MusicData(response.body()?.nextPageToken)
            }
        }
    }

    private fun popularTop100MusicDataMapping(responseData: PlayListVideoSearchData){
        Log.d("매핑","했다")
        for (index in responseData.items.indices){
            val thumbnail = responseData.items[index].snippet?.thumbnails?.default?.url!!
            val date = responseData.items[index].snippet?.publishedAt!!.substring(0, 10)
            val channelTitle = responseData.items[index].snippet?.videoOwnerChannelTitle?.replace(" - Topic","")!!
            val channelId = responseData.items[index].id
            val title = stringToHtmlSign(responseData.items[index].snippet?.title!!)
            val videoId = responseData.items[index].snippet?.resourceId?.videoId!!
            playListVideoData.add(VideoData(thumbnail, title, channelTitle, videoId, date, thumbnail))
            }
        binding.popularTop100PlaylistVideoProgressBar.visibility = View.GONE
        binding.popularTop100PlaylistVideoRecyclerView.visibility = View.VISIBLE
        popular100PlaylistAdapter.notifyDataSetChanged()
    }


    //영상 제목을 받아올때 &quot; &#39; 문자가 그대로 출력되기 때문에 다른 문자로 대체 해주기 위해 사용하는 메서드
    private fun stringToHtmlSign(str: String): String {
        return str.replace("&amp;".toRegex(), "[&]")
            .replace("[<]".toRegex(), "&lt;")
            .replace("[>]".toRegex(), "&gt;")
            .replace("&quot;".toRegex(), "'")
            .replace("&#39;".toRegex(), "'")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_item, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.youtube_search_icon).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView = this
            val searchAutoComplete = searchView.findViewById<SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
            searchAutoComplete.setTextColor(resources.getColor(R.color.white))
            searchAutoComplete.setHintTextColor(resources.getColor(R.color.description_color))
            searchAutoComplete.hint = "Youtube 검색"

            searchView.setOnQueryTextFocusChangeListener { p0, p1 ->
                binding.searchRecyclerView.visibility = View.VISIBLE
            }
            menu.findItem(R.id.youtube_search_icon).setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    Log.d("메뉴가","확장됨")
                    binding.toolBar.setBackgroundColor(resources.getColor(R.color.drawer_background))
                    binding.bottomNavigationView.visibility = View.GONE
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    for(fragment in supportFragmentManager.fragments) {
                        if(fragment.isVisible && fragment is SearchResultFragment) {
                            supportFragmentManager.popBackStackImmediate()
                        }
                    }
                    binding.toolBar.setBackgroundColor(resources.getColor(R.color.black))
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.searchRecyclerView.visibility = View.INVISIBLE
                    suggestionKeywords.clear()
                    searchAdapter.notifyDataSetChanged()
                    return true
                }
            })
            this.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    supportFragmentManager.beginTransaction()
                        .replace(binding.searchResultFragment.id,SearchResultFragment(query!!))
                        .addToBackStack(null)
                        .commit()
                    binding.searchRecyclerView.visibility = View.INVISIBLE
                    return false
                }
                //SwipeRefreshLayout 새로고침
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != ""){
                        getSuggestionKeyword(newText!!)
                    }
                    if (newText == ""){
                        Log.d("빈","칸")
                        suggestionKeywords.clear()
                        searchAdapter.notifyDataSetChanged()
                    }
                    Log.d("TAG", "SearchVies Text is changed : $newText")
                    return false
                }
            })

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId){
            R.id.transpose_icon -> {
                true
            }
            R.id.youtube_search_icon -> {
                Log.d("서치 버튼이","눌림")
                binding.searchRecyclerView.visibility = View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        if (supportFragmentManager.findFragmentByTag("playerFragment") == null){
            if (transposePage.visibility == View.VISIBLE)
                transposePage.visibility = View.INVISIBLE
            else
                return super.onBackPressed()
        }
        else{
            val playerFragment = supportFragmentManager.findFragmentById(binding.playerFragment.id) as PlayerFragment
            if (playerFragment.binding.playerMotionLayout.currentState == R.id.end){
                playerFragment.binding.playerMotionLayout.transitionToState(R.id.start)
            }
            else{
                if (transposePage.visibility == View.VISIBLE)
                    transposePage.visibility = View.INVISIBLE
                else
                    return super.onBackPressed()
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        val intent = Intent(this, VideoService::class.java)
        stopService(intent)
    }

}