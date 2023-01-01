package com.harbinger.dictation

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.harbinger.dictation.databinding.ActivityMainBinding
import com.harbinger.dictation.utils.Caches
import com.harbinger.dictation.utils.SharedPreferencesUtils
import com.harbinger.dictation.utils.UltimateTextSizeUtil

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //状态栏透明
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) //此FLAG可使状态栏透明，且当前视图在绘制时，从屏幕顶端开始即top = 0开始绘制，这也是实现沉浸效果的基础
            this.window.statusBarColor = resources.getColor(R.color.white)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        PushAgent.getInstance(this).onAppStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //设置状态栏黑色字体
            this.window.statusBarColor = resources.getColor(R.color.white)
            binding.root.fitsSystemWindows = true
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE //恢复状态栏白色字体
        }

        if (Caches.lastSample?.isNotEmpty() == true) {
            binding.sampleEt.setText(Caches.lastSample)
        }
        binding.hideIv.setOnClickListener {
            if (binding.sampleEt.alpha == 0f) {
                binding.sampleEt.alpha = 1f;
            } else {
                binding.sampleEt.alpha = 0f;
            }
        }
        binding.checkIv.setOnClickListener {
            checkDictation()
        }
    }

    private fun checkDictation() {
        if (!TextUtils.isEmpty(binding.sampleEt.text.toString())) {
            Caches.lastSample = binding.sampleEt.text.toString()
        }
        resetColor()
        val errorList = ArrayList<String>()
        val samples = binding.sampleEt.text.split(" ").toMutableList()
        trimList(samples)
        val dictations = binding.dictationEt.text.split(" ").toMutableList()
        trimList(dictations)
        for (i in dictations.indices) {
            if (!dictations.get(i).equals(samples.get(i))) {
                Log.d(TAG, "dictation:${dictations.get(i)},${samples.get(i)}")
                errorList.add(dictations.get(i))
            }
        }

        if (errorList.size > 0) {
            binding.dictationEt.setText(
                UltimateTextSizeUtil.getEmphasizedSpannableString(
                    binding.dictationEt.text.toString(),
                    errorList, 0,
                    resources.getColor(R.color.wrong),
                    0
                )
            )
        }
        binding.dictationEt.setSelection(binding.dictationEt.text.length)
    }

    private fun trimList(list: MutableList<String>) {
        val it = list.iterator()
        while (it.hasNext()) {
            val s = it.next()
            if (TextUtils.isEmpty(s)) {
                it.remove()
            }
        }
        list.forEach {
            Log.d(TAG, it)
        }
    }

    private fun resetColor() {
        binding.dictationEt.setText(binding.dictationEt.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!TextUtils.isEmpty(binding.sampleEt.text.toString())) {
            Caches.lastSample = binding.sampleEt.text.toString()
        }
    }
}