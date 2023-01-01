package com.harbinger.dictation

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * Created by acorn on 2023/1/1.
 */
class App :Application(){
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}