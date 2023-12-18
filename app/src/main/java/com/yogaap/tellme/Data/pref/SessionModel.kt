package com.yogaap.tellme.Data.pref

data class SessionModel (
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean
)