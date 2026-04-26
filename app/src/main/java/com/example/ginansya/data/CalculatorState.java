package com.example.ginansya.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ginansya.domain.CompoundInterestCalculator;

public final class CalculatorState {

    private static final String FILE = "calculator_state";
    private static final String KEY_PRINCIPAL = "principal";
    private static final String KEY_MONTHLY = "monthly";
    private static final String KEY_RATE = "rate";
    private static final String KEY_YEARS = "years";

    private CalculatorState() {}

    public static CompoundInterestCalculator.Result update(Context ctx,
                                                           double principal,
                                                           double monthly,
                                                           double rate,
                                                           int years) {
        prefs(ctx).edit()
                .putString(KEY_PRINCIPAL, String.valueOf(principal))
                .putString(KEY_MONTHLY, String.valueOf(monthly))
                .putString(KEY_RATE, String.valueOf(rate))
                .putInt(KEY_YEARS, years)
                .apply();
        return CompoundInterestCalculator.project(principal, monthly, rate, years);
    }

    public static double getPrincipal(Context ctx) {
        return readDouble(ctx, KEY_PRINCIPAL);
    }

    public static double getMonthly(Context ctx) {
        return readDouble(ctx, KEY_MONTHLY);
    }

    public static double getRate(Context ctx) {
        return readDouble(ctx, KEY_RATE);
    }

    public static int getYears(Context ctx) {
        return prefs(ctx).getInt(KEY_YEARS, 0);
    }

    public static CompoundInterestCalculator.Result getResult(Context ctx) {
        return CompoundInterestCalculator.project(
                getPrincipal(ctx),
                getMonthly(ctx),
                getRate(ctx),
                getYears(ctx));
    }

    private static double readDouble(Context ctx, String key) {
        String s = prefs(ctx).getString(key, "0");
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getApplicationContext()
                .getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
