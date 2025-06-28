package com.localcoupon.couponservice.common.util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public static String encrypt(String password) {
        char[] passwordChars = password.toCharArray();
        try {
            return argon2.hash(2, 65536, 1, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }

    public static boolean decrypt(String hash, String password) {
        char[] passwordChars = password.toCharArray();
        try {
            return argon2.verify(hash, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }
}
