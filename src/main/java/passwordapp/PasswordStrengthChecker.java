package passwordapp;

public class PasswordStrengthChecker {

    public static String checkStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Weak (empty password)";
        }

        int lengthScore = password.length() >= 12 ? 2 : password.length() >= 8 ? 1 : 0;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        int complexityScore = (hasUpper ? 1 : 0) + (hasLower ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);

        int totalScore = lengthScore + complexityScore;

        if (totalScore >= 5) {
            return "Very Strong";
        } else if (totalScore == 4) {
            return "Strong";
        } else if (totalScore == 3) {
            return "Moderate";
        } else {
            return "Weak";
        }
    }
}
