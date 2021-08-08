package com.plcoding.streamchatapp.ui.channel

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.plcoding.streamchatapp.R
import com.plcoding.streamchatapp.databinding.DialogChannelNameBinding
import com.plcoding.streamchatapp.databinding.FragmentChannelBinding
import com.plcoding.streamchatapp.ui.BindingFragment
import com.plcoding.streamchatapp.util.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

@AndroidEntryPoint
class ChannelsFragment : BindingFragment<FragmentChannelBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChannelBinding::inflate

    val TAG_SHOW_DIALOG = "SHOW_DIALOG"
    val TAG_STRING_DIALOG = "STRING_DIALOG"


    val viewModel by activityViewModels<ChannelViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.user == null) {
            findNavController().popBackStack()
            return
        } else {

            if (savedInstanceState != null) {
                if (savedInstanceState.getBoolean(TAG_SHOW_DIALOG, false)) {
                    showNewGroupDialog(savedInstanceState.getString(TAG_STRING_DIALOG, ""))
                }
            }

            binding.apply {
                val channelListViewModel by viewModels<ChannelListViewModel> {
                    ChannelListViewModelFactory(
                        filter = Filters.and(
                            Filters.eq("type", "messaging"),
                        ),
                        sort = ChannelListViewModel.DEFAULT_SORT,
                        limit = 10
                    )
                }
                channelListViewModel.bindView(channelListView, viewLifecycleOwner)

                channelListView.setChannelItemClickListener {
                    findNavController().navigate(
                        ChannelsFragmentDirections.actionChannelsFragmentToMessageFragment(
                            channelID = it.cid
                        )
                    )
                }

                fab.setOnClickListener {
                    showNewGroupDialog(null)

                }
                toolbar.apply {
                    inflateMenu(R.menu.menu)
                    setOnMenuItemClickListener {
                        if (it.title == "Logout") {
                            viewModel.logout();
                            findNavController().navigate(ChannelsFragmentDirections.actionChannelsFragmentToLoginFragment())
                            true
                        } else {
                            false
                        }
                    }
                }
            }
        }
        setHasOptionsMenu(true)
    }

    private fun showNewGroupDialog(channelName: String?) {

        val binding = DialogChannelNameBinding.inflate(LayoutInflater.from(requireContext()))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_channel_name)
            .setView(binding.root)
            .setPositiveButton(R.string.create) { _, _ ->
                viewModel.createChannel(binding.etChannelName.text.toString())
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create().show()


    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lifecycleScope.launchWhenStarted {
            viewModel.newChannelDialogAppearing.collect {
                outState.putBoolean(TAG_SHOW_DIALOG, it)
                outState.putString(TAG_STRING_DIALOG, viewModel.newGroupName.value)
            }
        }

    }

}