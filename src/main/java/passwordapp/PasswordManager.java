package passwordapp;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PasswordManager {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?/";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private final Map<String, String> passwordStore = new HashMap<>();

    private String hashedMasterPassword; // Argon2-hashed master password

    // === Master Password Management === //

    /** Set the master password (stored as an Argon2 hash). */
    public void setMasterPassword(String masterPassword) throws Exception {
        this.hashedMasterPassword = Argon2Utils.hashPassword(masterPassword);
    }

    /** Verify the master password against the stored hash. */
    public boolean verifyMasterPassword(String masterPassword) throws Exception {
        return Argon2Utils.verifyPassword(masterPassword, hashedMasterPassword);
    }

    // === Account Password Management === //

    /**
     * Add a password for an account.
     * The password is stored as a hash for security.
     */
    public void addPassword(String account, String password) throws Exception {
        String hashedPassword = Argon2Utils.hashPassword(password);
        passwordStore.put(account, hashedPassword);
    }

    /**
     * Verify a password for a given account.
     * Compares the provided password with the stored hash.
     */
    public boolean verifyAccountPassword(String account, String password) throws Exception {
        String storedHash = passwordStore.get(account);
        if (storedHash == null) {
            return false; // No password found for the account
        }
        return Argon2Utils.verifyPassword(password, storedHash);
    }

    /**
    * Retrieve all entries from the password store.
    * Returns a list of account and hashed password pairs.
    */
    public List<Map.Entry<String, String>> getPasswordStoreEntries() {
        return new ArrayList<>(passwordStore.entrySet());
    }


    /**
     * Check if an account exists in the password store.
     */
    public boolean accountExists(String account) {
        return passwordStore.containsKey(account);
    }

    // === Password Generation === //

    /**
     * Generate a secure password with the specified length.
     */
    public static String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password too short! Minimum 8 characters required.");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure the password has at least one character from each pool
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill the rest of the password length with random characters from all pools
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle the password to ensure randomness
        List<Character> passwordChars = password.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());
        Collections.shuffle(passwordChars, random);

        return passwordChars.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());
    }
}
