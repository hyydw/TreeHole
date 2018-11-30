package com.ydw.multicomponent

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.orhanobut.logger.Logger
import com.ydw.base.ARouterPath
import com.ydw.base.BaseActivity
import com.ydw.base.LogTag

@Route(path = ARouterPath.ACTIVITY_URL_PRELOGIN)
class PreLoginActivity :BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prelogin)
        Logger.d(LogTag.APP,"PreLoginActivity_onCreate")
    }

    override fun onResume() {
        super.onResume()
        val handler = Handler()
        val runnable = Runnable {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_URL_MAIN).navigation()

            //this.startActivity(intent)
        }
        handler.postDelayed(runnable,3000)

    }
}