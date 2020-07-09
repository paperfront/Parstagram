package com.example.parstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentEditProfileBinding;
import com.example.parstagram.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileDialogFragment extends DialogFragment {

    public static final String TAG = "ProfileDialogFragment";

    private EditText etDescription;
    private Button btConfirm;
    private final User currentUser = (User) ParseUser.getCurrentUser();
    private final String originalDescription = currentUser.getDescription();

    private FragmentEditProfileBinding binding;

    public EditProfileDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditProfileDialogFragment newInstance() {
        EditProfileDialogFragment frag = new EditProfileDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentEditProfileBinding.bind(view);
        bindElements();
        setupElements();
        // Show soft keyboard automatically and request focus to field
        etDescription.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void bindElements() {
        etDescription = binding.etDescription;
        btConfirm = binding.btConfirm;
    }

    private void setupElements() {
        getDialog().setTitle("Edit Profile");
        etDescription.setText(originalDescription);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String enteredText = etDescription.getText().toString();
                if (enteredText.equals(originalDescription)) {
                    dismiss();
                } else {
                    currentUser.setDescription(enteredText);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Failed to save new description", e);
                                Toast.makeText(getContext(), "Description failed to save.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i(TAG, "Successfully updated description.");
                                sendBackResult(enteredText);
                            }
                        }
                    });
                }
            }
        });
    }

    public interface EditProfileDialogListener {
        void onFinishEditDialog(String inputText);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(String newDescription) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        EditProfileDialogListener listener = (EditProfileDialogListener) getTargetFragment();
        listener.onFinishEditDialog(newDescription);
        dismiss();
    }
}
