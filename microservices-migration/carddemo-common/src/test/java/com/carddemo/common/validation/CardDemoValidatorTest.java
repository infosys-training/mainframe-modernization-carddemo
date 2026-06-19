package com.carddemo.common.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardDemoValidatorTest {

    @Test
    void isValidStateCode_validCodes() {
        assertTrue(CardDemoValidator.isValidStateCode("CA"));
        assertTrue(CardDemoValidator.isValidStateCode("NY"));
        assertTrue(CardDemoValidator.isValidStateCode("TX"));
        assertTrue(CardDemoValidator.isValidStateCode("NC"));
        assertTrue(CardDemoValidator.isValidStateCode("DC"));
    }

    @Test
    void isValidStateCode_invalidCodes() {
        assertFalse(CardDemoValidator.isValidStateCode("XX"));
        assertFalse(CardDemoValidator.isValidStateCode("ZZ"));
        assertFalse(CardDemoValidator.isValidStateCode(null));
        assertFalse(CardDemoValidator.isValidStateCode(""));
    }

    @Test
    void isValidSsn_valid() {
        assertTrue(CardDemoValidator.isValidSsn("020973888"));
        assertTrue(CardDemoValidator.isValidSsn("587518382"));
    }

    @Test
    void isValidSsn_invalid() {
        assertFalse(CardDemoValidator.isValidSsn("12345"));
        assertFalse(CardDemoValidator.isValidSsn("1234567890"));
        assertFalse(CardDemoValidator.isValidSsn(null));
    }

    @Test
    void isValidAccountId_valid() {
        assertTrue(CardDemoValidator.isValidAccountId("00000000001"));
        assertTrue(CardDemoValidator.isValidAccountId("12345678901"));
    }

    @Test
    void isValidAccountId_invalid() {
        assertFalse(CardDemoValidator.isValidAccountId("123"));
        assertFalse(CardDemoValidator.isValidAccountId("ABCDE123456"));
        assertFalse(CardDemoValidator.isValidAccountId(null));
    }

    @Test
    void isValidCardNumber_valid() {
        assertTrue(CardDemoValidator.isValidCardNumber("0500024453765740"));
    }

    @Test
    void isValidCardNumber_invalid() {
        assertFalse(CardDemoValidator.isValidCardNumber("12345"));
        assertFalse(CardDemoValidator.isValidCardNumber(null));
    }

    @Test
    void isValidDate_valid() {
        assertTrue(CardDemoValidator.isValidDate("2024-01-15"));
        assertTrue(CardDemoValidator.isValidDate("2014-11-20"));
    }

    @Test
    void isValidDate_invalid() {
        assertFalse(CardDemoValidator.isValidDate("2024-13-01"));
        assertFalse(CardDemoValidator.isValidDate("not-a-date"));
        assertFalse(CardDemoValidator.isValidDate(null));
    }

    @Test
    void isValidFicoScore_valid() {
        assertTrue(CardDemoValidator.isValidFicoScore(300));
        assertTrue(CardDemoValidator.isValidFicoScore(850));
        assertTrue(CardDemoValidator.isValidFicoScore(650));
    }

    @Test
    void isValidFicoScore_invalid() {
        assertFalse(CardDemoValidator.isValidFicoScore(100));
        assertFalse(CardDemoValidator.isValidFicoScore(900));
        assertFalse(CardDemoValidator.isValidFicoScore(0));
    }
}
