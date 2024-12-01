package passwordapp;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Argon2Utils {
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;

    public static String hashPassword(String password) throws Exception {
        byte[] salt = generateSalt();

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(1)
                .withMemoryAsKB(65536)
                .withIterations(3);
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());

        byte[] hash = new byte[HASH_LENGTH];
        generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash);

        // Encode salt + hash as Base64 for storage
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split("\\$");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

            Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt)
                    .withParallelism(1)
                    .withMemoryAsKB(65536)
                    .withIterations(3);
            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(builder.build());

            byte[] actualHash = new byte[HASH_LENGTH];
            generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), actualHash);

            return java.util.Arrays.equals(expectedHash, actualHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
}
