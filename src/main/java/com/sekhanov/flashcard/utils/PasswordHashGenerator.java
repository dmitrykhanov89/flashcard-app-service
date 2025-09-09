package com.sekhanov.flashcard.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String[] passwords = { "admin-2sS6r55Hv2b8bE", "user1-t03QRKrWsaLas9", "user2-KPYt3sz5UQdxRq" };

        for (String raw : passwords) {
            String hash = encoder.encode(raw);
            System.out.println(raw + " => " + hash);
        }
    }
}
