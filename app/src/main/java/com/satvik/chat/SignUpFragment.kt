package com.satvik.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.satvik.chat.api.Res
import com.satvik.chat.api.User

class SignUpFragment:Fragment() {
    companion object {
        const val TITLE = "title"
    }

    private var navigation: Navigation? = null
    private var isLogin = true
    private lateinit var userName:EditText
    private lateinit var password:EditText
    private lateinit var warningText:TextView
    private lateinit var title:TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        print("ATTACH")
        navigation = context as Navigation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userName = view.findViewById(R.id.username)
        password = view.findViewById(R.id.user_password)
        warningText = view.findViewById(R.id.warningText)
        title = view.findViewById(R.id.title)
        arguments?.let {
            title.text = it.getString(TITLE)
        }
        view.findViewById<TextView>(R.id.change).setOnClickListener {
            val btn = it as TextView
            if (isLogin){
                isLogin = false
                btn.text = this@SignUpFragment.getString(R.string.login)
                title.text = this@SignUpFragment.getString(R.string.signup)
            }else{
                isLogin = true
                btn.text = this@SignUpFragment.getString(R.string.signup)
                title.text = this@SignUpFragment.getString(R.string.login)
            }
            userName.setText("")
            password.setText("")
        }
        view.findViewById<Button>(R.id.submit).setOnClickListener { btn ->
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
            if(userName.text.isNotEmpty() && password.text.isNotEmpty()){
                warningText.text = ""
                progressBar.visibility = View.VISIBLE
                btn.isClickable = false
                val liveData:LiveData<Res<User>>
                if(isLogin) {
                     liveData = ChatRepository.get().requests.login(
                        userName.text.toString(),
                        password.text.toString())
                }else{
                    liveData = ChatRepository.get().requests.signUp(
                        userName.text.toString(),
                        password.text.toString()
                    )
                }
                liveData.observe(viewLifecycleOwner,{
                    if(it.status == 1){
                        warningText.text = it.msg
                        userName.setText("")
                        password.setText("")
                    }else{
                        it.data?.let { user ->
                            ChatRepository.get().addUser(user)
                        }
                        navigation?.transfer()
                    }
                    progressBar.visibility = View.GONE
                    btn.isClickable = true
                })
            }else {
                warningText.text = getString(R.string.field_empty)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        navigation = null
    }

    interface Navigation{
        fun transfer()
    }

}