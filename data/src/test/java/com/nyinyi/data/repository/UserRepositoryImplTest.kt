package com.nyinyi.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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

class UserRepositoryImplTest {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var supabaseStorage: Storage
    private lateinit var repository: UserRepositoryImpl

    // Mock collections and documents
    private lateinit var usersCollection: CollectionReference
    private lateinit var userDocument: DocumentReference
    private lateinit var userDocumentSnapshot: DocumentSnapshot
    private lateinit var bucket: BucketApi

    // Mock user and auth
    private lateinit var firebaseUser: FirebaseUser

    private val testUserId = "test_user_id"
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

    @Before
    fun setup() {
        auth = mockk()
        firestore = mockk()
        supabaseStorage = mockk()

        // Mock collections
        usersCollection = mockk()
        userDocument = mockk()
        userDocumentSnapshot = mockk()
        bucket = mockk()

        // Mock Firebase user
        firebaseUser = mockk()

        // Setup basic mocks
        every { firestore.collection("users") } returns usersCollection
        every { usersCollection.document(testUserId) } returns userDocument
        every { supabaseStorage.from("profile-pictures") } returns bucket

        repository = UserRepositoryImpl(auth, firestore, supabaseStorage)
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
    fun `isProfileComplete returns true when profile exists with username`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { userDocument.get() } returns Tasks.forResult(userDocumentSnapshot)
            every { userDocumentSnapshot.exists() } returns true
            every { userDocumentSnapshot.getString("username") } returns "testuser"

            // When
            val result = repository.isProfileComplete()

            // Then
            assertTrue(result.isSuccess)
            assertTrue(result.getOrNull()!!)
        }

    @Test
    fun `isProfileComplete returns false when profile exists but username is blank`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { userDocument.get() } returns Tasks.forResult(userDocumentSnapshot)
            every { userDocumentSnapshot.exists() } returns true
            every { userDocumentSnapshot.getString("username") } returns ""

            // When
            val result = repository.isProfileComplete()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(false, result.getOrNull())
        }

    @Test
    fun `isProfileComplete returns failure when no user is logged in`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns null

            // When
            val result = repository.isProfileComplete()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is FirebaseAuthException)
            assertEquals("NO_USER", (result.exceptionOrNull() as FirebaseAuthException).errorCode)
        }

    @Test
    fun `createUserProfile fails when no user is logged in`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns null

            // When
            val result = repository.createUserProfile("testuser", "@testuser")

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is FirebaseAuthException)
            assertEquals("NO_USER", (result.exceptionOrNull() as FirebaseAuthException).errorCode)
        }

    @Test
    fun `createUserProfile fails when user has no email`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { firebaseUser.email } returns null

            // When
            val result = repository.createUserProfile("testuser", "@testuser")

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is FirebaseAuthException)
            assertEquals("NO_EMAIL", (result.exceptionOrNull() as FirebaseAuthException).errorCode)
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
            assertEquals(testUserProfile.profilePictureUrl, profile?.profilePictureUrl)
            assertEquals(testUserProfile.likedPosts, profile?.likedPosts)
            assertEquals(testUserProfile.createdAt, profile?.createdAt)
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
            assertTrue(result.exceptionOrNull() is FirebaseAuthException)
            assertEquals("NO_USER", (result.exceptionOrNull() as FirebaseAuthException).errorCode)
        }

    @Test
    fun `updateUserProfile successfully updates profile without image`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { userDocument.update(any()) } returns Tasks.forResult(null)

            // When
            val result = repository.updateUserProfile("newuser", "@newuser", null)

            // Then
            assertTrue(result.isSuccess)
            verify {
                userDocument.update(
                    mapOf(
                        "username" to "newuser",
                        "handle" to "@newuser",
                    ),
                )
            }
        }

    @Test
    fun `updateUserProfile successfully updates profile with image`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            every { userDocument.update(any()) } returns Tasks.forResult(null)
            coEvery { bucket.upload(any<String>(), any<ByteArray>()) } returns mockk()
            every { bucket.publicUrl(any()) } returns "https://example.com/new_profile.jpg"
            val imageStream = ByteArrayInputStream("test image data".toByteArray())

            // When
            val result = repository.updateUserProfile("newuser", "@newuser", imageStream)

            // Then
            assertTrue(result.isSuccess)
            coVerify { bucket.upload(any<String>(), any<ByteArray>()) }
            verify {
                userDocument.update(
                    mapOf(
                        "username" to "newuser",
                        "handle" to "@newuser",
                        "profilePictureUrl" to "https://example.com/new_profile.jpg",
                    ),
                )
            }
        }

    @Test
    fun `updateUserProfile fails when no user is logged in`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns null

            // When
            val result = repository.updateUserProfile("newuser", "@newuser", null)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is FirebaseAuthException)
            assertEquals("NO_USER", (result.exceptionOrNull() as FirebaseAuthException).errorCode)
        }

    @Test
    fun `updateUserProfile handles exception during image upload`() =
        runBlocking {
            // Given
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns testUserId
            val imageStream = ByteArrayInputStream("test image data".toByteArray())
            coEvery {
                bucket.upload(
                    any<String>(),
                    any<ByteArray>(),
                )
            } throws RuntimeException("Upload failed")

            // When
            val result = repository.updateUserProfile("newuser", "@newuser", imageStream)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is RuntimeException)
            assertEquals("Upload failed", result.exceptionOrNull()?.message)
        }
}
