package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.MypageFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment(R.layout.mypage_fragment) {

    private lateinit var binding: MypageFragmentBinding
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance() //모름
    }

    fun String?.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this ?: "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMyPageBinding = MypageFragmentBinding.bind(view)
        binding = fragmentMyPageBinding

        fragmentMyPageBinding.signIn.setOnClickListener {
            binding?.let { binding ->
                val email = binding.loginId.text.toString()
                val password = binding.loginPw.text.toString()

                if (auth.currentUser == null) {

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                                //binding.loginId.text = Firebase.auth.currentUser?.uid?.toEditable()
                                    ?: "DefaultUserID".toEditable()
                                //binding.loginId.isEnabled = true


                                val nextIntent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(nextIntent)
                                requireActivity().finish()
                            } else {
                                Toast.makeText(
                                    context,
                                    "로그인에 실패했습니다. 이메일 또는 비밀번호를 다시 확인해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                } else {
                    Toast.makeText(
                        context,
                        "이미 로그인이 되어있습니다. 다시 로그인하고 싶다면 로그아웃을 해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        fragmentMyPageBinding.signOut.setOnClickListener {
            binding?.let { binding ->
                val email = binding.loginId.text.toString()
                val password = binding.loginPw.text.toString()

                if (auth.currentUser != null) {
                    auth.signOut()
                    binding.loginId.text.clear()
                    binding.loginId.isEnabled = true
                    binding.loginPw.text.clear()
                    binding.loginPw.isEnabled = true

                    binding.signIn.text = "로그인"
                    binding.signIn.isEnabled = false
                    Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                    val nextIntent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(nextIntent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, "로그인이 되어있지 않습니다. 우선 로그인을 해주세요.", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // 현재 로그인한 사용자가 있다면 사용자의 email(아이디)를 가져옴
            val userEmail = currentUser.email
            binding.loginId.text = userEmail?.toEditable() ?: "DefaultUserEmail".toEditable()
        }
    }
}

