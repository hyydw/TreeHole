package com.ydw.base

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log.isLoggable
import com.alibaba.android.arouter.launcher.ARouter
import com.orhanobut.logger.*

abstract class BaseApp : Application() {
    /**
     * Application 初始化
     */
    abstract fun initModuleApp(application: Application)

    /**
     * 所有 Application 初始化后的自定义操作
     */
    abstract fun initModuleData(application: Application)

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }

    fun getContext():Context = this

    fun initLogger(tag:String) {

        val formatStrategy = PrettyFormatStrategy.newBuilder()
            //.showThreadInfo(true)
            //.logStrategy(DynamicTagStrategy())
            .tag(tag)
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }

    class DynamicTagStrategy : LogStrategy {
        private val prefix = arrayOf(
            ". ",
            " .")

        private var index = 0

        override fun log(priority: Int, tag: String?, message: String) {
            index = index xor 1
            //Log.println(priority, prefix[index] + tag, message)
        }
    }

}