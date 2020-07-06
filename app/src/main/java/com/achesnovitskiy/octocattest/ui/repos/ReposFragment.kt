package com.achesnovitskiy.octocattest.ui.repos

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.achesnovitskiy.octocattest.R
import com.achesnovitskiy.octocattest.data.Repo
import com.achesnovitskiy.octocattest.viewmodels.repos.ReposState
import com.achesnovitskiy.octocattest.viewmodels.repos.ReposViewModel
import kotlinx.android.synthetic.main.fragment_repos.*


class ReposFragment : Fragment(R.layout.fragment_repos) {
    private lateinit var reposViewModel: ReposViewModel
    private lateinit var reposAdapter: ReposAdapter
    private val binding: ReposBinding by lazy { ReposBinding() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()
        setupProgressBar()
        setupViewModel()
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.queryHint = getString(R.string.search)

        if (binding.isSearch) {
            searchItem.expandActionView()
            searchView.setQuery(binding.searchQuery, false)
        }

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                reposViewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                reposViewModel.handleSearchMode(false)
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                reposViewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                reposViewModel.handleSearchQuery(newText)
                return true
            }
        })

        searchView.setOnCloseListener {
            reposViewModel.handleSearchMode(false)
            true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        reposViewModel.disposeDisposables()
        super.onDestroy()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(repos_toolbar)
    }

    private fun setupProgressBar() {
        // Set progress bar color for API < 21
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val drawableProgress =
                DrawableCompat.wrap(repos_progress_bar.indeterminateDrawable)

            DrawableCompat.setTint(
                drawableProgress,
                ContextCompat.getColor((activity as AppCompatActivity), R.color.color_accent)
            )

            repos_progress_bar.indeterminateDrawable = DrawableCompat.unwrap(drawableProgress)
        }

        binding.isLoading
    }

    private fun setupViewModel() {
        reposViewModel = ViewModelProvider(this).get(ReposViewModel::class.java)
            .apply {
                getState().observe(
                    viewLifecycleOwner,
                    Observer { state ->
                        binding.bind(state)
                    }
                )

                getRepos(USER_OCTOCAT).observe(
                    viewLifecycleOwner,
                    Observer { repos ->
                        repos_list_is_empty_text_view.visibility =
                            if (repos.isNullOrEmpty()) View.VISIBLE else View.GONE
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

    inner class ReposBinding {
        var searchQuery: String? = null
        var isSearch: Boolean = false
        var isLoading: Boolean = false

        fun bind(state: ReposState) {
            isSearch = state.isSearch
            searchQuery = state.searchQuery
            isLoading = state.isLoading
        }
    }
}