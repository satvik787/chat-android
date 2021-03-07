package com.satvik.chat


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.satvik.chat.api.Channel
import com.satvik.chat.api.Res
import com.satvik.chat.api.User

class ChannelFragment:Fragment(),ChatRepository.Callback {

    private val viewModel:ChannelViewModel by lazy {
        ViewModelProvider(this).get(ChannelViewModel::class.java)
    }
    private var navigation:Navigation? = null
    private  var adapter: ChannelAdapter = ChannelAdapter(listOf())
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var alertDialog:AlertDialog

    private var userId:Int? = null

    companion object{
        const val USER_ID = "user_id"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigation = context as Navigation
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChatRepository.isActive = true
        arguments?.let {
            userId = it.getInt(USER_ID)
            viewModel.getChannels(this.userId!!)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.fragment_channel,menu)
        val searchItem: MenuItem = menu.findItem(R.id.search_channels)
        val searchView = searchItem.actionView as SearchView
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    viewModel.getUsers(queryText)
                    progressBar.visibility = View.VISIBLE
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.refresh){
            progressBar.visibility = View.VISIBLE
            viewModel.getChannels(userId!!)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_channel,container,false)
        this.adapter = ChannelAdapter(mutableListOf())
        recyclerView = view.findViewById<RecyclerView>(R.id.channels).apply {
            this.adapter = this@ChannelFragment.adapter
            this.layoutManager = LinearLayoutManager(this@ChannelFragment.requireContext())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = view.findViewById(R.id.progress_bar)
        viewModel.allChannel?.observe(viewLifecycleOwner,{
            progressBar.visibility = View.GONE
            if (it.status == 1){
                Toast.makeText(
                    this@ChannelFragment.requireContext(),
                    it.msg,
                    Toast.LENGTH_LONG
                ).show()
            }else{
                println("PISS")
                it.data?.let { list ->
                    adapter.mutableList = list
                    adapter.notifyDataSetChanged()
                }
            }
        })
        viewModel.searchUsers.observe(viewLifecycleOwner,{
            progressBar.visibility = View.GONE
            it?.let {
                alertDialog = AlertDialog.Builder(this.requireContext()).apply{
                    val r = RecyclerView(this@ChannelFragment.requireContext())
                    r.adapter = UserAdapter(it)
                    r.layoutManager = LinearLayoutManager(this@ChannelFragment.requireContext())
                    setView(r)
                }.create()
                alertDialog.show()
            }
        })
    }

    override fun onComplete(it: Res<Int>){
        progressBar.visibility = View.GONE
        if(it.status == 1) {
            Toast.makeText(
                this@ChannelFragment.requireContext(),
                it.msg,
                Toast.LENGTH_LONG
            ).show()
        }else{
            it.data?.let { id ->
                navigation?.onTransfer(userId!!,id)
            }
        }
    }

    inner class ChannelAdapter(var mutableList: List<Channel>):RecyclerView.Adapter<ChannelViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
            val view = this@ChannelFragment.layoutInflater.inflate(
                R.layout.item_channel,
                parent,
                false
            )
            return ChannelViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
            holder.bind(mutableList[position])
        }

        override fun getItemCount() = mutableList.size

    }

    inner class ChannelViewHolder(view: View)
        :RecyclerView.ViewHolder(view),View.OnClickListener {
        init{
            itemView.findViewById<ImageView>(R.id.user_two_image).setImageResource(R.mipmap.user)
            view.setOnClickListener(this)
        }
        private var obj:Channel? = null
        private val userName: TextView = itemView.findViewById(R.id.user_two_name)

        fun bind(channel: Channel) {
            obj = channel
            userName.text = channel.userTwoName
        }


        override fun onClick(v: View?) {
            navigation?.onTransfer(userId!!,obj?.channelId!!)
        }
    }



    interface Navigation{
        fun onTransfer(userId: Int,channelId: Int)
    }

    inner class UserAdapter(var mutableList: List<User>):RecyclerView.Adapter<UserViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = this@ChannelFragment.layoutInflater.inflate(
                R.layout.item_channel,
                parent,
                false
            )
            return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            holder.bind(mutableList[position])
        }

        override fun getItemCount() = mutableList.size

    }

    inner class UserViewHolder(view: View)
        :RecyclerView.ViewHolder(view),View.OnClickListener {
        init{
            itemView.findViewById<ImageView>(R.id.user_two_image).setImageResource(R.mipmap.user)
            view.setOnClickListener(this)
        }
        private var obj:User? = null
        private val userName: TextView = itemView.findViewById(R.id.user_two_name)

        fun bind(user: User) {
            obj = user
            userName.text = user.userName
        }


        override fun onClick(v: View?) {
            alertDialog.dismiss()
            progressBar.visibility = View.VISIBLE
            viewModel.createChannel(this@ChannelFragment.userId!!,obj?.userId!!,this@ChannelFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatRepository.isActive = false
    }
}