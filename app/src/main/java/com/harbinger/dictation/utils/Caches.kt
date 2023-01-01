package com.harbinger.dictation.utils

import com.tencent.mmkv.MMKV

/**
 * Created by acorn on 2023/1/1.
 */
object Caches {
    private val mmkv = MMKV.mmkvWithID("FSfs", MMKV.MULTI_PROCESS_MODE)

    var lastSample by mmkv.string()
    var lastDictation by mmkv.string()
}