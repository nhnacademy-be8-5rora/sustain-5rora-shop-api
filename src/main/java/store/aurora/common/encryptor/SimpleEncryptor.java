package store.aurora.common.encryptor;

import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class SimpleEncryptor {
    private static final String SECRET_KEY = "secretKeyAurora!";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private final SecretKeySpec secretKeySpec;

    public SimpleEncryptor() {
        secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    }

    public String encrypt(String data){
        try{
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            return Base64.getUrlEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException
                 | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    public String decrypt(String encryptedData) {
        try{
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] decoded = Base64.getUrlDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException
                 | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }
}
