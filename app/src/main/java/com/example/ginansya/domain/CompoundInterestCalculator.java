package com.example.ginansya.domain;

import java.util.ArrayList;
import java.util.List;

public final class CompoundInterestCalculator {

    public static class Result {
        public final double futureValue;
        public final double totalContributed;
        public final double interestEarned;
        public final List<Double> periodBalances;

        Result(double futureValue, double totalContributed,
               double interestEarned, List<Double> periodBalances) {
            this.futureValue = futureValue;
            this.totalContributed = totalContributed;
            this.interestEarned = interestEarned;
            this.periodBalances = periodBalances;
        }
    }

    private CompoundInterestCalculator() {}

    public static Result project(double principal,
                                 double periodicContribution,
                                 double annualRatePct,
                                 double years,
                                 int compoundsPerYear) {
        int n = Math.max(1, compoundsPerYear);
        double periodRate = annualRatePct / 100.0 / n;
        int totalPeriods = Math.max(1, (int) Math.round(years * n));

        double balance = principal;
        List<Double> series = new ArrayList<>(totalPeriods + 1);
        series.add(balance);

        for (int p = 1; p <= totalPeriods; p++) {
            balance = balance * (1 + periodRate) + periodicContribution;
            series.add(balance);
        }

        double contributed = principal + periodicContribution * totalPeriods;
        double interest = balance - contributed;
        return new Result(balance, contributed, interest, series);
    }
}
