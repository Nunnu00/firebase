package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.myapplication.DBKey.Companion.CHILD_CHAT
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.example.myapplication.DBKey.Companion.DB_USER

class ProductActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    val userDB = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val articleModel: ArticleModel = intent.getSerializableExtra("articleModel") as ArticleModel

        bind(articleModel)
        val button = findViewById<Button>(R.id.chatButton).setOnClickListener {
            val chatRoom = ChatModel(
                buyerId = auth.currentUser!!.uid,
                sellerId = articleModel.sellerId,
                itemTitle = articleModel.title,
                key = System.currentTimeMillis()
            )

            userDB.child(auth.currentUser!!.uid)
                .child(CHILD_CHAT)
                .push()
                .setValue(chatRoom)

            userDB.child(articleModel.sellerId)
                .child(CHILD_CHAT)
                .push()
                .setValue(chatRoom)

            AlertDialog.Builder(this)
                .setTitle("상상부기")
                .setMessage("채팅방이 생성되었어요.\n채팅방 목록에서 확인해주세요!")
                .setPositiveButton("확인") { _, _ ->
                    finish()
                }
                .create()
                .show()
        }

    }
    private fun bind(articleModel: ArticleModel) {
        val titleEditText = findViewById<TextView>(R.id.titleEditText)
        val priceEditText = findViewById<TextView>(R.id.priceEditText)
        val informationText = findViewById<TextView>(R.id.informationText)
        val filterText = findViewById<TextView>(R.id.filterEditText)

        titleEditText.text = "글 제목: " + articleModel.title
        priceEditText.text = "가격: " + articleModel.price
        informationText.text = "제품 정보: " + articleModel.information
        filterText.text = "판매 여부: " + articleModel.filter
    }
}