package com.sbsj.dreamwing.mission.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.databinding.FragmentMissionBinding
import com.sbsj.dreamwing.user.LoginActivity
import com.sbsj.dreamwing.user.MyVolunteerDetailActivity
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import com.sbsj.dreamwing.volunteer.ui.VolunteerCertificationActivity

/**
 * 미션 프래그먼트
 * @author 정은지
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01  	정은지       최초 생성
 * </pre>
 */
class MissionFragment : Fragment() {

    private var _binding: FragmentMissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 로그인 여부 확인 메서드
     */
    private fun checkUserLoggedIn(): Boolean {
        val jwtToken = SharedPreferencesUtil.getToken(requireContext())
        if (jwtToken.isNullOrEmpty()) {
            showLoginRequestDialog()
            return false
        }
        return true
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardWalk.setOnClickListener {
            val intent = Intent(activity, WalkActivity::class.java)
            startActivity(intent)
        }

        binding.cardQuiz.setOnClickListener {
            val intent = Intent(activity, QuizActivity::class.java)
            startActivity(intent)
        }

        binding.cardVolunteer.setOnClickListener {
            if (checkUserLoggedIn()) {
                val intent = Intent(activity, MyVolunteerDetailActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}