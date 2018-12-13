package com.ydw.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter

abstract class BaseActivity  : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
    }

    fun getSharedLoginPref() :SharedPreferences = this.getSharedPreferences(
            getString(R.string.app_login_sharedPreferences), Context.MODE_PRIVATE)

}