package com.ncnf.views.fragments.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.ncnf.R;
import com.ncnf.repositories.EventRepository;
import com.ncnf.utilities.InputValidator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.ncnf.utilities.StringCodes.UUID_KEY;

@AndroidEntryPoint
public class EventNewsFragment extends Fragment {

    @Inject
    EventRepository eventRepository;

    private String eventUUID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.eventUUID = this.getArguments().getString(UUID_KEY);
        return inflater.inflate(R.layout.fragment_event_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initForm(view);
    }

    private void initForm(View view) {
        Button publishButton = view.findViewById(R.id.event_news_publish_button);
        EditText textField = view.findViewById(R.id.event_news_field);

        publishButton.setOnClickListener(b -> {
            if (InputValidator.verifyGenericInput(textField)) {
                textField.setEnabled(false);
                eventRepository.addNews(eventUUID, textField.getText().toString()).thenAccept(res -> {
                    FragmentManager fm = getParentFragmentManager();
                    fm.popBackStack();
                    Snackbar bar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "News published !", LENGTH_LONG);
                    bar.show();
                }).exceptionally(exception -> {
                    Snackbar bar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Could not publish the news ! Try again later.", LENGTH_LONG);
                    bar.show();
                    return null;
                });
            }
        });
    }


}
