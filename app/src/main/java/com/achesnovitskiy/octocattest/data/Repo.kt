package com.achesnovitskiy.octocattest.data

import com.google.gson.annotations.SerializedName

data class Repo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)