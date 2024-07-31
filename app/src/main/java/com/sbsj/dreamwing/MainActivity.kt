//package net.developia.helloworld
//// MainActivity.kt
//import RetrofitClient
//import UserInfo
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // 사용자 ID로 사용자 정보 가져오기
//        RetrofitClient.instance.getUserById(1).enqueue(object : Callback<UserInfo> {
//            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
//                if (response.isSuccessful) {
//                    val user = response.body()
//                    Log.d("MainActivity", "User: $user")
//                }
//            }
//
//            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
//                Log.e("MainActivity", "Error: ${t.message}")
//            }
//        })
//
//        // 모든 사용자 정보 가져오기
//        RetrofitClient.instance.getAllUsers().enqueue(object : Callback<List<UserInfo>> {
//            override fun onResponse(call: Call<List<UserInfo>>, response: Response<List<UserInfo>>) {
//                if (response.isSuccessful) {
//                    val users = response.body()
//                    Log.d("MainActivity", "Users: $users")
//                }
//            }
//
//            override fun onFailure(call: Call<List<UserInfo>>, t: Throwable) {
//                Log.e("MainActivity", "Error: ${t.message}")
//            }
//        })
//    }
//}
package com.sbsj.dreamwing



import com.sbsj.dreamwing.data.api.RetrofitClient;
import com.sbsj.dreamwing.data.model.UserInfo;

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var tvUserInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvUserInfo = findViewById(R.id.tvUserInfo)

        // 사용자 ID로 사용자 정보 가져오기
        RetrofitClient.instance.getUserById(1).enqueue(object : Callback<UserInfo> {
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    Log.d("MainActivity", "User: $user")
                    user?.let {
                        tvUserInfo.text = "ID: ${it.userId}\n" +
                                "Login ID: ${it.loginId}\n" +
                                "Password: ${it.password}\n" +
                                "Name: ${it.name}\n" +
                                "Phone: ${it.phone}\n" +
                                "Total Point: ${it.totalPoint}\n" +
                                "Withdraw: ${it.withdraw}\n" +
                                "Profile Image URL: ${it.profileImageUrl}\n" +
                                "Created Date: ${it.createdDate}"
                    }
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                tvUserInfo.text = "Failed to load user info"
            }
        })

        // 모든 사용자 정보 가져오기 (예제용, 필요 시 주석 해제)
        // RetrofitClient.instance.getAllUsers().enqueue(object : Callback<List<UserInfo>> {
        //     override fun onResponse(call: Call<List<UserInfo>>, response: Response<List<UserInfo>>) {
        //         if (response.isSuccessful) {
        //             val users = response.body()
        //             Log.d("MainActivity", "Users: $users")
        //         }
        //     }

        //     override fun onFailure(call: Call<List<UserInfo>>, t: Throwable) {
        //         Log.e("MainActivity", "Error: ${t.message}")
        //     }
        // })
    }
}