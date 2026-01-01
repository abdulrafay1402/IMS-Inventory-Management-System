package utils;

// ============= VALIDATION UTILITIES =============
public class ValidationUtils {

    public static boolean isValidCnic(String cnic) {
        return cnic != null && cnic.matches("\\d{13}");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{11}");
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.matches("[a-zA-Z0-9]{3,50}");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}

