package com.achesnovitskiy.empoyees.api

import com.achesnovitskiy.octocattest.data.Repo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{username}/repos")
    fun getReposByUser(@Path("username") userName: String): Observable<List<Repo>>
}