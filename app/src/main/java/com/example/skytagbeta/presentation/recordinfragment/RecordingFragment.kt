package com.example.skytagbeta.presentation.recordinfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.skytagbeta.R
import com.example.skytagbeta.databinding.FragmentRecordingBinding
import com.example.skytagbeta.presentation.main.MainActivity
import com.example.skytagbeta.presentation.recordinfragment.fragmentviewmodel.RecordFragmentViewModel

class RecordingFragment : Fragment() {
    private lateinit var binding: FragmentRecordingBinding
    private var mActivity: MainActivity? = null
    private val recordFragmentViewModel: RecordFragmentViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentRecordingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = "Record"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                requireActivity().onBackPressedDispatcher.onBackPressed()
                mActivity?.onBackPressed()
                true
            }
            else ->super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        setHasOptionsMenu(false)
        recordFragmentViewModel.showButtons(true)
        super.onDestroy()
    }
}