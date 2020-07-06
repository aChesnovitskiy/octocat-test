package com.achesnovitskiy.octocattest.viewmodels.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.achesnovitskiy.octocattest.data.Repo

class ReposViewModel : ViewModel() {
    private val state = MutableLiveData<ReposState>().apply {
        value = ReposState()
    }
    private val repos = MutableLiveData<List<Repo>>()

    fun getState(): LiveData<ReposState> = state

    fun getRepos(): LiveData<List<Repo>> {
        val result = MediatorLiveData<List<Repo>>()

        val filterF = {
            val query = state.value?.searchQuery ?: ""
            val repos = repos.value!!

            result.value = if (query.isEmpty()) repos
            else repos.filter { it.name.contains(query, true) }
        }

        result.addSource(repos) { filterF.invoke() }
        result.addSource(state) { filterF.invoke() }

        return result
    }

    fun loadReposFromApi(user: String) {
        repos.value = listOf(
            Repo(1, "Hello World!"),
            Repo(2, "Repo 2 $user"),
            Repo(2, "Repo 3 $user"),
            Repo(2, "Repo 4 $user"),
            Repo(2, "Repo 5 $user")
        )
    }

    fun handleSearchQuery(query: String?) {
        query ?: return
        updateState { it.copy(searchQuery = query) }
    }

    fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch) }
    }

    private fun updateState(update: (currentState: ReposState) -> ReposState) {
        val updatedState = update(state.value!!)
        state.value = updatedState
        Log.d("My_ReposViewModel", "${state.value}")
    }
}

data class ReposState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true
)