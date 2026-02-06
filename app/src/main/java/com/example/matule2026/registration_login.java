package com.example.matule2026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class registration_login extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView passwordVisibilityIcon;
    private Button loginButton;
    private PopupWindow errorPopup;
    private boolean isPasswordVisible = false;
    private boolean isFormValid = false;
    private SharedPreferencesHelper prefsHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        prefsHelper = new SharedPreferencesHelper(this);

        initViews();
        setupPasswordVisibilityToggle();
        setupTextWatchers();
        updateButtonState();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        loginButton = findViewById(com.example.ui_kit.R.id.big_button);
        passwordEditText = findViewById(com.example.ui_kit.R.id.editTextTextPassword);
        passwordVisibilityIcon = findViewById(com.example.ui_kit.R.id.imageView6);

        if (loginButton != null) {
            loginButton.setText("Далее");
            loginButton.setOnClickListener(v -> handleLogin());
        }
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (isValidEmail(email) && !TextUtils.isEmpty(password)) {
            saveUserData(email, password);
            startActivity(new Intent(this, profile.class));
            finish();
        } else {
            showErrorMessage(email, password);
        }
    }

    private void saveUserData(String email, String password) {
        prefsHelper.setFirstLaunchComplete();
        prefsHelper.setUserRegistered(true);
        prefsHelper.setRegistrationSkipped(false);
        prefsHelper.saveUserEmail(email);

        SharedPreferences userPrefs = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putString("user_email", email);
        editor.putString("user_password", password);
        editor.apply();
    }

    private void setupPasswordVisibilityToggle() {
        if (passwordVisibilityIcon != null && passwordEditText != null) {
            passwordVisibilityIcon.setOnClickListener(v -> togglePasswordVisibility());
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisibilityIcon.setImageResource(com.example.ui_kit.R.drawable.eye_close);
        } else {
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisibilityIcon.setImageResource(com.example.ui_kit.R.drawable.eye_open);
        }
        isPasswordVisible = !isPasswordVisible;
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
            }
            public void afterTextChanged(Editable s) {}
        };

        if (emailEditText != null) {
            emailEditText.addTextChangedListener(textWatcher);
        }

        if (passwordEditText != null) {
            passwordEditText.addTextChangedListener(textWatcher);
        }
    }

    private void updateButtonState() {
        if (emailEditText == null || passwordEditText == null || loginButton == null) {
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        boolean emailValid = isValidEmail(email);
        boolean passwordValid = !TextUtils.isEmpty(password);
        isFormValid = emailValid && passwordValid;

        if (isFormValid) {
            loginButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, com.example.ui_kit.R.color.accent)));
        } else {
            loginButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, com.example.ui_kit.R.color.accent_inactive)));
        }

        loginButton.setAlpha(isFormValid ? 1.0F : 0.7F);
        loginButton.setEnabled(true); // Кнопка всегда кликабельна
    }

    private boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) return false;
        String emailPattern = "^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$";
        return email.matches(emailPattern) && email.toLowerCase().endsWith(".ru");
    }

    private void showErrorMessage(String email, String password) {
        if (errorPopup != null && errorPopup.isShowing()) {
            errorPopup.dismiss();
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.message_error, null);

        android.widget.TextView errorTextView = popupView.findViewById(R.id.textView22);
        errorTextView.setText(getErrorMessage(email, password));

        errorPopup = new PopupWindow(
                popupView,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        errorPopup.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        errorPopup.setOutsideTouchable(false);

        ImageView dismissButton = popupView.findViewById(R.id.imageView);
        dismissButton.setOnClickListener(v -> {
            if (errorPopup != null && errorPopup.isShowing()) {
                errorPopup.dismiss();
            }
        });

        View mainView = findViewById(R.id.main);
        errorPopup.showAtLocation(mainView, android.view.Gravity.CENTER, 0, 0);

        mainView.postDelayed(() -> {
            if (errorPopup != null && errorPopup.isShowing()) {
                errorPopup.dismiss();
            }
        }, 5000);
    }

    private String getErrorMessage(String email, String password) {
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            return "Введите email и пароль";
        } else if (TextUtils.isEmpty(email)) {
            return "Введите email";
        } else if (TextUtils.isEmpty(password)) {
            return "Введите пароль";
        } else if (!isValidEmail(email)) {
            if (!email.contains("@")) {
                return "Email должен содержать @";
            } else if (!email.toLowerCase().endsWith(".ru")) {
                return "Email должен заканчиваться на .ru";
            } else {
                return "Email должен содержать только строчные буквы и цифры";
            }
        } else {
            return "Ошибка при заполнении формы";
        }
    }

    public void openCreatProfile(View view) {
        prefsHelper.setFirstLaunchComplete();
        prefsHelper.setRegistrationSkipped(false);
        startActivity(new Intent(this, profile_create.class));
        finish();
    }

    public void skip(View view) {
        prefsHelper.setFirstLaunchComplete();
        prefsHelper.setUserRegistered(true);
        prefsHelper.setRegistrationSkipped(true);
        prefsHelper.setHasProfile(true);
        startActivity(new Intent(this, profile.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (errorPopup != null && errorPopup.isShowing()) {
            errorPopup.dismiss();
        }
    }
}