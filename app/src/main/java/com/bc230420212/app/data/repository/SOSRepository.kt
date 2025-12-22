package com.bc230420212.app.data.repository

import com.bc230420212.app.data.model.SOSAlert
import com.bc230420212.app.data.model.SOSStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * SOS REPOSITORY
 * 
 * This repository handles all Firestore operations related to SOS alerts.
 * It provides functions to:
 * - Save SOS alerts to Firestore
 * - Fetch SOS alerts from Firestore
 * - Update SOS alert status
 */
class SOSRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    /**
     * Send an SOS alert to Firestore
     * 
     * @param alert - The SOS alert to send
     * @return Result containing the alert ID if successful, or error if failed
     */
    suspend fun sendSOSAlert(alert: SOSAlert): Result<String> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            // Add user information to alert
            val alertWithUser = alert.copy(
                userId = currentUser.uid,
                userEmail = currentUser.email ?: "",
                userName = currentUser.displayName ?: "Unknown User"
            )
            
            // Convert alert to HashMap for Firestore
            val alertData = hashMapOf(
                "userId" to alertWithUser.userId,
                "userEmail" to alertWithUser.userEmail,
                "userName" to alertWithUser.userName,
                "latitude" to alertWithUser.latitude,
                "longitude" to alertWithUser.longitude,
                "address" to alertWithUser.address,
                "timestamp" to alertWithUser.timestamp,
                "status" to alertWithUser.status.name,
                "message" to alertWithUser.message
            )
            
            // Save to Firestore collection "sosAlerts"
            val docRef = firestore.collection("sosAlerts").add(alertData).await()
            
            // Update the alert with the generated ID
            firestore.collection("sosAlerts").document(docRef.id).update("id", docRef.id).await()
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all active SOS alerts from Firestore
     * 
     * @return List of active SOS alerts
     */
    suspend fun getActiveSOSAlerts(): Result<List<SOSAlert>> {
        return try {
            val snapshot = firestore.collection("sosAlerts")
                .whereEqualTo("status", SOSStatus.ACTIVE.name)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val alerts = snapshot.documents.map { doc ->
                SOSAlert(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    userEmail = doc.getString("userEmail") ?: "",
                    userName = doc.getString("userName") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    address = doc.getString("address") ?: "",
                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                    status = SOSStatus.valueOf(doc.getString("status") ?: "ACTIVE"),
                    message = doc.getString("message") ?: ""
                )
            }
            
            Result.success(alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

