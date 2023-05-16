package bps.sumsel.st2023.ui.change_password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentChangePasswordBinding
import bps.sumsel.st2023.repository.ResultData

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ChangePasswordViewModelFactory =
            ChangePasswordViewModelFactory.getInstance(requireActivity())
        val viewModel: ChangePasswordViewModel by viewModels { factory }

        parentActivity = requireActivity() as MainActivity

        viewModel.setStatusNull()

        viewModel.resultStatus.observe(viewLifecycleOwner) { result ->
            Log.d("result", result.toString())
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }
                    is ResultData.Success -> {
                        parentActivity.setLoading(false)

                        if (result.data == "success") {
                            Toast.makeText(context, "Password berhasil diubah", Toast.LENGTH_SHORT)
                                .show()

                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                    is ResultData.Error -> {
                        parentActivity.setLoading(false)

                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.errorSamePassword.visibility = View.GONE
        binding.errorMinimalPassword.visibility = View.GONE

        var password = ""
        var passwordConfirm = ""

        binding.btnChangePassword.isEnabled = false
        binding.btnChangePassword.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.gray
            )
        )

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                password = p0.toString()
                checkPassword(password, passwordConfirm)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.edtPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                passwordConfirm = p0.toString()
                checkPassword(password, passwordConfirm)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.btnChangePassword.setOnClickListener {
            viewModel.changePassword(password)
        }
    }

    private fun checkPassword(password: String, passwordConfirm: String) {
        if (password.isNotEmpty() || passwordConfirm.isNotEmpty()) {
            if (password.length < 6) {
                binding.errorMinimalPassword.visibility = View.VISIBLE
            } else {
                binding.errorMinimalPassword.visibility = View.GONE
            }

            if (password != passwordConfirm) {
                binding.errorSamePassword.visibility = View.VISIBLE
            } else {
                binding.errorSamePassword.visibility = View.GONE
            }

            if (password.length < 6 || password != passwordConfirm) {
                binding.btnChangePassword.isEnabled = false
                binding.btnChangePassword.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray
                    )
                )
            } else {
                binding.btnChangePassword.isEnabled = true
                binding.btnChangePassword.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green_900
                    )
                )
            }
        }
    }
}