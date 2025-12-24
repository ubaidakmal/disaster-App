package com.bc230420212.app.data.repository

import com.bc230420212.app.data.model.DisasterReport
import com.bc230420212.app.data.model.ReportStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * REPORT REPOSITORY
 * 
 * This repository handles all Firestore operations related to disaster reports.
 * It provides functions to:
 * - Save new reports to Firestore
 * - Fetch reports from Firestore
 * - Update report status
 */
class ReportRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    /**
     * Save a new disaster report to Firestore
     * 
     * @param report - The disaster report to save
     * @return Result containing the report ID if successful, or error if failed
     */
    suspend fun saveReport(report: DisasterReport): Result<String> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            // Add user ID to report
            val reportWithUserId = report.copy(userId = currentUser.uid)
            
            // Convert report to HashMap for Firestore
            val reportData = hashMapOf(
                "userId" to reportWithUserId.userId,
                "disasterType" to reportWithUserId.disasterType.name,
                "description" to reportWithUserId.description,
                "latitude" to reportWithUserId.latitude,
                "longitude" to reportWithUserId.longitude,
                "address" to reportWithUserId.address,
                "mediaUrls" to reportWithUserId.mediaUrls,
                "timestamp" to reportWithUserId.timestamp,
                "status" to reportWithUserId.status.name,
                "confirmations" to reportWithUserId.confirmations,
                "dismissals" to reportWithUserId.dismissals,
                "confirmedBy" to reportWithUserId.confirmedBy,
                "dismissedBy" to reportWithUserId.dismissedBy
            )
            
            // Save to Firestore collection "reports"
            val docRef = firestore.collection("reports").add(reportData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all reports from Firestore (both active and past)
     * 
     * @return List of all disaster reports
     */
    suspend fun getAllReports(): Result<List<DisasterReport>> {
        return try {
            val snapshot = firestore.collection("reports")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val reports = snapshot.documents.map { doc ->
                // Convert Firestore document to DisasterReport
                DisasterReport(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    disasterType = com.bc230420212.app.data.model.DisasterType.valueOf(
                        doc.getString("disasterType") ?: "OTHER"
                    ),
                    description = doc.getString("description") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    address = doc.getString("address") ?: "",
                    mediaUrls = (doc.get("mediaUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                    status = ReportStatus.valueOf(doc.getString("status") ?: "ACTIVE"),
                    confirmations = (doc.getLong("confirmations") ?: 0).toInt(),
                    dismissals = (doc.getLong("dismissals") ?: 0).toInt(),
                    confirmedBy = (doc.get("confirmedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    dismissedBy = (doc.get("dismissedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                )
            }
            
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get a single report by ID
     * 
     * @param reportId - ID of the report to fetch
     * @return DisasterReport if found, or error
     */
    suspend fun getReportById(reportId: String): Result<DisasterReport> {
        return try {
            val doc = firestore.collection("reports").document(reportId).get().await()
            
            if (doc.exists()) {
                val report = DisasterReport(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    disasterType = com.bc230420212.app.data.model.DisasterType.valueOf(
                        doc.getString("disasterType") ?: "OTHER"
                    ),
                    description = doc.getString("description") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    address = doc.getString("address") ?: "",
                    mediaUrls = (doc.get("mediaUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                    status = ReportStatus.valueOf(doc.getString("status") ?: "ACTIVE"),
                    confirmations = (doc.getLong("confirmations") ?: 0).toInt(),
                    dismissals = (doc.getLong("dismissals") ?: 0).toInt(),
                    confirmedBy = (doc.get("confirmedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    dismissedBy = (doc.get("dismissedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                )
                Result.success(report)
            } else {
                Result.failure(Exception("Report not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if current user has already confirmed a report
     * 
     * @param reportId - ID of the report to check
     * @return True if user has already confirmed, false otherwise
     */
    suspend fun hasUserConfirmed(reportId: String): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) return false
            
            val doc = firestore.collection("reports").document(reportId).get().await()
            val confirmedBy = (doc.get("confirmedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            confirmedBy.contains(currentUser.uid)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if current user has already dismissed a report
     * 
     * @param reportId - ID of the report to check
     * @return True if user has already dismissed, false otherwise
     */
    suspend fun hasUserDismissed(reportId: String): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) return false
            
            val doc = firestore.collection("reports").document(reportId).get().await()
            val dismissedBy = (doc.get("dismissedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            dismissedBy.contains(currentUser.uid)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Confirm a report (one-time per user)
     * Adds user ID to confirmedBy array and updates count
     * 
     * @param reportId - ID of the report to confirm
     * @return Success or error
     */
    suspend fun confirmReport(reportId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val reportRef = firestore.collection("reports").document(reportId)
            val currentData = reportRef.get().await()
            
            // Check if user has already confirmed
            val confirmedBy = (currentData.get("confirmedBy") as? List<*>)?.mapNotNull { it as? String }?.toMutableList() ?: mutableListOf()
            
            if (confirmedBy.contains(currentUser.uid)) {
                return Result.failure(Exception("You have already confirmed this report"))
            }
            
            // Check if user has dismissed (can't confirm if already dismissed)
            val dismissedBy = (currentData.get("dismissedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            if (dismissedBy.contains(currentUser.uid)) {
                return Result.failure(Exception("You have already dismissed this report"))
            }
            
            // Add user ID to confirmedBy array
            confirmedBy.add(currentUser.uid)
            
            // Update Firestore with new array and updated count
            reportRef.update(
                mapOf(
                    "confirmedBy" to confirmedBy,
                    "confirmations" to confirmedBy.size
                )
            ).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Dismiss a report (one-time per user)
     * Adds user ID to dismissedBy array and updates count
     * 
     * @param reportId - ID of the report to dismiss
     * @return Success or error
     */
    suspend fun dismissReport(reportId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val reportRef = firestore.collection("reports").document(reportId)
            val currentData = reportRef.get().await()
            
            // Check if user has already dismissed
            val dismissedBy = (currentData.get("dismissedBy") as? List<*>)?.mapNotNull { it as? String }?.toMutableList() ?: mutableListOf()
            
            if (dismissedBy.contains(currentUser.uid)) {
                return Result.failure(Exception("You have already dismissed this report"))
            }
            
            // Check if user has confirmed (can't dismiss if already confirmed)
            val confirmedBy = (currentData.get("confirmedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            if (confirmedBy.contains(currentUser.uid)) {
                return Result.failure(Exception("You have already confirmed this report"))
            }
            
            // Add user ID to dismissedBy array
            dismissedBy.add(currentUser.uid)
            
            // Update Firestore with new array and updated count
            reportRef.update(
                mapOf(
                    "dismissedBy" to dismissedBy,
                    "dismissals" to dismissedBy.size
                )
            ).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update report status (Admin only)
     * 
     * @param reportId - ID of the report to update
     * @param status - New status to set (VERIFIED, RESOLVED, FALSE_ALARM)
     * @return Success or error
     */
    suspend fun updateReportStatus(reportId: String, status: ReportStatus): Result<Unit> {
        return try {
            // Update status in Firestore
            firestore.collection("reports").document(reportId)
                .update("status", status.name)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get pending reports (ACTIVE status) for admin review
     * 
     * @return List of pending reports
     */
    suspend fun getPendingReports(): Result<List<DisasterReport>> {
        return try {
            val snapshot = firestore.collection("reports")
                .whereEqualTo("status", ReportStatus.ACTIVE.name)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val reports = snapshot.documents.map { doc ->
                DisasterReport(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    disasterType = com.bc230420212.app.data.model.DisasterType.valueOf(
                        doc.getString("disasterType") ?: "OTHER"
                    ),
                    description = doc.getString("description") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    address = doc.getString("address") ?: "",
                    mediaUrls = (doc.get("mediaUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                    status = ReportStatus.valueOf(doc.getString("status") ?: "ACTIVE"),
                    confirmations = (doc.getLong("confirmations") ?: 0).toInt(),
                    dismissals = (doc.getLong("dismissals") ?: 0).toInt(),
                    confirmedBy = (doc.get("confirmedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    dismissedBy = (doc.get("dismissedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                )
            }
            
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

