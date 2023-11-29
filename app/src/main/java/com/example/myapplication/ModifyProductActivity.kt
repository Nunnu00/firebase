package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.example.myapplication.DBKey.Companion.DB_ARTICLES
import com.example.myapplication.DBKey.Companion.DB_USER
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class ModifyProductActivity: AppCompatActivity() {

    private val articleList = mutableListOf<ArticleModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_product)

        val articleModel: ArticleModel = intent.getSerializableExtra("articleModel") as ArticleModel
        val articleKey: String? = intent.getStringExtra("articleKey")

        //Toast.makeText(this, articleModel.itemId, Toast.LENGTH_SHORT).show()
        Log.d("ModifyProductActivity", "articleModel: $articleModel")
        Log.d("ModifyProductActivity", "Received articleModel: $articleModel, articleKey: $articleKey")

        bind(articleModel)

        val currentUser = Firebase.auth.currentUser
        val userEmail = currentUser?.email
        val email = userEmail.toString()

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            // 수정된 데이터를 Firebase Realtime Database에 업데이트
            modifyArticle(
                articleModel.sellerId,
                findViewById<EditText>(R.id.titleEditText).text.toString(),
                findViewById<EditText>(R.id.priceEditText).text.toString(),
                "",
                articleModel.itemId,
                findViewById<EditText>(R.id.informationText).text.toString(),
                findViewById<EditText>(R.id.filterText).text.toString(),
                email
            )
            articleList.clear()
            finish()
        }

    }
    private fun bind(articleModel: ArticleModel) {
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val priceEditText = findViewById<EditText>(R.id.priceEditText)
        val informationText = findViewById<EditText>(R.id.informationText)
        val filterText = findViewById<EditText>(R.id.filterText)

        titleEditText.setText(articleModel.title)
        priceEditText.setText(articleModel.price)
        informationText.setText(articleModel.information)
        filterText.setText(articleModel.filter)
    }

    private fun modifyArticle(
        sellerId: String,
        title: String,
        price: String,
        imageUrl: String,
        itemId: String,
        information: String,
        filter: String,
        email: String
    ) {
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), price, imageUrl, itemId, information, filter, email)

        val articleModel: ArticleModel = intent.getSerializableExtra("articleModel") as ArticleModel

        val articleRef = FirebaseDatabase.getInstance().getReference("Articles").child(articleModel.itemId)

        articleRef.setValue(model)
            .addOnSuccessListener {
                hideProgress()
                Toast.makeText(this, "판매글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                hideProgress()
                Toast.makeText(this, "수정에 실패했습니다. 오류: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }
}