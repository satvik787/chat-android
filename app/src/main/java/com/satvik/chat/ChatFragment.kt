package com.satvik.chat

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.satvik.chat.api.Chat


class ChatFragment:Fragment(){

    private val viewModel:ChatViewModel by lazy{
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    private val thread = BackgroundThread()

    private lateinit var adapter:ChatAdapter
    private lateinit var recyclerView:RecyclerView

    private var userId:Int? = null
    private var chain:Double? = null
    private var channelId:Int? = null
    companion object {
        const val USER_ID = "user_id"
        const val CHANNEL_ID = "channel_id"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            userId = it.getInt(USER_ID)
            channelId = it.getInt(CHANNEL_ID)
        }
        lifecycle.addObserver(thread)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        adapter = ChatAdapter(mutableListOf())
        val view = inflater.inflate(R.layout.fragment_chat,container,false)
        recyclerView = view.findViewById<RecyclerView>(R.id.messages_recycler).apply {
            layoutManager = LinearLayoutManager(this@ChatFragment.requireContext())
            adapter = this@ChatFragment.adapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        thread.getMessages(userId!!,channelId!!,viewModel.liveData)
        viewModel.liveData.observe(viewLifecycleOwner,{
            view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
            it?.let {
                if(it.status == 1){
                    Toast.makeText(requireContext(),it.msg,Toast.LENGTH_LONG).show()
                }else{
                    it.data?.let { list ->
                        adapter.list = list
                        adapter.notifyDataSetChanged()
                        if (adapter.list.size > 0){
                            recyclerView.smoothScrollToPosition(adapter.list.size - 1)
                        }
                    }
                }
            }

        })

        view.findViewById<ImageButton>(R.id.btn_send).setOnClickListener {
            val text = view.findViewById<TextView>(R.id.message_box)
            thread.postMessage(userId!!,channelId!!,text.text.toString())
            text.text = ""
        }
    }

    inner class ChatAdapter(var list: List<Chat>):RecyclerView.Adapter<ChatViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            return ChatViewHolder(
                layoutInflater.inflate(
                    R.layout.item_chat,
                    parent,
                    false)
            )
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount() = list.size

    }

    inner class ChatViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val message = view.findViewById<TextView>(R.id.message)
        private val time = view.findViewById<TextView>(R.id.time)

        fun bind(chat: Chat){
            message.text = chat.msg
            time.text = chat.sentAt
            userId?.let {
                if (chat.userID != it){
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                    params.gravity = Gravity.START
                    message.layoutParams = params
                    time.layoutParams = params
                }else{
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                    params.gravity = Gravity.END
                    message.layoutParams = params
                    time.layoutParams = params
                }

            }

        }
    }
}