package com.bc230420212.app.data.repository

import com.bc230420212.app.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
            )?.await()

            // Create user document in Firestore
            val user = hashMapOf(
                "uid" to result.user!!.uid,
                "email" to email,
                "displayName" to displayName,
                "photoUrl" to "",
                "role" to UserRole.USER.name
            )
            firestore.collection("users").document(result.user!!.uid).set(user).await()

            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            
            // Check if user exists in Firestore, if not create one
            val userDoc = firestore.collection("users").document(result.user!!.uid).get().await()
            if (!userDoc.exists()) {
                val user = hashMapOf(
                    "uid" to result.user!!.uid,
                    "email" to (result.user!!.email ?: ""),
                    "displayName" to (result.user!!.displayName ?: ""),
                    "photoUrl" to (result.user!!.photoUrl?.toString() ?: ""),
                    "role" to UserRole.USER.name
                )
                firestore.collection("users").document(result.user!!.uid).set(user).await()
            }
            
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(uid: String): UserRole {
        return try {
            val userDoc = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            if (!userDoc.exists()) {
                return UserRole.USER
            }

            val roleString = userDoc.getString("role")
                ?.uppercase()
                ?: "USER"

            UserRole.valueOf(roleString)



        } catch (e: Exception) {
            UserRole.USER
        }
    }


    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Change user password
     * 
     * @param currentPassword - Current password for verification
     * @param newPassword - New password to set
     * @return Result indicating success or failure
     */
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            // Re-authenticate user with current password
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(
                user.email!!,
                currentPassword
            )
            user.reauthenticate(credential).await()
            
            // Update password
            user.updatePassword(newPassword).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

