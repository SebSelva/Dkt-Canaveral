package com.decathlon.canaveral.dashboard.player

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseDialogFragment
import com.decathlon.canaveral.dashboard.DashboardViewModel
import com.decathlon.canaveral.databinding.DialogPlayerEditionBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerEditionFragment: BaseDialogFragment<DialogPlayerEditionBinding>() {
    override var layoutId = R.layout.dialog_player_edition
    val args: PlayerEditionFragmentArgs by navArgs()

    private val dashboardViewModel by viewModel<DashboardViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        _binding.inputPlayerName.setText(args.player.nickname)
        args.player.image?.let {
            Glide.with(requireContext()).load(it).circleCrop().into(_binding.playerImage)
        }

        _binding.optionCamera.setOnClickListener {

        }

        _binding.editionValidate.setOnClickListener {
            val player = args.player
            if (!_binding.inputPlayerName.text.isNullOrEmpty()) {
                player.nickname = _binding.inputPlayerName.text.toString()
            }
            dashboardViewModel.updatePlayer(player)
            findNavController().popBackStack()
        }
        _binding.editionCancel.setOnClickListener {
            dismiss()
        }
    }
}