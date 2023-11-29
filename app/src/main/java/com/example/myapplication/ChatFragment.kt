package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.DBKey.Companion.CHILD_CHAT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.myapplication.databinding.ChatFragmentBinding

class ChatFragment:Fragment(R.layout.chat_fragment) {

    private var binding: ChatFragmentBinding? = null
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatModel>()

    private lateinit var chatDB: DatabaseReference
    val userDB = Firebase.database.reference
    //private lateinit var userDB: DatabaseReference

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatlistBinding = ChatFragmentBinding.bind(view)
        binding = fragmentChatlistBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { ChatRoom ->
            val intent = Intent(requireContext(),ChatRoomActivity::class.java)
            intent.putExtra("chatKey", ChatRoom.key)
            startActivity(intent)

        })

        chatRoomList.clear()


        fragmentChatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter

        if (auth.currentUser == null){
            return
        }

        chatDB = Firebase.database.reference.child(auth.currentUser!!.uid).child(CHILD_CHAT)

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val model = it.getValue(ChatModel::class.java)
                    model ?: return
                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }
}