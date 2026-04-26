package com.example.ginansya.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ginansya.domain.CompoundInterestCalculator;

public final class CalculatorState {

    private static final String FILE = "calculator_state";
    private static final String KEY_PRINCIPAL = "principal";
    // Stored under "monthly" historically; semantics now: deposit per compounding period.
    private static final String KEY_DEPOSIT = "monthly";
    private static final String KEY_RATE = "rate";
    private static final String KEY_YEARS = "years";
    private static final String KEY_COMPOUNDS_PER_YEAR = "compounds_per_year";

    private static final int DEFAULT_COMPOUNDS_PER_YEAR = 12;

    private CalculatorState() {}

    public static CompoundInterestCalculator.Result update(Context ctx,
                                                           double principal,
                                                           double periodicContribution,
                                                           double rate,
                                                           double years,
                                                           int compoundsPerYear) {
        prefs(ctx).edit()
                .putString(KEY_PRINCIPAL, String.valueOf(principal))
                .putString(KEY_DEPOSIT, String.valueOf(periodicContribution))
                .putString(KEY_RATE, String.valueOf(rate))
                .putString(KEY_YEARS, String.valueOf(years))
                .putInt(KEY_COMPOUNDS_PER_YEAR, compoundsPerYear)
                .apply();
        return CompoundInterestCalculator.project(
                principal, periodicContribution, rate, years, compoundsPerYear);
    }

    public static double getPrincipal(Context ctx) {
        return readDouble(ctx, KEY_PRINCIPAL);
    }

    public static double getDeposit(Context ctx) {
        return readDouble(ctx, KEY_DEPOSIT);
    }

    public static double getRate(Context ctx) {
        return readDouble(ctx, KEY_RATE);
    }

    public static double getYears(Context ctx) {
        return readDouble(ctx, KEY_YEARS);
    }

    public static int getCompoundsPerYear(Context ctx) {
        return prefs(ctx).getInt(KEY_COMPOUNDS_PER_YEAR, DEFAULT_COMPOUNDS_PER_YEAR);
    }

    public static CompoundInterestCalculator.Result getResult(Context ctx) {
        return CompoundInterestCalculator.project(
                getPrincipal(ctx),
                getDeposit(ctx),
                getRate(ctx),
                getYears(ctx),
                getCompoundsPerYear(ctx));
    }

    private static double readDouble(Context ctx, String key) {
        SharedPreferences p = prefs(ctx);
        try {
            String s = p.getString(key, "0");
            return Double.parseDouble(s);
        } catch (ClassCastException e) {
            int legacyInt = p.getInt(key, 0);
            p.edit().putString(key, String.valueOf(legacyInt)).apply();
            return legacyInt;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getApplicationContext()
                .getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
