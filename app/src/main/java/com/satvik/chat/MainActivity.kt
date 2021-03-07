package com.satvik.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(),SignUpFragment.Navigation,ChannelFragment.Navigation {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userId = ChatRepository.get().geUserId()
        if (userId < 0){
            val fragment = supportFragmentManager.findFragmentById(R.id.host)
            if(fragment == null){
                val signUpFragment = SignUpFragment().apply {
                    arguments = Bundle()
                    arguments?.putString(SignUpFragment.TITLE,this@MainActivity.getString(R.string.login))
                }
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.host,signUpFragment)
                    .commit()
            }
        }else{
            supportFragmentManager
                .beginTransaction()
                .add(R.id.host,addChannelFragment(userId))
                .commit()
        }
    }

    override fun transfer() {
        val userId = ChatRepository.get().geUserId()
        if(userId > 0) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.host, addChannelFragment(userId))
                .commit()
        }
    }

    private fun addChannelFragment(userId: Int):ChannelFragment{
        return ChannelFragment().apply {
                arguments = Bundle().apply {
                putInt(ChannelFragment.USER_ID,userId)
            }
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 0){
            supportFragmentManager.popBackStackImmediate()
        }else{
            super.onBackPressed()
        }
    }

    override fun onTransfer(userId: Int, channelId: Int) {
        val frag = ChatFragment().apply {
            arguments = Bundle()
            arguments?.putInt(ChatFragment.USER_ID,userId)
            arguments?.putInt(ChatFragment.CHANNEL_ID,channelId)
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.host,frag)
            .addToBackStack("chat")
            .commit()
    }
}