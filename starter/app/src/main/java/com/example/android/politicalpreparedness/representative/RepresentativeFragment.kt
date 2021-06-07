package com.example.android.politicalpreparedness.representative

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.databinding.FragmentDetailBinding
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        private val REQUEST_LOCATION_PERMISSION = 1
    }

    val representativeViewModel by lazy {
        ViewModelProvider(this, RepresentativeViewModelFactory()).get(
                RepresentativeViewModel::class.java
        )
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val adapter = RepresentativeListAdapter()
        binding.representativesList.adapter = adapter
        binding.representativesList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        representativeViewModel.getLocation.observe(viewLifecycleOwner, Observer {
            checkLocationPermissions()
        })
        representativeViewModel.officials.observe(viewLifecycleOwner, Observer { officials ->
            adapter.submitList(officials)
            hideKeyboard()
        })

        binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                representativeViewModel.state.value = parent?.getItemAtPosition(position) as String
            }

        }
        binding.viewModel = representativeViewModel
        return binding.root

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                checkLocationPermissions()
            } else {
                // Permission denied.
                Snackbar.make(
                        requireView(),
                        R.string.permission_denied_explanation_settings,
                        Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.settings) {
                    // Displays App settings screen.
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation
                    .addOnCompleteListener { task ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData(fusedLocationClient)
                        } else {
                            getLocation(location)
                        }
                    }

        } else {
            requestPermissions(
                    arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(fusedLocationClient: FusedLocationProviderClient) {
        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                getLocation(locationResult.lastLocation)
            }
        }

        with(LocationRequest()) {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
            fusedLocationClient.requestLocationUpdates(this, locationCallback, Looper.myLooper())
        }
    }

    private fun getLocation(location: Location) {
        val address = geoCodeLocation(location)
        representativeViewModel.useMyLocation.value = address
        representativeViewModel.line1.value = address.line1
        representativeViewModel.line2.value = address.line2
        representativeViewModel.state.value = address.state
        representativeViewModel.city.value = address.city
        representativeViewModel.zip.value = address.zip
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}