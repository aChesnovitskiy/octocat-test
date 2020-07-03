package com.achesnovitskiy.octocattest.ui.repoinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.achesnovitskiy.octocattest.R
import com.achesnovitskiy.octocattest.data.Repo
import com.achesnovitskiy.octocattest.viewmodels.repoinfo.RepoInfoViewModel
import kotlinx.android.synthetic.main.fragment_repo_info.*

class RepoInfoFragment : Fragment(R.layout.fragment_repo_info) {
    private lateinit var repoInfoViewModel: RepoInfoViewModel
    private val repoName by lazy {
        arguments?.get("repo_name") as String
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
        repoInfoViewModel = ViewModelProvider(this).get(RepoInfoViewModel::class.java)
            .apply {
                setRepoName(repoName)

                getRepoName().observe(
                    viewLifecycleOwner,
                    Observer { repoName ->
                        repo_name_text_view.text = repoName
                    }
                )
            }
    }
}