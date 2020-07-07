package com.achesnovitskiy.octocattest.viewmodels.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.achesnovitskiy.octocattest.data.Repo
import com.achesnovitskiy.octocattest.repositories.Repository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ReposViewModel : ViewModel() {
    private val state = MutableLiveData<ReposState>().apply {
        value = ReposState()
    }
    private val repos = MutableLiveData<List<Repo>>(listOf())

    fun getState(): LiveData<ReposState> = state

    fun getRepos(userName: String): LiveData<List<Repo>> {
        loadReposFromApi(userName)

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

    private fun loadReposFromApi(userName: String) {
        Repository.isNeedLoadRepos { isNeed ->
            if (isNeed) {
                updateState { it.copy(isLoading = true) }
                Repository.loadReposFromApi(userName) {reposFromApi ->
                    repos.value = reposFromApi
                    updateState { it.copy(isLoading = false) }
                }
            } else {
                // Idle delay for showing progress bar
                Completable.timer(1500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        updateState { it.copy(isLoading = false) }
                    }
            }
        }
    }

    fun updateRepos(userName: String) {
        updateState { it.copy(isLoading = true) }
        loadReposFromApi(userName)
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
    }

    fun disposeDisposables() {
        Repository.disposeDisposables()
    }
}

data class ReposState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true
)