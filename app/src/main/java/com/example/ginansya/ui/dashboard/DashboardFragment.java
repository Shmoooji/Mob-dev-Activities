package com.example.ginansya.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ginansya.R;
import com.example.ginansya.data.CalculatorState;
import com.example.ginansya.domain.CompoundInterestCalculator;
import com.example.ginansya.util.CurrencyUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton openCalc = view.findViewById(R.id.home_open_calc);
        openCalc.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.calculatorFragment));
    }

    @Override
    public void onResume() {
        super.onResume();
        View root = requireView();
        CompoundInterestCalculator.Result r = CalculatorState.getResult(requireContext());

        ((TextView) root.findViewById(R.id.home_future_value))
                .setText(CurrencyUtils.format(r.futureValue));
        ((TextView) root.findViewById(R.id.home_contributed))
                .setText(CurrencyUtils.format(r.totalContributed));
        ((TextView) root.findViewById(R.id.home_interest))
                .setText(CurrencyUtils.format(Math.max(0, r.interestEarned)));

        TextView insightText = root.findViewById(R.id.home_insight_text);
        if (r.futureValue > 0 && r.interestEarned > 0) {
            int pct = (int) Math.round(r.interestEarned / r.futureValue * 100);
            insightText.setText(getString(R.string.home_insight_growth_fmt,
                    CurrencyUtils.format(Math.round(r.interestEarned)), pct));
        } else {
            insightText.setText(R.string.home_insight_default);
        }

        MaterialCardView insightCard = root.findViewById(R.id.home_insight_card);
        insightCard.setAlpha(0f);
        insightCard.setTranslationY(12f);
        insightCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(320)
                .start();
    }
}
