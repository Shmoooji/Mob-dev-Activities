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
        CompoundInterestCalculator.Result r = CalculatorState.getResult(requireContext());
        View root = requireView();
        ((TextView) root.findViewById(R.id.home_future_value))
                .setText(CurrencyUtils.format(r.futureValue));
        ((TextView) root.findViewById(R.id.home_contributed))
                .setText(CurrencyUtils.format(r.totalContributed));
        ((TextView) root.findViewById(R.id.home_interest))
                .setText(CurrencyUtils.format(Math.max(0, r.interestEarned)));
    }
}
