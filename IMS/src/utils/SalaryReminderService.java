package utils;

import database.NotificationDAO;
import database.SalaryDAO;
import database.UserDAO;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Background service to check for month-end and send salary reminders to managers
 */
public class SalaryReminderService {
    private static Timer timer;
    private static final long CHECK_INTERVAL = 12 * 60 * 60 * 1000; // Check every 12 hours
    
    /**
     * Start the salary reminder service
     */
    public static void startService() {
        if (timer != null) {
            return; // Already running
        }
        
        timer = new Timer("SalaryReminderService", true); // Daemon thread
        
        // Run immediately on start, then every 12 hours
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndNotifyManagers();
            }
        }, 0, CHECK_INTERVAL);
    }
    
    /**
     * Stop the salary reminder service
     */
    public static void stopService() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    /**
     * Check if it's month-end period and notify managers about pending salary payments
     */
    private static void checkAndNotifyManagers() {
        try {
            LocalDate today = LocalDate.now();
            int dayOfMonth = today.getDayOfMonth();
            YearMonth currentYearMonth = YearMonth.from(today);
            int lastDayOfMonth = currentYearMonth.lengthOfMonth();
            
            // Check if it's the last 3 days of the month
            if (dayOfMonth >= lastDayOfMonth - 2) {
                String currentMonth = currentYearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
                
                // Check if salaries have been paid for this month
                boolean isPaid = SalaryDAO.areSalariesPaidForMonth(currentMonth);
                
                if (!isPaid) {
                    // Get all active managers
                    java.util.List<models.Manager> managers = UserDAO.getAllManagers();
                    
                    for (models.User manager : managers) {
                        // Check if notification already sent today
                        if (!hasNotificationBeenSentToday(manager.getId(), "SALARY_REMINDER")) {
                            // Send reminder notification
                            NotificationDAO.createNotification(
                                manager.getId(),
                                "SALARY_REMINDER",
                                "⚠️ Month-End Salary Payment Reminder",
                                "Please process monthly salary payments before month-end. " +
                                "Go to Salary Management panel to process payments for " + currentMonth + ".",
                                ""
                            );
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in SalaryReminderService: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if a notification of given type has been sent to user today
     */
    private static boolean hasNotificationBeenSentToday(int userId, String notificationType) {
        try {
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND notification_type = ? " +
                        "AND DATE(created_at) = DATE('now')";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, notificationType);
            java.sql.ResultSet rs = ps.executeQuery();
            
            boolean exists = rs.next() && rs.getInt(1) > 0;
            
            rs.close();
            ps.close();
            conn.close();
            
            return exists;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
