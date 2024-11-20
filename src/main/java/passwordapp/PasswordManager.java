package passwordapp;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class PasswordManager {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;':,.<>?";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private final Map<String, String> passwordStore = new HashMap<>();
    private final SecretKey encryptionKey;

    // Constructor to initialize with an encryption key
    public PasswordManager(SecretKey key) {
        this.encryptionKey = key;
    }

    // Add a password for an account
    public void addPassword(String account, String password) throws Exception {
        String encryptedPassword = EncryptionUtils.encrypt(password, encryptionKey);
        passwordStore.put(account, encryptedPassword);
    }

    // Retrieve a password for an account
    public String getPassword(String account) throws Exception {
        String encryptedPassword = passwordStore.get(account);
        if (encryptedPassword == null) {
            return null;
        }
        return EncryptionUtils.decrypt(encryptedPassword, encryptionKey);
    }

    // Generate a secure password
    public static String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password too short! Minimum 8 characters required.");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each group
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill the rest of the password
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle to avoid predictable patterns
        return shufflePassword(password.toString());
    }

    private static String shufflePassword(String password) {
        char[] characters = password.toCharArray();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            // Swap characters
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }

        return new String(characters);
    }

    // Display stored accounts and passwords (for debugging)
    public void displayPasswords() {
        passwordStore.forEach((account, encryptedPassword) -> {
            try {
                String decryptedPassword = EncryptionUtils.decrypt(encryptedPassword, encryptionKey);
                System.out.println(account + ": " + decryptedPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
