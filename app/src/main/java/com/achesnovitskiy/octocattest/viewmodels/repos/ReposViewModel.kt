package com.achesnovitskiy.octocattest.viewmodels.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.achesnovitskiy.octocattest.data.Repo

class ReposViewModel : ViewModel() {
    private val repos = MutableLiveData<List<Repo>>()

    fun getRepos() : LiveData<List<Repo>> = repos

    fun loadReposFromApi(user: String) {
        repos.value = listOf(
            Repo(1, "Repo $user"),
            Repo(2, "Repo 2 $user"),
            Repo(2, "Repo 3 $user"),
            Repo(2, "Repo 4 $user"),
            Repo(2, "Repo 5 $user")
        )
    }
}