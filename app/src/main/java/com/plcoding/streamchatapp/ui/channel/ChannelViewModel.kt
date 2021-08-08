package com.plcoding.streamchatapp.ui.channel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    val client: ChatClient
) : ViewModel() {
    fun logout() {
        client.disconnect()
    }


    private val _eventsChannel = Channel<Events>();
    val events = _eventsChannel.receiveAsFlow()
    val user by lazy { client.getCurrentUser() }

    private val _newChannelDialogAppearing = MutableStateFlow(false)

    val newChannelDialogAppearing = _newChannelDialogAppearing.asStateFlow()

    private val _newGroupName = MutableLiveData<String?>()
    val newGroupName: LiveData<String?> = _newGroupName

    fun onNewGroupDialogAppears() {
        _newChannelDialogAppearing.value = true
    }

    fun onNewGroupDialogDisappears() {
        _newChannelDialogAppearing.value = false
        _newGroupName.value = null
    }

    fun createChannel(channelName: String) {
        val trimmedChannelName = channelName.trim()
        viewModelScope.launch {
            if (trimmedChannelName.isEmpty()) {
                _eventsChannel.send(Events.Error("The channel name can't be empty."))
                return@launch
            }
            val result = client.channel(
                channelType = "messaging",
                channelId = UUID.randomUUID().toString()
            ).create(
                mapOf(
                    "name" to trimmedChannelName,
                )
            ).await()
            if (result.isError) {
                _eventsChannel.send(
                    Events.Error(result.error().message ?: "Unknown error")
                )
                return@launch
            }
            _eventsChannel.send(Events.Success)
        }

    }

    sealed class Events {
        class Error(val error: String) : Events()
        object Success : Events()
    }

}