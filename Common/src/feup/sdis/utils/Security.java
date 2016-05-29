package feup.sdis.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Security utilities class
 */
public class Security {

    /**
     * Initialization vector
     */
    private static final byte[] IV = new byte[]
            {
                    0x00, 0x01, 0x02, 0x03,
                    0x04, 0x05, 0x06, 0x07,
                    0x08, 0x09, 0x0a, 0x0b,
                    0x0c, 0x0d, 0x0e, 0x0f
            };

    /**
     * Generate a secret key
     *
     * @param algorithm algorithm to generate the secret key
     * @return generated secret key
     * @throws NoSuchAlgorithmException when there is no such algorithm
     */
    public static SecretKey generateSecretKey(final String algorithm) throws NoSuchAlgorithmException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(128);

        byte[] key = keyGenerator.generateKey().getEncoded();
        return new SecretKeySpec(key, algorithm);
    }

    /**
     * Recover a secret key given its algorithm and encoded bytes
     *
     * @param algorithm algorithm of the secret key
     * @param encoded   encoded bytes of the secret key
     * @return recovered secret key
     */
    public static SecretKey recoverSecretKey(final String algorithm, final byte[] encoded) {
        return new SecretKeySpec(encoded, algorithm);
    }

    /**
     * Encrypt a byte array to a fixed size 128 byte array
     *
     * @param algorithm algorithm to be used when encrypting
     * @param secretKey secret key to use to encrypt
     * @param plain     plain byte to be encrypted
     * @return encrypted 128 length byte array
     */
    public static byte[] encrypt(final String algorithm, final SecretKey secretKey, byte[] plain) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        final Cipher cipher = Cipher.getInstance(algorithm + "/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));

        return cipher.doFinal(plain);
    }

    /**
     * Decrypt a byte array to its original content
     *
     * @param algorithm algorithm to be used when decrypting
     * @param secretKey secret key to use to decrypt
     * @param encrypted encrypted byte to be decrypted
     * @return decrypted byte array
     */
    public static byte[] decrypt(final String algorithm, final SecretKey secretKey, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        final Cipher cipher = Cipher.getInstance(algorithm + "/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));

        return cipher.doFinal(encrypted);
    }

    /**
     * Calculate the checksum of a data array
     * @param data data to check the checksum
     * @return checksum of that data
     */
    public static long checksum(byte[] data) {
        final Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);

        return checksum.getValue();
    }
}