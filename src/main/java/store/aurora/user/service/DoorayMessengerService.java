package store.aurora.user.service;

public interface DoorayMessengerService {
    void sendVerificationCode(String phoneNumber, String verificationCode);
    boolean verifyCode(String phoneNumber, String inputCode);
}
