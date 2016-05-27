package feup.sdis.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Security utilities class
 */
public class Security {

    /**
     * Generate a secret key
     * @param algorithm algorithm to generate the secret key
     * @return generated secret key
     * @throws NoSuchAlgorithmException when there is no such algorithm
     */
    public static SecretKey generateSecretKey(final String algorithm) throws NoSuchAlgorithmException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(128);

        return keyGenerator.generateKey();
    }

    /**
     * Recover a secret key given its algorithm and encoded bytes
     * @param algorithm algorithm of the secret key
     * @param encoded encoded bytes of the secret key
     * @return recovered secret key
     */
    public static SecretKey recoverSecretKey(final String algorithm, final byte[] encoded) {
        return new SecretKeySpec(encoded, algorithm);
    }

    /**
     * Encrypt a byte array to a fixed size 128 byte array
     * @param algorithm algorithm to be used when encrypting
     * @param secretKey secret key to use to encrypt
     * @param plain plain byte to be encrypted
     * @return encrypted 128 length byte array
     */
    public static byte[] encrypt(final String algorithm, final SecretKey secretKey, byte[] plain) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(plain);
    }

    /**
     * Decrypt a byte array to its original content
     * @param algorithm algorithm to be used when decrypting
     * @param secretKey secret key to use to decrypt
     * @param encrypted encrypted byte to be decrypted
     * @return decrypted byte array
     */
    public static byte[] decrypt(final String algorithm, final SecretKey secretKey, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encrypted);
    }
}