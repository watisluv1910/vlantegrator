package com.wladischlau.vlt.core.integrator.utils;

import com.wladischlau.vlt.core.integrator.config.properties.VltProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class VersionHashGenerator {

    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private final int versionHashLength;

    public VersionHashGenerator(VltProperties props) {
        this.versionHashLength = props.getVersionHashLength();
    }

    public String generate() {
        int hexLen = versionHashLength;
        int byteLen = (hexLen + 1) / 2;

        byte[] rnd = new byte[byteLen];
        RNG.nextBytes(rnd);

        char[] out = new char[byteLen * 2];
        for (int i = 0; i < byteLen; i++) {
            int v = rnd[i] & 0xFF;
            out[i * 2] = HEX[v >>> 4];
            out[i * 2 + 1] = HEX[v & 0x0F];
        }

        return new String(out, 0, hexLen);
    }
}