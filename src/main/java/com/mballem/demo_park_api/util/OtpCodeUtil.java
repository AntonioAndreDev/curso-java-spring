package com.mballem.demo_park_api.util;

import java.util.Random;

public class OtpCodeUtil {
    String numbers = "0123456789";

    Random random = new Random();

    char[] otp = new char[6];

    public String genOtpCode() {
        for (int i = 0; i < 6; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return new String(otp);
    }
}
