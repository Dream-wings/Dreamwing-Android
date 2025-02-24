package com.sbsj.dreamwing.user.ui

import android.app.AlertDialog
import android.content.Intent
import android.icu.text.DecimalFormat
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
import com.sbsj.dreamwing.MainActivity
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentMypageBinding
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
    // ViewBinding을 사용하여 뷰 요소에 쉽게 접근하기 위한 변수
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    /**
     * 프래그먼트의 뷰를 생성하는 메서드
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 뷰가 생성된 후 호출
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 사용자 로그인 상태를 확인하고, 로그인 상태라면 사용자 정보를 가져옵니다.
        if (checkUserLoggedIn()) {
            fetchUserInfo()
        }

        // 회원정보 수정 버튼 클릭 리스너 설정
        val updateButton = binding.updateButton
        updateButton.setOnClickListener {
            // 회원가입 액티비티로 이동
            val intent = Intent(activity, UpdateUserActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 수정 버튼 클릭 리스너 설정
        val logoutButton = binding.logoutButton
        logoutButton.setOnClickListener {
            val jwtToken = SharedPreferencesUtil.getToken(requireContext())
            val authHeader = "$jwtToken"

            // Retrofit을 사용하여 사용자 정보 API 호출
            RetrofitClient.userService.logout(authHeader).enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        // 토큰 삭제
                        SharedPreferencesUtil.clearToken(requireContext())
                        showLogoutSuccessDialog()


                    } else {
                        showLogoutFailDialog()
                        Log.e("MypageFragment", "Response not successful: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("MypageFragment", "Error: ${t.message}")
                }
            })

        }

        // 포인트 내역 클릭 이벤트 설정
        val pointDetail = view.findViewById<View>(R.id.pointDetail)
        pointDetail.setOnClickListener {
            val intent = Intent(activity, MyPointDetailActivity::class.java)
            startActivity(intent)
        }

        // 후원 내역 클릭 이벤트 설정
        val supportDetail = view.findViewById<View>(R.id.supportDetail)
        supportDetail.setOnClickListener {
            val intent = Intent(activity, MySupportDetailActivity::class.java)
            startActivity(intent)
        }

        // 봉사 활동 내역 클릭 이벤트 설정
        val volunteerDetail = view.findViewById<View>(R.id.volunteerDetail)
        volunteerDetail.setOnClickListener {
            val intent = Intent(activity, MyVolunteerDetailActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 프래그먼트가 화면에 다시 나타날 때 호출되는 메서드
     */
    override fun onResume() {
        super.onResume()
        fetchUserInfo()
    }

    /**
     * 사용자가 로그인되어 있는지 확인하는 메서드
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
     */
    private fun updateUI(myPageDTO: MyPageDTO) {
        binding.name.text = myPageDTO.name
        Picasso.get().load(myPageDTO.profileImageUrl).into(binding.profileImage)

        val decimalFormat = DecimalFormat("#,###")

        binding.userPoint.text = decimalFormat.format(myPageDTO.totalPoint)
        binding.totalSupportPoit.text = decimalFormat.format(myPageDTO.totalSupportPoint)
    }

    /**
     * 세부 내역을 컨테이너에 추가하는 메서드
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_alert, null)
        val confirmTextView = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)

        confirmTextView.text = "로그인이 필요합니다."

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            dialog.dismiss()
            // 로그인 액티비티로 이동
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        dialog.show()
    }

    /**
     * 로그인아웃 시 성공 다이얼로그 표시하는 메서드
     */
    private fun showLogoutSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alert, null)
        val confirmTextView = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)

        confirmTextView.text = "로그아웃 되었습니다."

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            dialog.dismiss()
            // 로그인 액티비티로 이동
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        dialog.show()
    }

    /**
     * 로그아웃 실패 시 실패 다이얼로그 표시하는 메서드
     */
    private fun showLogoutFailDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("로그아웃 실패")
            .setMessage("로그아웃 정보를 다시 입력해주세요.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}