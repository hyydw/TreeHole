package com.example.ydw.login

import android.app.Application
import com.ydw.base.BaseApp

class LoginApp :BaseApp(){

    override fun onCreate() {
        super.onCreate()
        initModuleApp(this)
        initModuleData(this)
    }

    override fun initModuleData(application: Application) {

    }

    override fun initModuleApp(application: Application) {

    }

}