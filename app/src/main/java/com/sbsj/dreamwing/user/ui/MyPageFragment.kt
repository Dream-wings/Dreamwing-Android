package com.sbsj.dreamwing.user.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.user.UpdateUserActivity
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentMypageBinding
import com.sbsj.dreamwing.user.LoginActivity
import com.sbsj.dreamwing.user.MyPointDetailActivity
import com.sbsj.dreamwing.user.MySupportDetailActivity
import com.sbsj.dreamwing.user.model.dto.MyPageDTO
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * 마이페이지 프래그먼트
 */
class MyPageFragment : Fragment() {
    // ViewBinding을 사용하여 뷰 요소에 쉽게 접근하기 위한 변수
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    /**
     * 프래그먼트의 뷰를 생성하는 메서드
     * @param inflater 레이아웃 인플레이터
     * @param container 컨테이너 뷰 그룹
     * @param savedInstanceState 저장된 인스턴스 상태
     * @return 생성된 뷰
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 뷰가 생성된 후 호출되는 메서드
     * @param view 생성된 뷰
     * @param savedInstanceState 저장된 인스턴스 상태
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 사용자 로그인 상태를 확인하고, 로그인 상태라면 사용자 정보를 가져옵니다.
        if (checkUserLoggedIn()) {
            fetchUserInfo()
        }

        // 회원가입 버튼 클릭 리스너 설정
        val updateButton = binding.updateButton
        updateButton.setOnClickListener {
            // 회원가입 액티비티로 이동
            val intent = Intent(activity, UpdateUserActivity::class.java)
            startActivity(intent)
        }

        // 포인트 상세보기 클릭 이벤트 설정
        val pointDetail = view.findViewById<View>(R.id.pointDetail)
        pointDetail.setOnClickListener {
            val intent = Intent(activity, MyPointDetailActivity::class.java)
            startActivity(intent)
        }

        // 후원 상세보기 클릭 이벤트 설정
        val supportDetail = view.findViewById<View>(R.id.supportDetail)
        supportDetail.setOnClickListener {
            val intent = Intent(activity, MySupportDetailActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 프래그먼트가 화면에 다시 나타날 때 호출되는 메서드
     */
    override fun onResume() {
        super.onResume()
        // 프래그먼트가 다시 보여질 때마다 사용자 정보를 갱신
        if (checkUserLoggedIn()) {
            fetchUserInfo()
        }
    }

    /**
     * 사용자가 로그인되어 있는지 확인하는 메서드
     * @return 로그인 여부
     */
    private fun checkUserLoggedIn(): Boolean {
        val jwtToken = SharedPreferencesUtil.getToken(requireContext())
        if (jwtToken.isNullOrEmpty()) {
            // 로그인되어 있지 않으면 로그인 요청 다이얼로그를 표시
            showLoginRequestDialog()
            return false
        }
        return true
    }

    /**
     * 사용자 정보를 서버에서 가져오는 메서드
     */
    private fun fetchUserInfo() {
        val jwtToken = SharedPreferencesUtil.getToken(requireContext())
        val authHeader = "$jwtToken"

        lifecycleScope.launch {
            try {
                // Retrofit을 사용하여 사용자 정보 API 호출
                RetrofitClient.userService.getMyPageInfo(authHeader).enqueue(object : Callback<ApiResponse<MyPageDTO>> {
                    override fun onResponse(
                        call: Call<ApiResponse<MyPageDTO>>,
                        response: Response<ApiResponse<MyPageDTO>>
                    ) {
                        if (response.isSuccessful) {
                            val userInfoData = response.body()?.data
                            Log.d("MypageFragment", "userinfo: $userInfoData")
                            userInfoData?.let {
                                // 사용자 정보를 UI에 업데이트
                                updateUI(it)
                            }
                        } else {
                            Log.e("MypageFragment", "Response not successful: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<MyPageDTO>>, t: Throwable) {
                        Log.e("MypageFragment", "Error: ${t.message}")
                    }
                })
            } catch (e: HttpException) {
                Log.e("MypageFragment", "HttpException: ${e.message}")
            } catch (e: IOException) {
                Log.e("MypageFragment", "IOException: ${e.message}")
            }
        }
    }

    /**
     * 사용자 정보를 UI에 업데이트하는 메서드
     * @param myPageDTO 사용자 정보 데이터 전송 객체
     */
    private fun updateUI(myPageDTO: MyPageDTO) {
        binding.name.text = myPageDTO.name
        Picasso.get().load(myPageDTO.profileImageUrl).into(binding.profileImage)
        binding.userPoint.text = myPageDTO.totalPoint.toString()
        binding.totalSupportPoit.text = myPageDTO.totalSupportPoint.toString()
    }

    /**
     * 세부 내역을 컨테이너에 추가하는 메서드
     * @param container 내역을 추가할 컨테이너
     * @param details 내역 리스트
     */
    private fun addDetailsToContainer(container: LinearLayout, details: List<String>) {
        container.removeAllViews()
        if (details.isEmpty()) {
            container.addView(createDetailTextView("내역 없음"))
        } else {
            for (i in details.indices) {
                if (i >= 3) break
                container.addView(createDetailTextView(details[i]))
            }
        }
    }

    /**
     * 세부 내역 텍스트뷰를 생성하는 메서드
     * @param detail 내역 문자열
     * @return 생성된 텍스트뷰
     */
    private fun createDetailTextView(detail: String): TextView {
        val textView = TextView(context)
        textView.text = detail
        textView.textSize = 16f
        textView.setTextColor(resources.getColor(R.color.white, null))
        return textView
    }

    /**
     * 뷰가 파괴될 때 호출되는 메서드
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 로그인 요청 다이얼로그를 표시하는 메서드
     */
    private fun showLoginRequestDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("로그인 요청")
            .setMessage("로그인이 필요합니다.")
            .setPositiveButton("확인") { dialog, _ ->
                // 로그인 액티비티로 이동
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            .show()
    }
}