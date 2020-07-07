package com.achesnovitskiy.octocattest.repositories

import com.achesnovitskiy.empoyees.api.ApiFactory
import com.achesnovitskiy.octocattest.data.Repo
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

object Repository {
    private val apiFactory = ApiFactory
    private val apiService = apiFactory.getApiService()
    private var repos = mutableListOf<Repo>()
    private val compositeDisposable = CompositeDisposable()

    fun loadReposFromApi(userName: String, callback: (List<Repo>) -> Unit) {
        val disposable = apiService.getReposByUser(userName)
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

    fun isNeedLoadRepos(): Single<Boolean> = Single.fromCallable { repos.isEmpty() }

    fun disposeDisposables() {
        compositeDisposable.dispose()
    }
}