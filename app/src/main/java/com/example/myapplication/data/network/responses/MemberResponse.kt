package com.example.myapplication.data.network.responses

class MemberResponse(
    val id: String,
    val role: MemberRoleResponse,
    val nickname: String,
    val user: MemberResponse? = null
)