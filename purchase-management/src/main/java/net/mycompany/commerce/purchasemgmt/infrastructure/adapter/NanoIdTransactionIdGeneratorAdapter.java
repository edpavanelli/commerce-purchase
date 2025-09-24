package net.mycompany.commerce.purchasemgmt.infrastructure.adapter;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import net.mycompany.commerce.purchasemgmt.domain.port.TransactionIdGeneratorPort;

@Component
public class NanoIdTransactionIdGeneratorAdapter implements TransactionIdGeneratorPort {
	
	private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int SIZE = 15;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String nextId() {
        return NanoIdUtils.randomNanoId(SECURE_RANDOM, ALPHABET, SIZE);
    }

}
