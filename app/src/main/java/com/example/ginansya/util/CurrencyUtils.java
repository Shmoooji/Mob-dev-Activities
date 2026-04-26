package com.example.ginansya.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtils {

    private static final String SYMBOL = "₱";
    private static final NumberFormat CURRENCY;
    private static final NumberFormat COMPACT;

    static {
        CURRENCY = build();
        CURRENCY.setMaximumFractionDigits(2);
        CURRENCY.setMinimumFractionDigits(0);
        COMPACT = build();
        COMPACT.setMaximumFractionDigits(0);
        COMPACT.setMinimumFractionDigits(0);
    }

    private static NumberFormat build() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH"));
        if (nf instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) nf;
            DecimalFormatSymbols sym = df.getDecimalFormatSymbols();
            sym.setCurrencySymbol(SYMBOL);
            df.setDecimalFormatSymbols(sym);
        }
        return nf;
    }

    private CurrencyUtils() {}

    public static String format(double amount) {
        if (amount == Math.floor(amount)) {
            return COMPACT.format(amount);
        }
        return CURRENCY.format(amount);
    }

    public static String formatCompact(double amount) {
        if (Math.abs(amount) >= 1_000_000) {
            return SYMBOL + round(amount / 1_000_000.0, 1) + "M";
        }
        if (Math.abs(amount) >= 1_000) {
            return SYMBOL + round(amount / 1_000.0, 1) + "k";
        }
        return COMPACT.format(amount);
    }

    private static String round(double v, int decimals) {
        double factor = Math.pow(10, decimals);
        double r = Math.round(v * factor) / factor;
        if (r == Math.floor(r)) return String.valueOf((long) r);
        return String.valueOf(r);
    }
}
