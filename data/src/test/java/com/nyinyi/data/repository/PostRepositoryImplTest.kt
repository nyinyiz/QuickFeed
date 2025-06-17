package com.nyinyi.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.nyinyi.domain_model.Post
import com.nyinyi.domain_model.UserProfile
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class PostRepositoryImplTest {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var supabaseStorage: Storage
    private lateinit var repository: PostRepositoryImpl

    // Mock collections and documents
    private lateinit var usersCollection: CollectionReference
    private lateinit var postsCollection: CollectionReference
    private lateinit var userDocument: DocumentReference
    private lateinit var postDocument: DocumentReference
    private lateinit var userDocumentSnapshot: DocumentSnapshot
    private lateinit var postDocumentSnapshot: DocumentSnapshot
    private lateinit var querySnapshot: QuerySnapshot
    private lateinit var query: Query
    private lateinit var writeBatch: WriteBatch
    private lateinit var bucketApi: BucketApi

    // Mock user and auth
    private lateinit var firebaseUser: FirebaseUser

    private val testUserId = "test_user_id"
    private val testPostId = "test_post_id"
    private val testUserProfile =
        UserProfile(
            userId = testUserId,
            username = "testuser",
            handle = "@testuser",
            email = "test@example.com",
            profilePictureUrl = "https://example.com/profile.jpg",
            likedPosts = listOf("liked_post_1", "liked_post_2"),
            createdAt = 1234567890L,
        )

    private val testPost =
        Post(
            id = testPostId,
            content = "Test post content",
            imageUrl = "https://example.com/image.jpg",
            timestamp = 1234567890L,
            likeCount = 5,
            commentCount = 2,
            authorUid = testUserId,
            authorUsername = "testuser",
            authorHandle = "@testuser",
            authorProfilePictureUrl = "https://example.com/profile.jpg",
            isLiked = false,
        )

    @Before
    fun setup() {
        auth = mockk()
        firestore = mockk()
        supabaseStorage = mockk()

        // Mock collections
        usersCollection = mockk()
        postsCollection = mockk()
        userDocument = mockk()
        postDocument = mockk()
        userDocumentSnapshot = mockk()
        postDocumentSnapshot = mockk()
        querySnapshot = mockk()
        query = mockk()
        writeBatch = mockk()
        bucketApi = mockk()

        // Mock Firebase user
        firebaseUser = mockk()

        // Setup basic mocks
        every { firestore.collection("users") } returns usersCollection
        every { firestore.collection("posts") } returns postsCollection
        every { usersCollection.document(testUserId) } returns userDocument
        every { postsCollection.document() } returns postDocument
        every { postsCollection.document(testPostId) } returns postDocument
        every { postDocument.id } returns testPostId
        every { firestore.batch() } returns writeBatch
        every {
            writeBatch.update(
                any<DocumentReference>(),
                any<String>(),
                any(),
            )
        } returns writeBatch
        every { writeBatch.commit() } returns Tasks.forResult(null)

        repository = PostRepositoryImpl(auth, firestore, supabaseStorage)
    }

    @Test
    fun `getCurrentUserId returns user id when user is logged in`() {
        // Given
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns testUserId

        // When
        val result = repository.getCurrentUserId()

        // Then
        assertEquals(testUserId, result)
    }

    @Test
    fun `getCurrentUserId returns null when no user is logged in`() {
        // Given
        every { auth.currentUser } returns null

        // When
        val result = repository.getCurrentUserId()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `getCurrentUserProfile returns user profile when user exists`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { firebaseUser.email } returns "test@example.com"
            every { userDocument.get() } returns Tasks.forResult(userDocumentSnapshot)
            every { userDocumentSnapshot.exists() } returns true
            every { userDocumentSnapshot.getString("userId") } returns testUserId
            every { userDocumentSnapshot.getString("username") } returns "testuser"
            every { userDocumentSnapshot.getString("handle") } returns "@testuser"
            every { userDocumentSnapshot.getString("email") } returns "test@example.com"
            every { userDocumentSnapshot.getString("profilePictureUrl") } returns "https://example.com/profile.jpg"
            every { userDocumentSnapshot.get("likedPosts") } returns
                listOf(
                    "liked_post_1",
                    "liked_post_2",
                )
            every { userDocumentSnapshot.getTimestamp("createdAt")?.seconds } returns 1234567890L

            // When
            val result = repository.getCurrentUserProfile()

            // Then
            assertTrue(result.isSuccess)
            val profile = result.getOrNull()
            assertEquals(testUserProfile.userId, profile?.userId)
            assertEquals(testUserProfile.username, profile?.username)
            assertEquals(testUserProfile.handle, profile?.handle)
            assertEquals(testUserProfile.email, profile?.email)
        }

    @Test
    fun `getCurrentUserProfile returns null when user document doesn't exist`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { userDocument.get() } returns Tasks.forResult(userDocumentSnapshot)
            every { userDocumentSnapshot.exists() } returns false

            // When
            val result = repository.getCurrentUserProfile()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(null, result.getOrNull())
        }

    @Test
    fun `getCurrentUserProfile returns failure when no user is logged in`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns null

            // When
            val result = repository.getCurrentUserProfile()

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `createPost successfully creates post without image`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { userDocument.get() } returns Tasks.forResult(userDocumentSnapshot)
            every { userDocumentSnapshot.exists() } returns true
            every { userDocumentSnapshot.getString("username") } returns "testuser"
            every { userDocumentSnapshot.getString("handle") } returns "@testuser"
            every { userDocumentSnapshot.getString("profilePictureUrl") } returns "https://example.com/profile.jpg"
            every { postDocument.set(any()) } returns Tasks.forResult(null)

            // When
            val result = repository.createPost("Test content", null)

            // Then
            assertTrue(result.isSuccess)
            verify { postDocument.set(any()) }
        }

    @Test
    fun `createPost successfully creates post with image`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { userDocument.get() } returns Tasks.forResult(userDocumentSnapshot)
            every { userDocumentSnapshot.exists() } returns true
            every { userDocumentSnapshot.getString("username") } returns "testuser"
            every { userDocumentSnapshot.getString("handle") } returns "@testuser"
            every { userDocumentSnapshot.getString("profilePictureUrl") } returns "https://example.com/profile.jpg"
            every { postDocument.set(any()) } returns Tasks.forResult(null)
            every { supabaseStorage.from("timeline-post") } returns bucketApi
            coEvery { bucketApi.upload(any<String>(), any<ByteArray>()) } returns mockk()
            every { bucketApi.publicUrl(any()) } returns "https://supabase.com/image.jpg"

            val imageStream = ByteArrayInputStream("test image data".toByteArray())

            // When
            val result = repository.createPost("Test content", imageStream)

            // Then
            assertTrue(result.isSuccess)
            verify { postDocument.set(any()) }
            coVerify { bucketApi.upload(any<String>(), any<ByteArray>()) }
        }

    @Test
    fun `deletePost successfully deletes post without image`() =
        runBlocking {
            // Given
            val postWithoutImage = testPost.copy(imageUrl = null)
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { postDocument.delete() } returns Tasks.forResult(null)

            // When
            val result = repository.deletePost(postWithoutImage)

            // Then
            assertTrue(result.isSuccess)
            verify { postDocument.delete() }
        }

    @Test
    fun `deletePost successfully deletes post with image`() =
        runBlocking {
            // Given
            val postWithImage =
                testPost.copy(imageUrl = "https://supabase.com/timeline-post/posts/test/image.jpg")
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { postDocument.delete() } returns Tasks.forResult(null)
            every { supabaseStorage.from("timeline-post") } returns bucketApi
            coEvery { bucketApi.delete(any<List<String>>()) } returns mockk()

            // When
            val result = repository.deletePost(postWithImage)

            // Then
            assertTrue(result.isSuccess)
            verify { postDocument.delete() }
            coVerify { bucketApi.delete(listOf("posts/test/image.jpg")) }
        }

    @Test
    fun `deletePost fails when user is not the author`() =
        runBlocking {
            // Given
            val otherUserPost = testPost.copy(authorUid = "other_user_id")
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId

            // When
            val result = repository.deletePost(otherUserPost)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("not authorized") == true)
        }

    @Test
    fun `deletePost fails when no user is logged in`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns null

            // When
            val result = repository.deletePost(testPost)

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `likePost fails when no user is logged in`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns null

            // When
            val result = repository.likePost(testPostId)

            // Then
            assertTrue(result.isSuccess)
        }

    @Test
    fun `createPost handles exception during post creation`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { userDocument.get() } returns Tasks.forException(RuntimeException("Firestore error"))

            // When
            val result = repository.createPost("Test content", null)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is RuntimeException)
        }
}
