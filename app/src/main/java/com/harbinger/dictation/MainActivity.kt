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
import com.harbinger.dictation.dialog.NormalDialogBuilder
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
        if (Caches.lastDictation?.isNotEmpty() == true) {
            binding.dictationEt.setText(Caches.lastDictation)
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
        saveCaches()
        resetColor()
        val sbDebug = StringBuilder()
        val errorList = ArrayList<String>()
        val samples = binding.sampleEt.text.split(" ").toMutableList()
        sbDebug.append("sample:\n${trimList(samples)}")
        val dictations = binding.dictationEt.text.split(" ").toMutableList()
        sbDebug.append("dictation:\n${trimList(dictations)}\ncheck:\n")

        for (i in dictations.indices) {
            var d = dictations.get(i).replace("\n", "")
            d = d.replace(" ", "")
            d=d.replace("\t","")
            d=d.replace("&#160;","")
            var s = samples.get(i).replace("\n", "")
            s = s.replace(" ", "")
            s=s.replace("\t","")
            s=s.replace("&#160;","")
            if (!d.equals(s)) {
                Log.d(TAG, "dictation:$d,$s")
                sbDebug.append("$d!=$s|")
//                binding.dictationEt.setText(
//                    binding.dictationEt.text.toString() + "\n" + "dictation:$d,$s."
//                )
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

        showLogDialog(sbDebug.toString())
    }

    private fun showLogDialog(log: String) {
        return
        NormalDialogBuilder(this)
            .setMessage(log)
            .setOkText("OK")
            .create()
            .show()
    }

    private fun trimList(list: MutableList<String>): String {
        val it = list.iterator()
        while (it.hasNext()) {
            var s = it.next()
            if (s.equals("\n") || TextUtils.isEmpty(s)) {
                it.remove()
            }
        }
        val sb = StringBuilder()
        list.forEach {
            Log.d(TAG, it)
            sb.append("$it,")
        }
        return sb.toString()
    }

    private fun resetColor() {
        binding.dictationEt.setText(binding.dictationEt.text.toString())
    }

    private fun saveCaches() {
        if (!TextUtils.isEmpty(binding.sampleEt.text.toString())) {
            Caches.lastSample = binding.sampleEt.text.toString()
        }
        if (!TextUtils.isEmpty(binding.dictationEt.text.toString())) {
            Caches.lastDictation = binding.dictationEt.text.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveCaches()
    }
}