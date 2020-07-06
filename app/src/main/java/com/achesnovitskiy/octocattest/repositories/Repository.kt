package com.achesnovitskiy.octocattest.repositories

import com.achesnovitskiy.empoyees.api.ApiFactory
import com.achesnovitskiy.octocattest.data.Repo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

object Repository {
    private val apiFactory = ApiFactory
    private val apiService = apiFactory.getApiService()
    private var repos = mutableListOf<Repo>()
    private val compositeDisposable = CompositeDisposable()

    fun loadReposFromApi(user: String, callback: (List<Repo>) -> Unit) {
        val disposable = apiService.getReposByUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    repos.addAll(result)
                },
                { error ->
                    error.printStackTrace()
                },
                {
                    callback(repos)
                }
            )
        compositeDisposable.add(disposable)
    }

    fun disposeDisposables() {
        compositeDisposable.dispose()
    }
}