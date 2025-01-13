package store.aurora.common.encryptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleEncryptorTest {
    private SimpleEncryptor simpleEncryptor;

    @BeforeEach
    void setUp() {
        simpleEncryptor = new SimpleEncryptor();
    }

    @Test
    void encryptTest() {
        String data = "apple";
        String encrypted = simpleEncryptor.encrypt(data);
        String decryted = simpleEncryptor.decrypt(encrypted);

        Assertions.assertEquals(data, decryted);
    }

    @Test
    void encrytTest2() {
        String data = "payco:123jkhsdaf456fdlsj";
        String encrypted = simpleEncryptor.encrypt(data);
        String decryted = simpleEncryptor.decrypt(encrypted);

        Assertions.assertEquals(data, decryted);
    }

    @Test
    void encrytTest3() {
        String data = String.valueOf(1L);
        String encrypted = simpleEncryptor.encrypt(data);
        String decryted = simpleEncryptor.decrypt(encrypted);

        Assertions.assertEquals(data, decryted);
    }
}