package com.kinopio.eatgo.presentation.store

import android.content.Context
import android.util.AttributeSet
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.appcompat.widget.Toolbar
import com.kinopio.eatgo.MainActivity
import com.kinopio.eatgo.R
import com.kinopio.eatgo.RetrofitClient
import com.kinopio.eatgo.data.store.StoreService
import com.kinopio.eatgo.databinding.ActivityMainBinding
import com.kinopio.eatgo.databinding.ActivityManageBinding
import com.kinopio.eatgo.domain.map.StoreHistoryRequestDto
import com.kinopio.eatgo.domain.map.StoreLocationDto
import com.kinopio.eatgo.domain.map.StoreMyPageResponseDto
import com.kinopio.eatgo.domain.store.StoreModificationResponseDto
import com.kinopio.eatgo.domain.store.ui_model.OpenInfo
import com.kinopio.eatgo.domain.templates.ApiResultDto
import com.kinopio.eatgo.presentation.map.NaverMapFragment
import com.kinopio.eatgo.presentation.map.StoreMangeNaverMapFragment
import com.kinopio.eatgo.presentation.templates.NavigationFragment
import com.kinopio.eatgo.util.LockableNestedScrollView
import com.kinopio.eatgo.util.OnMapTouchListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.Locale


class ManageActivity : AppCompatActivity(), OnMapTouchListener {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var storeModificationResponseDto: StoreModificationResponseDto
    private var selectedPositionX = 0.0
    private var selectedPositionY = 0.0
    private val binding : ActivityManageBinding by lazy {
        ActivityManageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        ToolbarUtils.setupToolbar(
            this,
            binding.root.findViewById<Toolbar>(R.id.toolbar),
            "가게 관리",
            null
        )

        // 예외 처리 해줘야 함
        var storeId = intent.getIntExtra("storeId", 0)
        if(storeId == 0){
            Log.d("store intent", "storeId가 0입니다")
        }
        var startStatus = findViewById<TextView>(R.id.startStatus)
        var endStatus = findViewById<TextView>(R.id.endStatus)
        startStatus.visibility = View.GONE
        endStatus.visibility = View.GONE


        val retrofit = RetrofitClient.getRetrofit2()
        val storeService = retrofit.create(StoreService::class.java)


        storeService.getModificationStore(storeId)
            .enqueue(object : Callback<StoreModificationResponseDto> {
                override fun onFailure(call: Call<StoreModificationResponseDto>, t: Throwable) {
                    Log.d("fail123", "실패")
                    Log.d("fail123", "$t")
                }
                override fun onResponse(
                    call: Call<StoreModificationResponseDto>,
                    response: Response<StoreModificationResponseDto>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.d("retrofit", "retrofit4")
                        return
                    }
                    Log.d("retrofit", "통신 + ${response?.body()}")

                    response.body()?.let{
                        storeModificationResponseDto=it
                    }
                    var days : List<OpenInfo>
                    binding.storeEdittext.text = Editable.Factory.getInstance().newEditable(storeModificationResponseDto?.storeInfo)
                    if(storeModificationResponseDto?.createdType == 1){
                        binding.foodTruck.isChecked = true
                    } else{
                        binding.snackCart.isChecked = true
                    }

                    val fm = supportFragmentManager
                    val transaction = fm.beginTransaction()

                    val bundle = Bundle()

                    var mapFragment : StoreMangeNaverMapFragment = StoreMangeNaverMapFragment()

                    bundle.putDouble("positionX", storeModificationResponseDto!!.positionX)
                    bundle.putDouble("positionY", storeModificationResponseDto!!.positionY)
                    binding.manageAddress.text = storeModificationResponseDto.address.toString()
                    mapFragment.arguments = bundle

                    transaction.add(R.id.manageMap, mapFragment)
                    transaction.commit()
                    // storeMyPageResponseDto?.openInfos?.get(1)?.day


                    for (i in 0 .. storeModificationResponseDto?.openInfos?.size!! - 1) {
                        when(storeModificationResponseDto.openInfos[i].day) {
                            "월" -> binding.tbMon.isChecked = true
                            "화" -> binding.tbTue.isChecked = true
                            "수" -> binding.tbWed.isChecked = true
                            "목" -> binding.tbThu.isChecked = true
                            "금" -> binding.tbFri.isChecked = true
                            "토" -> binding.tbSat.isChecked = true
                            "일" -> binding.tbSun.isChecked = true
                        }
                    }
                }
            })




        binding.startBtn.setOnClickListener {
            Log.d("store start retrofit", "store retrofit1")

            val retrofit = RetrofitClient.getRetrofit2()
            val storeService = retrofit.create(StoreService::class.java)

            Log.d("store start retrofit", "store retrofit2")

            getPosition(binding.manageAddress.text.toString())

            var storeHistoryRequestDto: StoreHistoryRequestDto = StoreHistoryRequestDto(
                address = binding.manageAddress.text.toString(),
                storeId = storeId,
                positionX = selectedPositionX,
                positionY = selectedPositionY,
            )

            storeService.changeStoreStatusOpen(storeId, storeHistoryRequestDto)
                .enqueue(object : Callback<ApiResultDto> {
                    override fun onFailure(call: Call<ApiResultDto>, t: Throwable) {
                        Log.d("fail", "실패")
                        Log.d("fail", "$t")
                    }
                    override fun onResponse(
                        call: Call<ApiResultDto>,
                        response: Response<ApiResultDto>
                    ) {
                        if (response.isSuccessful.not()) {
                            Log.d("retrofit", "retrofit4")
                            return
                        }
                        Log.d("retrofit", "통신 + ${response?.body()}")
                    }
                })

            endStatus.visibility = View.GONE
            startStatus.visibility = View.VISIBLE

        }

        
        
        binding.closeBtn.setOnClickListener {
            Log.d("store close retrofit", "store retrofit1")

            val retrofit = RetrofitClient.getRetrofit2()
            val storeService = retrofit.create(StoreService::class.java)

            Log.d("store close retrofit", "store retrofit2")
            storeService.changeStoreStatusClose(storeId)
                .enqueue(object : Callback<ApiResultDto> {
                    override fun onFailure(call: Call<ApiResultDto>, t: Throwable) {
                        Log.d("fail", "실패")
                        Log.d("fail", "$t")
                    }
                    override fun onResponse(
                        call: Call<ApiResultDto>,
                        response: Response<ApiResultDto>
                    ) {
                        if (response.isSuccessful.not()) {
                            Log.d("retrofit", "retrofit4")
                            return
                        }
                        Log.d("retrofit", "통신 + ${response?.body()}")
                    }
                })
            endStatus.visibility = View.VISIBLE
            startStatus.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this , MainActivity::class.java)
        return ToolbarUtils.handleOptionsItemSelected(
            this,
            item
        ) // 분리된 클래스의 handleOptionsItemSelected 함수 호출
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    override fun onTouch() {
        // 스크롤뷰 객체에 requestDisallowInterceptTouchEvent를 true로 설정
        binding.svParent.requestDisallowInterceptTouchEvent(true)
    }

    private fun getPosition(address : String) {
        Geocoder(applicationContext, Locale.KOREA).getFromLocationName(address, 1)?.let {
            Location("").apply {
                Log.d("geo", "${it[0].latitude}, ${it[0].longitude}")
                selectedPositionX = it[0].latitude.toDouble()
                selectedPositionY = it[0].longitude.toDouble()

            }
        }
    }
}