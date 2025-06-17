package com.nyinyi.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: AuthRepositoryImpl

    // Mock collections and documents
    private lateinit var usersCollection: CollectionReference
    private lateinit var userDocument: DocumentReference
    private lateinit var firebaseUser: FirebaseUser

    private val testUserId = "test_user_id"
    private val testEmail = "test@example.com"
    private val testPassword = "password123"

    @Before
    fun setup() {
        auth = mockk()
        firestore = mockk()
        usersCollection = mockk()
        userDocument = mockk()
        firebaseUser = mockk()

        // Setup basic mocks
        every { firestore.collection("users") } returns usersCollection
        every { usersCollection.document(testUserId) } returns userDocument
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns testUserId
        every { firebaseUser.email } returns testEmail

        repository = AuthRepositoryImpl(auth, firestore)
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
    fun `isLoggedIn returns true when user is logged in`() {
        // Given
        every { auth.currentUser } returns firebaseUser

        // When
        val result = repository.isLoggedIn()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isLoggedIn returns false when no user is logged in`() {
        // Given
        every { auth.currentUser } returns null

        // When
        val result = repository.isLoggedIn()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `signUp fails with email already in use error`() =
        runBlocking {
            // Given
            val exception = FirebaseAuthException("ERROR_EMAIL_ALREADY_IN_USE", "Email already in use")
            every {
                auth.createUserWithEmailAndPassword(
                    testEmail,
                    testPassword,
                )
            } returns Tasks.forException(exception)

            // When
            val result = repository.signUp(testEmail, testPassword)

            // Then
            assertTrue(result.isFailure)
            assertEquals("An account with this email already exists", result.exceptionOrNull()?.message)
        }

    @Test
    fun `signUp fails with weak password error`() =
        runBlocking {
            // Given
            val exception = FirebaseAuthException("ERROR_WEAK_PASSWORD", "Weak password")
            every {
                auth.createUserWithEmailAndPassword(
                    testEmail,
                    testPassword,
                )
            } returns Tasks.forException(exception)

            // When
            val result = repository.signUp(testEmail, testPassword)

            // Then
            assertTrue(result.isFailure)
            assertEquals("Password is too weak", result.exceptionOrNull()?.message)
        }

    @Test
    fun `signUp fails with invalid email error`() =
        runBlocking {
            // Given
            val exception = FirebaseAuthException("ERROR_INVALID_EMAIL", "Invalid email")
            every {
                auth.createUserWithEmailAndPassword(
                    testEmail,
                    testPassword,
                )
            } returns Tasks.forException(exception)

            // When
            val result = repository.signUp(testEmail, testPassword)

            // Then
            assertTrue(result.isFailure)
            assertEquals("Invalid email address", result.exceptionOrNull()?.message)
        }

    @Test
    fun `signUp handles generic exception`() =
        runBlocking {
            // Given
            val exception = RuntimeException("Network error")
            every {
                auth.createUserWithEmailAndPassword(
                    testEmail,
                    testPassword,
                )
            } returns Tasks.forException(exception)

            // When
            val result = repository.signUp(testEmail, testPassword)

            // Then
            assertTrue(result.isFailure)
            assertEquals("Network error", result.exceptionOrNull()?.message)
        }

    @Test
    fun `login succeeds with valid credentials`() =
        runBlocking {
            // Given
            val authResult = mockk<AuthResult>()
            every { auth.signInWithEmailAndPassword(testEmail, testPassword) } returns
                Tasks.forResult(
                    authResult,
                )

            // When
            val result = repository.login(testEmail, testPassword)

            // Then
            assertTrue(result.isSuccess)
            verify { auth.signInWithEmailAndPassword(testEmail, testPassword) }
        }

    @Test
    fun `login fails with invalid user error`() =
        runBlocking {
            // Given
            val exception = FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "User not found")
            every {
                auth.signInWithEmailAndPassword(
                    testEmail,
                    testPassword,
                )
            } returns Tasks.forException(exception)

            // When
            val result = repository.login(testEmail, testPassword)

            // Then
            assertTrue(result.isFailure)
            assertEquals(
                "No account found with this email address. Please check your email or sign up.",
                result.exceptionOrNull()?.message,
            )
        }

    @Test
    fun `login fails with invalid credentials error`() =
        runBlocking {
            // Given
            val exception =
                FirebaseAuthInvalidCredentialsException("ERROR_WRONG_PASSWORD", "Wrong password")
            every {
                auth.signInWithEmailAndPassword(
                    testEmail,
                    testPassword,
                )
            } returns Tasks.forException(exception)

            // When
            val result = repository.login(testEmail, testPassword)

            // Then
            assertTrue(result.isFailure)
            assertEquals("Incorrect password. Please try again.", result.exceptionOrNull()?.message)
        }

    @Test
    fun `login fails with too many requests error`() =
        runBlocking {
            // Given
            val exception = FirebaseAuthException("ERROR_TOO_MANY_REQUESTS", "Too many requests")
            every {
                auth.signInWithEmailAndPassword(
                    testEmail,
                    testPassword,
                )
            } returns Tasks.forException(exception)

            // When
            val result = repository.login(testEmail, testPassword)

            // Then
            assertTrue(result.isFailure)
            assertEquals(
                "Too many requests. Please try again later.",
                result.exceptionOrNull()?.message,
            )
        }

    @Test
    fun `login handles generic exception`() =
        runBlocking {
            // Given
            val exception = RuntimeException("Network error")
            every {
                auth.signInWithEmailAndPassword(
                    testEmail,
                    testPassword,
                )
            } returns Tasks.forException(exception)

            // When
            val result = repository.login(testEmail, testPassword)

            // Then
            assertTrue(result.isFailure)
            assertEquals(
                "Login failed. Please check your connection and try again.",
                result.exceptionOrNull()?.message,
            )
        }

    @Test
    fun `logout calls signOut on FirebaseAuth`() {
        // Given
        every { auth.signOut() } just Runs

        // When
        repository.logout()

        // Then
        verify { auth.signOut() }
    }
}
