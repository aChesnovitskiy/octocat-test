package com.achesnovitskiy.octocattest.ui.repos

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.achesnovitskiy.octocattest.R
import com.achesnovitskiy.octocattest.data.Repo
import com.achesnovitskiy.octocattest.viewmodels.repos.ReposViewModel
import kotlinx.android.synthetic.main.fragment_repos.*

class ReposFragment : Fragment(R.layout.fragment_repos) {
    private lateinit var reposViewModel: ReposViewModel
    private lateinit var reposAdapter: ReposAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        reposViewModel = ViewModelProvider(this).get(ReposViewModel::class.java)
            .apply {
                loadReposFromApi(USER_OCTOCAT)

                getRepos().observe(
                    viewLifecycleOwner,
                    Observer { repos ->
                        reposAdapter.updateRepos(repos)
                    }
                )
            }
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter { repo ->
            navigateToInfo(repo)
        }

        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        with(repos_recycler_view) {
            adapter = reposAdapter
            layoutManager = LinearLayoutManager(this@ReposFragment.context)
            addItemDecoration(divider)
        }
    }

    private fun navigateToInfo(repo: Repo) {
        this.findNavController()
            .navigate(
                ReposFragmentDirections
                    .actionRepositoriesFragmentToRepoInfoFragment(repo.name)
            )
    }

    companion object {
        const val USER_OCTOCAT = "octocat"
    }
}