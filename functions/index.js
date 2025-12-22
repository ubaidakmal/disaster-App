/**
 * FIREBASE CLOUD FUNCTIONS
 * 
 * This file contains Cloud Functions that automatically send push notifications
 * to all users when a new disaster report is created.
 * 
 * DEPLOYMENT INSTRUCTIONS:
 * 1. Install Firebase CLI: npm install -g firebase-tools
 * 2. Login: firebase login
 * 3. Initialize: firebase init functions
 * 4. Deploy: firebase deploy --only functions
 * 
 * IMPORTANT: This runs on the server side, not in the Android app.
 * The app creates the report, and this function sends notifications to everyone.
 */

const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Initialize Firebase Admin (automatically done in Firebase Functions environment)
admin.initializeApp();

/**
 * CLOUD FUNCTION: Send Notification on New Report
 * 
 * This function is triggered automatically when a new document is created
 * in the "reports" collection in Firestore.
 * 
 * It sends a push notification to all users subscribed to the "allUsers" topic.
 * 
 * @param {Object} change - Firestore document change
 * @param {Object} context - Function context
 */
exports.sendReportNotification = functions.firestore
    .document('reports/{reportId}')
    .onCreate(async (snap, context) => {
        try {
            const report = snap.data();
            const reportId = context.params.reportId;

            // Get disaster type display name
            const disasterType = report.disasterType || 'Disaster';
            const disasterTypeNames = {
                'FLOOD': 'Flood',
                'FIRE': 'Fire',
                'EARTHQUAKE': 'Earthquake',
                'ACCIDENT': 'Accident',
                'STORM': 'Storm',
                'LANDSLIDE': 'Landslide',
                'OTHER': 'Disaster'
            };
            const disasterTypeName = disasterTypeNames[disasterType] || 'Disaster';

            // Prepare notification message
            const notification = {
                title: 'New Disaster Report',
                body: `${disasterTypeName} reported near your location`,
                data: {
                    reportId: reportId,
                    disasterType: disasterType,
                    latitude: report.latitude?.toString() || '0',
                    longitude: report.longitude?.toString() || '0',
                    timestamp: report.timestamp?.toString() || Date.now().toString()
                }
            };

            // Send notification to all users subscribed to the topic
            // Users automatically subscribe to this topic when they open the app
            const message = {
                notification: {
                    title: notification.title,
                    body: notification.body
                },
                data: notification.data,
                topic: 'allUsers', // All users subscribe to this topic
                android: {
                    priority: 'high',
                    notification: {
                        sound: 'default',
                        channelId: 'disaster_alert_channel',
                        priority: 'high'
                    }
                }
            };

            // Send the notification
            const response = await admin.messaging().send(message);
            console.log('Successfully sent notification:', response);
            
            return null;
        } catch (error) {
            console.error('Error sending notification:', error);
            return null;
        }
    });

/**
 * OPTIONAL: Send notification to users near the disaster location
 * 
 * This is a more advanced function that could send notifications only to users
 * within a certain radius of the disaster. For now, we send to all users.
 */

