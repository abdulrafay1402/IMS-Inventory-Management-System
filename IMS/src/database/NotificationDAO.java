package database;

import models.Notification;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    
    // Create a new notification
    public static boolean createNotification(int userId, String type, String title, String message, String relatedId) {
        String sql = "INSERT INTO notifications (user_id, type, title, message, related_id, is_read, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, 0, datetime('now', 'localtime'))";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, title);
            ps.setString(4, message);
            ps.setString(5, relatedId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get unread notifications for a user
    public static List<Notification> getUnreadNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Notification notif = new Notification();
                notif.setId(rs.getInt("id"));
                notif.setUserId(rs.getInt("user_id"));
                notif.setType(rs.getString("type"));
                notif.setTitle(rs.getString("title"));
                notif.setMessage(rs.getString("message"));
                notif.setRelatedId(rs.getString("related_id"));
                notif.setRead(rs.getBoolean("is_read"));
                notif.setCreatedAt(LocalDateTime.parse(rs.getString("created_at").replace(" ", "T")));
                notifications.add(notif);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    // Get all notifications for a user (read and unread)
    public static List<Notification> getAllNotifications(int userId, int limit) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Notification notif = new Notification();
                notif.setId(rs.getInt("id"));
                notif.setUserId(rs.getInt("user_id"));
                notif.setType(rs.getString("type"));
                notif.setTitle(rs.getString("title"));
                notif.setMessage(rs.getString("message"));
                notif.setRelatedId(rs.getString("related_id"));
                notif.setRead(rs.getBoolean("is_read"));
                notif.setCreatedAt(LocalDateTime.parse(rs.getString("created_at").replace(" ", "T")));
                notifications.add(notif);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    // Mark notification as read
    public static boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Mark all notifications as read for a user
    public static boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate() >= 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get unread count
    public static int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Delete old read notifications (older than 30 days)
    public static boolean cleanupOldNotifications() {
        String sql = "DELETE FROM notifications WHERE is_read = 1 AND created_at < datetime('now', '-30 days')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
