package com.sbsj.dreamwing.admin.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.databinding.ActivityAdminBinding
import com.sbsj.dreamwing.volunteer.ui.VolunteerCertificationActivity

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.adminToolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.adminToolbar.toolbar.title = "관리자 페이지"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.volunteer_board -> {
//                startActivity(Intent(this, VolunteerBoardActivity::class.java))
//                return true
//            }
            R.id.volunteer_request -> {
                switchFragment(VolunteerRequestListFragment(), "봉사활동 신청 대기 목록")
                return true
            }
            R.id.volunteer_certification -> {
                switchFragment(VolunteerCertificationListFragment(), "봉사활동 인증 대기 목록")
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun switchFragment(fragment: Fragment, title: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        supportActionBar?.title = title
    }

}
