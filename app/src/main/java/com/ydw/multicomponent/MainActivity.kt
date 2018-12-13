package com.ydw.multicomponent

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.ydw.login.LoginActivity
import com.orhanobut.logger.Logger
import com.ydw.base.ARouterPath
import com.ydw.base.BaseActivity
import com.ydw.base.LogTag

@Route(path = ARouterPath.ACTIVITY_URL_MAIN)
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.t(LogTag.AppTag_App).d("MainActivity_onCreate")
    }

    override fun onResume() {
        super.onResume()

    }
}
