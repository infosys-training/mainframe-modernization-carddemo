package com.carddemo.migration;

import com.carddemo.migration.reader.CobolRecordParser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CobolRecordParserTest {

    @Test
    void parseSignedDecimal_positiveZero() {
        assertEquals(new BigDecimal("0.00"), CobolRecordParser.parseSignedDecimal("00000000000{", 2));
    }

    @Test
    void parseSignedDecimal_positiveValue_1940() {
        // 00000001940{ → { = +0, so value = 00000001940 + 0 = 19400 → /100 = 194.00
        assertEquals(new BigDecimal("194.00"), CobolRecordParser.parseSignedDecimal("00000001940{", 2));
    }

    @Test
    void parseSignedDecimal_negativeValue() {
        // 0000009190} → } = -0, so value = 00000091900 → /100 = -919.00
        assertEquals(new BigDecimal("-919.00"), CobolRecordParser.parseSignedDecimal("0000009190}", 2));
    }

    @Test
    void parseSignedDecimal_positiveWithLetterG() {
        // 0000000678H → H = +8, so value = 00000006788 → /100 = 67.88
        assertEquals(new BigDecimal("67.88"), CobolRecordParser.parseSignedDecimal("0000000678H", 2));
    }

    @Test
    void parseSignedDecimal_negativeWithLetterJ() {
        // 0000009190J → J = -1, so value = 00000091901 → /100 = -919.01
        // Wait, let me recalculate: J=-1, prefix=0000009190, digit=1, numStr=00000091901
        // movePointLeft(2) = 919.01, negative → -919.01
        assertEquals(new BigDecimal("-919.01"), CobolRecordParser.parseSignedDecimal("0000009190J", 2));
    }

    @Test
    void parseSignedDecimal_largeCreditLimit() {
        // 00000020200{ → prefix=0000002020, digit=0, numStr=00000020200 → /100 = 2020.00
        assertEquals(new BigDecimal("2020.00"), CobolRecordParser.parseSignedDecimal("00000020200{", 2));
    }

    @Test
    void parseSignedDecimal_emptyString_returnsZero() {
        assertEquals(BigDecimal.ZERO, CobolRecordParser.parseSignedDecimal("", 2));
    }

    @Test
    void parseSignedDecimal_null_returnsZero() {
        assertEquals(BigDecimal.ZERO, CobolRecordParser.parseSignedDecimal(null, 2));
    }

    @Test
    void extract_validRange() {
        String record = "00000000001Y";
        assertEquals("00000000001", CobolRecordParser.extract(record, 1, 11));
        assertEquals("Y", CobolRecordParser.extract(record, 12, 1));
    }

    @Test
    void parseUnsignedNumeric_validNumber() {
        assertEquals(11L, CobolRecordParser.parseUnsignedNumeric("00000000011"));
    }

    @Test
    void parseUnsignedNumeric_blank_returnsZero() {
        assertEquals(0L, CobolRecordParser.parseUnsignedNumeric("   "));
    }
}
