package store.aurora.common.encryptor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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
        String data = String.valueOf(2L);
        String encrypted = simpleEncryptor.encrypt(data);
        log.info("{}", encrypted);
        String decryted = simpleEncryptor.decrypt(encrypted);

        Assertions.assertEquals(data, decryted);
    }
}