package com.sbsj.dreamwing.user.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.UpdateUserActivity
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentMypageBinding
import com.sbsj.dreamwing.user.MyPointDetailActivity
import com.sbsj.dreamwing.user.MySupportDetailActivity
import com.sbsj.dreamwing.user.SignUpActivity
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
 * @author 정은지
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01  	정은지       최초 생성
 * 2024.08.02   정은찬       사용자 정보를 가져와 UI 업데이트
 * </pre>
 */
class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserInfo()

        val updateButton = binding.updateButton

        // 회원가입 버튼 클릭 리스너 설정
        updateButton.setOnClickListener {
            // 회원가입 액티비티로 이동
            val intent = Intent(activity, UpdateUserActivity::class.java)
            startActivity(intent)
            // Fragment에서는 finish()를 호출할 수 없습니다.
            // Activity에서 Fragment를 종료하려면 다음과 같이 작성하세요.
            activity?.finish()
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

    private fun fetchUserInfo() {
        val jwtToken = SharedPreferencesUtil.getToken(requireContext())
        if (jwtToken.isNullOrEmpty()) {


            return
        }

        val authHeader = "$jwtToken"

        lifecycleScope.launch {
            try {
                RetrofitClient.userService.getMyPageInfo(authHeader).enqueue(object : Callback<ApiResponse<MyPageDTO>> {
                    override fun onResponse(
                        call: Call<ApiResponse<MyPageDTO>>,
                        response: Response<ApiResponse<MyPageDTO>>
                    ) {
                        if (response.isSuccessful) {
                            val userInfoData = response.body()?.data
                            Log.d("MypageFragment", "userinfo: $userInfoData")
                            userInfoData?.let {
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

    private fun updateUI(myPageDTO: MyPageDTO) {
        binding.name.text = myPageDTO.name
        Picasso.get().load(myPageDTO.profileImageUrl).into(binding.profileImage)
        binding.userPoint.text = myPageDTO.totalPoint.toString()
        binding.totalSupportPoit.text = myPageDTO.totalSupportPoint.toString()
    }

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

    private fun createDetailTextView(detail: String): TextView {
        val textView = TextView(context)
        textView.text = detail
        textView.textSize = 16f
        textView.setTextColor(resources.getColor(R.color.white, null))
        return textView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
