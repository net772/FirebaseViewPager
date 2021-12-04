package com.example.firebaseviewpager

import android.util.Log
import com.example.firebaseviewpager.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getLayoutResourceId() = R.layout.activity_main

    override fun initDataBinding() {
        initData()

    }

    override fun initView() {

    }

    private fun initData() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if(it.isSuccessful) {
                val quotes = parseQuotesJson(remoteConfig.getString("quotes"))
                val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")

                displayQuotesPager(quotes, isNameRevealed)

            }
        }
    }

    private fun displayQuotesPager(quotes: List<Quote>, isNameRevealed: Boolean) {
        Log.d("동현"," : $quotes")
        mBinding.viewPager.adapter = QuotesPagerAdapter(
            quotes
        )
    }

    private fun parseQuotesJson(json: String) : List<Quote> {
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>()

        for(index in 0 until  jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)
            jsonObject?.let {
                jsonList = jsonList + it
            }
        }

        return jsonList.map {
            Quote(
                  quote = it.getString("quote"),
                  name = it.getString("name"))
        }
    }
}