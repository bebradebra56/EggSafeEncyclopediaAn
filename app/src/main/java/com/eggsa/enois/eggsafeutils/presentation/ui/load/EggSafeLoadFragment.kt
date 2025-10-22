package com.eggsa.enois.eggsafeutils.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.collection.buildIntSet
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eggsa.enois.EggSafeActivity
import com.eggsa.enois.MainActivity
import com.eggsa.enois.R
import com.eggsa.enois.databinding.FragmentLoadEggsafeBinding
import com.eggsa.enois.eggsafeutils.data.shar.EggSafeSharedPreference
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class EggSafeLoadFragment : Fragment(R.layout.fragment_load_eggsafe) {
    private lateinit var champbtLoadBinding: FragmentLoadEggsafeBinding

    private val eggSafeLoadViewModel by viewModel<EggSafeLoadViewModel>()

    private val eggSafeSharedPreference by inject<EggSafeSharedPreference>()

    private var url = ""

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navigateToSuccess(url)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                eggSafeSharedPreference.notificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                navigateToSuccess(url)
            } else {
                navigateToSuccess(url)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        champbtLoadBinding = FragmentLoadEggsafeBinding.bind(view)

        champbtLoadBinding.grantButton.setOnClickListener {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            requestNotificationPermission.launch(permission)
            eggSafeSharedPreference.notificationRequestedBefore = true
        }

        champbtLoadBinding.skipButton.setOnClickListener {
            eggSafeSharedPreference.notificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            navigateToSuccess(url)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eggSafeLoadViewModel.crasherHomeScreenState.collect {
                    when (it) {
                        is EggSafeLoadViewModel.EggSafeHomeScreenState.EggSafeLoading -> {

                        }

                        is EggSafeLoadViewModel.EggSafeHomeScreenState.EggSafeError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is EggSafeLoadViewModel.EggSafeHomeScreenState.Success -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val permission = Manifest.permission.POST_NOTIFICATIONS
                                val permissionRequestedBefore = eggSafeSharedPreference.notificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                                    navigateToSuccess(it.data)
                                } else if (!permissionRequestedBefore && (System.currentTimeMillis() / 1000 > eggSafeSharedPreference.notificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    champbtLoadBinding.notiGroup.visibility = View.VISIBLE
                                    champbtLoadBinding.eggSafeBackgroundGroup.visibility = View.VISIBLE
                                    champbtLoadBinding.loadingGroup.visibility = View.GONE
                                    url = it.data
                                } else if (shouldShowRequestPermissionRationale(permission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > eggSafeSharedPreference.notificationRequest) {
                                        champbtLoadBinding.notiGroup.visibility = View.VISIBLE
                                        champbtLoadBinding.eggSafeBackgroundGroup.visibility = View.VISIBLE
                                        champbtLoadBinding.loadingGroup.visibility = View.GONE
                                        url = it.data
                                    } else {
                                        navigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    navigateToSuccess(it.data)
                                }
                            } else {
                                navigateToSuccess(it.data)
                            }
                        }

                        EggSafeLoadViewModel.EggSafeHomeScreenState.NotInternet -> {
                            champbtLoadBinding.loadConnectionStateText.visibility = View.VISIBLE
                            champbtLoadBinding.loadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun navigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_eggSafeLoadFragment_to_eggSafeV,
            bundleOf(EGGSAFE_SPLASH_DATA to data)
        )
    }

    companion object {
        const val EGGSAFE_SPLASH_DATA = "crashSplashData"
    }
}