package com.assignment.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.log4j.Logger;

/**
 * Utilities class to operate on Amounts
 */
public class AmountUtil {

    public static final String DEFAULT_CURRENCY = "INR";

    static final Logger log = Logger.getLogger(AmountUtil.class);

    public static String setDisplayAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN)
                .toPlainString();
    }

    /**
     * @param inputCurrencyCode String Currency code to be validated
     * @return true if currency code is valid ISO code, false otherwise
     */
    public static boolean validateCurrencyCode(String inputCurrencyCode) {
        try {
            Currency instance = Currency.getInstance(inputCurrencyCode);
            return instance.getCurrencyCode().equals(inputCurrencyCode);
        } catch (Exception e) {
            log.warn("Cannot parse the input Currency Code, Validation Failed: ", e);
        }
        return false;
    }

    public static class AmountSerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(setDisplayAmount(value));
        }
    }

}
