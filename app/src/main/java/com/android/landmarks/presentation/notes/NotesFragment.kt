package com.android.landmarks.presentation.notes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.landmarks.R
import com.android.landmarks.databinding.FragmentNotesBinding
import com.android.landmarks.domain.model.Coordinates
import com.android.landmarks.domain.model.Note
import com.android.landmarks.presentation.notesmanager.NotesManagerCallback
import com.android.landmarks.util.PermissionUtil
import com.android.landmarks.util.onQueryTextChanged
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class NotesFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    companion object {

        val FRAGMENT_NAME: String = NotesFragment::class.java.name
        const val DEFAULT_ZOOM_DEVEL = 10f
        const val REQUEST_CODE_LOCATION_PERMISSION = 1234

        @JvmStatic
        fun newInstance() =
            NotesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private lateinit var fragmentLandmarksBinding: FragmentNotesBinding
    private var mCallback: NotesManagerCallback? = null
    private val viewModel: NotesViewModel by viewModels()
    private lateinit var map: GoogleMap
    private var mCurrLocationMarker: Marker? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotesManagerCallback) {
            mCallback = context
        } else throw ClassCastException(context.toString() + "must implement OnLocationManagerCallback!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLandmarksBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false)
        fragmentLandmarksBinding.apply {
            landmarksViewModel = viewModel
            notesFabAddNote.setOnClickListener {
                if (viewModel.isValidCurrentLocation()) {
                    showNoteDialog()
                } else {
                    showLocationDialog()
                }
            }
            notesToolbar.inflateMenu(R.menu.landmarks_menu)
            val searchView =
                notesToolbar.menu.findItem(R.id.menu_main_search).actionView as SearchView
            searchView.onQueryTextChanged {
                viewModel.searchNotes(it)
            }
            val txtSearch =
                searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
            txtSearch.hint = resources.getString(R.string.notes_search_hint)
            txtSearch.setHintTextColor(Color.LTGRAY)
            txtSearch.setTextColor(Color.WHITE)

            notesSwitchMyNotes.setOnCheckedChangeListener { _, isChecked ->
                if (!viewModel.isSearching) {
                    if (isChecked) {
                        txtSearch.setText("")
                        viewModel.getMyNotes()
                    } else {
                        viewModel.loadNotes()
                    }
                }
            }
        }

        viewModel.isLoadingLiveData.observe(
            viewLifecycleOwner
        ) {
            it?.let { loading ->
                fragmentLandmarksBinding.notesProgressBar.visibility =
                    if (loading) View.VISIBLE else View.GONE
            }
        }

        viewModel.isSearchingLiveData.observe(
            viewLifecycleOwner
        ) {
            it?.let { searching ->
                fragmentLandmarksBinding.notesSwitchMyNotes.isChecked = false
                fragmentLandmarksBinding.notesProgressBar.visibility =
                    if (searching) View.VISIBLE else View.GONE
            }
        }

        viewModel.notesReceivedLiveData.observe(
            viewLifecycleOwner
        ) { it ->
            it?.let { list ->
                clearMap()
                updateLandmarks(list)
                viewModel.currentLocation?.let {
                    updateCurrentLocationMark(it)
                }
            }
        }

        viewModel.currentLocationLiveData.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                updateCurrentLocationMark(it)
            }
        }

        // for some reason, I could not find the SupportMapFragment in the viewbinding
        // so we manually find it using fragment manager
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.notes_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return fragmentLandmarksBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(context)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(context)
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(context)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isZoomControlsEnabled = true

        if (PermissionUtil.hasLocationPermissions(requireContext())) {
            map.isMyLocationEnabled = true
            subscribeLocation()
        }

        viewModel.currentLocation?.let {
            updateCurrentLocationMark(it)
        }
        viewModel.loadNotes()
    }


    private fun subscribeLocation() = viewModel.locationLiveData.observe(
        viewLifecycleOwner
    ) {
        it?.let {
            viewModel.currentLocationLiveData.value = it
        }
    }

    private fun clearMap() {
        map.clear()
    }

    private fun updateLandmarks(notes: List<Note>) {
        notes.forEach {
            map.addMarker(
                getNormalMarkerOptions(it)
            ).apply { this?.tag = it.id }
        }
    }

    private fun updateCurrentLocationMark(coordinates: Coordinates) {
        val latLng = LatLng(coordinates.lat, coordinates.lng)

        if (hasCurrentLocationMarker()) {
            mCurrLocationMarker!!.remove()
        } else { // move and zoom to current location
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_DEVEL))
        }

        mCurrLocationMarker = map.addMarker(getCurrentLocationMarkerOptions(latLng))
    }

    private fun hasCurrentLocationMarker(): Boolean {
        return mCurrLocationMarker != null
    }

    private fun getCurrentLocationMarkerOptions(latLng: LatLng): MarkerOptions {
        return MarkerOptions()
            .position(latLng)
            .title(getString(R.string.notes_location_current_title))
            .icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
    }

    private fun getNormalMarkerOptions(note: Note): MarkerOptions {
        return MarkerOptions()
            .position(note.location.toLatLng())
            .title(note.remark)
            .snippet("${note.location}${System.lineSeparator()}created by : ${note.userName}")
    }

    private fun requestPermissions() {
        if (PermissionUtil.hasLocationPermissions(requireContext())) {
            return
        }
        PermissionUtil.requestLocationPermission(this, getString(R.string.notes_permission_require))
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        subscribeLocation()
        viewModel.loadNotes()
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * Informs user that there is no valid location
     */
    private fun showLocationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.notes_invalid_location_title)
        builder.setMessage(R.string.notes_invalid_location_msg)
        builder.setPositiveButton(R.string.create_note_ok_button, null)
        builder.show()
    }

    /**
     * UI to enter notes
     */
    private fun showNoteDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setTitle(R.string.notes_add_notes_title)
        builder.setPositiveButton(getString(R.string.create_note_create)) { _, _ ->
            val note = input.text.toString()
            if (viewModel.isValidNote(note)) {
                viewModel.addNote(note)
            }
        }
        builder.setNegativeButton(getString(R.string.create_note_discard), null)
        val dialog = builder.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }
}
