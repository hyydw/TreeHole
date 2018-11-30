package com.ydw.multicomponent

import android.content.Context
import android.os.Debug
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.orhanobut.logger.Logger
import com.ydw.base.LogTag

@Interceptor(priority = 20,name = "登录状态拦截器")
class LoginInterceptor: IInterceptor{
    private lateinit var context: Context

    override fun init(context: Context) {
        this.context = context
    }

    override fun process(postcard: Postcard, callback: InterceptorCallback) {
        Logger.d(LogTag.APP,"进行了拦截")
        callback.onContinue(postcard)  // 处理完成，交还控制权

    }

}