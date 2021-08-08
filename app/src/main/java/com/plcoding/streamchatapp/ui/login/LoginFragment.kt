package com.plcoding.streamchatapp.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.DataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.plcoding.streamchatapp.databinding.FragmentLoginBinding
import com.plcoding.streamchatapp.ui.BindingFragment
import com.plcoding.streamchatapp.util.makeSnackBar
import com.plcoding.streamchatapp.util.makeToast
import com.plcoding.streamchatapp.util.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.core.internal.exhaustive
import kotlinx.coroutines.flow.collect
import java.util.prefs.Preferences
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment() : BindingFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate
    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpChannelListener()


        binding.apply {
            btnConfirm.setOnClickListener {
                viewModel.connectUser(
                    etUsername.text.toString()
                )
                progressBar.isVisible = true
                btnConfirm.isEnabled = false
            }
            etUsername.addTextChangedListener {
                tilUsername.error = null;
            }
        }

    }

    private fun setUpChannelListener() {
        lifecycleScope.launchWhenStarted {
            viewModel.event.collect {
                when (it) {
                    Events.InvalidUsername -> {
                        binding.tilUsername.error = "Invalid Username:Try something big."
                    }
                    is Events.StreamError -> {

                            makeSnackBar(it.message)

                            binding.progressBar.isVisible = false
                            binding.btnConfirm.isEnabled = true
                    }

                    Events.Success -> {
                        context?.makeToast("Successful")
                        findNavController().navigateSafely(LoginFragmentDirections.actionLoginFragmentToChannelsFragment().actionId)

                    }
                    Events.AlreadyLoggedIn -> {
                        findNavController().navigateSafely(LoginFragmentDirections.actionLoginFragmentToChannelsFragment().actionId)


                    }
                }
            }
        }
    }

}