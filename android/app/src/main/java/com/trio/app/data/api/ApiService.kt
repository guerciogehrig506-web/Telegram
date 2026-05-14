package com.trio.app.data.api

import com.trio.app.data.model.ChatResponse
import com.trio.app.data.model.Group
import com.trio.app.data.model.LoginResponse
import com.trio.app.data.model.Message
import com.trio.app.data.model.MomentResponse
import com.trio.app.data.model.RegisterResponse
import com.trio.app.data.model.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("users/me")
    suspend fun getMe(): Response<User>

    @GET("users")
    suspend fun getUsers(
        @Query("search") search: String? = null,
        @Query("department") department: String? = null
    ): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>

    @GET("messages/chats")
    suspend fun getChats(): Response<List<ChatResponse>>

    @GET("messages/user")
    suspend fun getMessages(@Query("otherUserId") otherUserId: String): Response<List<Message>>

    @POST("messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<Message>

    @GET("moments")
    suspend fun getMoments(): Response<List<MomentResponse>>

    @POST("moments")
    suspend fun createMoment(@Body request: CreateMomentRequest): Response<MomentResponse>

    @POST("moments/{id}/like")
    suspend fun toggleLike(@Path("id") id: String): Response<LikeResponse>

    @Multipart
    @POST("upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ImageUploadResponse>

    @PATCH("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<User>

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<Group>

    @GET("groups")
    suspend fun getGroups(): Response<List<Group>>

    @GET("groups/{id}")
    suspend fun getGroupById(@Path("id") id: String): Response<Group>

    @PATCH("groups/{id}")
    suspend fun updateGroup(@Path("id") id: String, @Body request: UpdateGroupRequest): Response<Group>

    @POST("groups/{id}/members")
    suspend fun addGroupMembers(@Path("id") id: String, @Body request: AddMemberRequest): Response<Group>

    @DELETE("groups/{id}/members/{userId}")
    suspend fun removeGroupMember(@Path("id") id: String, @Path("userId") userId: String): Response<Group>

    @DELETE("groups/{id}")
    suspend fun deleteGroup(@Path("id") id: String): Response<DeleteGroupResponse>

    @GET("messages/group")
    suspend fun getGroupMessages(@Query("groupId") groupId: String): Response<List<Message>>

    @PATCH("users/me/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<Unit>
}

data class ImageUploadResponse(
    val url: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class SendMessageRequest(
    val content: String,
    val image: String? = null,
    val type: String? = "text",
    val receiverId: String? = null,
    val groupId: String? = null
)

data class CreateMomentRequest(
    val content: String
)

data class LikeResponse(
    val liked: Boolean
)

data class UpdateProfileRequest(
    val username: String? = null,
    val bio: String? = null,
    val avatar: String? = null
)

data class CreateGroupRequest(
    val name: String,
    val avatar: String? = null,
    val memberIds: List<String>
)

data class UpdateGroupRequest(
    val name: String? = null,
    val avatar: String? = null
)

data class AddMemberRequest(
    val userIds: List<String>
)

data class DeleteGroupResponse(
    val success: Boolean
)

data class FcmTokenRequest(
    val token: String
)