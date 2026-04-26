# Ginansya

Ginansya is an Android finance application developed using Java in Android Studio. The application was created to help users visualize how their savings can grow over time through compound interest and monthly contributions.

This project is a partial requirement of course CS 2203N - Mobile development:

Submitted by: Curtney Sealtiel Mata Juma-ang <br>
Submitted to: Mr. Jofferson Gonzales

The app currently contains two main screens:
- Home Dashboard
- Compound Interest Calculator

## Current Features

- Compound interest calculator with recurring deposits
- Real-time calculation updates while typing
- Savings growth visualization using charts
- Home dashboard showing latest financial projection
- Interest earned and total contribution breakdown
- Financial insight card based on current projection
- Offline functionality using SharedPreferences
- Simple and beginner-friendly Material Design interface

## Compound Interest Logic

The application uses the standard compound interest formula with recurring deposits added after every compounding period.

Instead of directly computing the final value using the closed-form formula, I implemented the logic using period-by-period iteration. This allows the app to generate the running balance history needed for the chart visualization while still keeping the calculations efficient.

The calculator computes:
- Future value
- Total contributed amount
- Interest earned
- Running balance history for charts

## App Flow

The application follows a simple two-screen structure:

```plaintext
Home Dashboard
      ↓
Compound Interest Calculator
      ↓
Back to Dashboard
```

## Notes

This project is intended for educational purposes only and does not connect to real banking systems or online services.
