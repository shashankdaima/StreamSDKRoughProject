package com.plcoding.streamchatapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.streamchatapp.util.MIN_USERNAME_LENGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {

    private val _eventChannel = Channel<Events>();
    val event = _eventChannel.receiveAsFlow()
    private lateinit var devToken: String

    private fun isValidUsername(username: String) = username.length > MIN_USERNAME_LENGHT

    private lateinit var user: User

    init {
        if (client.getCurrentUser() != null) {
            viewModelScope.launch {
                _eventChannel.send(Events.AlreadyLoggedIn)
            }
        }
    }

    fun connectUser(username: String) {
        val trimmedUsername = username.trim()
        viewModelScope.launch {
            if (isValidUsername(trimmedUsername)) {
                val result = client.connectGuestUser(trimmedUsername, trimmedUsername).await()
                if (result.isError) {
                    result.error().message?.let { it1 ->
                        Events.StreamError(
                            it1
                        )
                    }?.let { it2 -> _eventChannel.send(it2) }
                } else {
                    _eventChannel.send(Events.Success)
                }
            } else {
                _eventChannel.send(Events.InvalidUsername)
            }
        }
    }
}

sealed class Events {
    object InvalidUsername : Events()
    object Success : Events()
    class StreamError(val message: String) : Events()
    object AlreadyLoggedIn:Events()

}