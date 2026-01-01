package main;

import javax.swing.*;
import ui.LoginFrame;
import utils.InitializeDatabase;
import utils.SalaryReminderService;

public class Main {
    public static void main(String[] args) {
        // Initialize database on startup
        InitializeDatabase.initialize();

        // Start salary reminder service
        SalaryReminderService.startService();

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start Login Frame
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}