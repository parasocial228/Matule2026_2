package com.example.matule2026;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class creat_password extends AppCompatActivity {

    private EditText editTextPassword;
    private EditText editTextPassword2;
    private ImageView imageViewEye1;
    private ImageView imageViewEye2;
    private Button buttonNext;
    private boolean isEye1Closed = true;
    private boolean isEye2Closed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creat_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получаем только имя и email из предыдущей активности
        Intent intent = getIntent();
        final String userName = intent.getStringExtra("USER_NAME");
        final String userEmail = intent.getStringExtra("USER_EMAIL");

        initViews();
        setupTextWatchers();
        setupButtonClickListener(userName, userEmail);
        setupEyeClickListeners();
        updateButtonState();
    }

    private void initViews() {
        // Находим первый include для пароля по новому ID
        View includePassword1 = findViewById(R.id.firstPassword);
        if (includePassword1 != null) {
            // Находим EditText внутри include
            editTextPassword = includePassword1.findViewById(com.example.ui_kit.R.id.editTextTextPassword);
            // Находим ImageView глаза внутри include
            imageViewEye1 = includePassword1.findViewById(com.example.ui_kit.R.id.imageView6);
        }

        // Находим второй include для повторного ввода пароля по новому ID
        View includePassword2 = findViewById(R.id.secondPassword);
        if (includePassword2 != null) {
            // Находим EditText внутри include
            editTextPassword2 = includePassword2.findViewById(com.example.ui_kit.R.id.editTextTextPassword);
            // Находим ImageView глаза внутри include
            imageViewEye2 = includePassword2.findViewById(com.example.ui_kit.R.id.imageView6);
        }

        // Находим кнопку из include (ищем Button в основном layout, так как include без ID)
        // Ищем сначала в include, потом напрямую
        buttonNext = null;

        // Ищем кнопку в include button_big (если она есть с ID)
        View includeButton = findViewById(com.example.ui_kit.R.id.big_button);
        if (includeButton instanceof Button) {
            buttonNext = (Button) includeButton;
        }

        // Если не нашли, ищем напрямую по ID из ui_kit
        if (buttonNext == null) {
            buttonNext = findViewById(com.example.ui_kit.R.id.big_button);
        }


        if (buttonNext != null) {
            buttonNext.setText("Сохранить");
        }
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        if (editTextPassword != null) editTextPassword.addTextChangedListener(textWatcher);
        if (editTextPassword2 != null) editTextPassword2.addTextChangedListener(textWatcher);
    }

    private void setupButtonClickListener(final String userName, final String userEmail) {
        if (buttonNext != null) {
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValidForm()) {
                        String password = editTextPassword != null ? editTextPassword.getText().toString().trim() : "";

                        // Переход на следующую активность с передачей только имени и email
                        Intent intent = new Intent(creat_password.this, creat_password_login.class);
                        intent.putExtra("USER_NAME", userName);
                        intent.putExtra("USER_EMAIL", userEmail);
                        intent.putExtra("USER_PASSWORD", password);

                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void setupEyeClickListeners() {
        if (imageViewEye1 != null) {
            imageViewEye1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    togglePasswordVisibility(editTextPassword, imageViewEye1, isEye1Closed);
                    isEye1Closed = !isEye1Closed;
                }
            });
        }

        if (imageViewEye2 != null) {
            imageViewEye2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    togglePasswordVisibility(editTextPassword2, imageViewEye2, isEye2Closed);
                    isEye2Closed = !isEye2Closed;
                }
            });
        }
    }

    private void togglePasswordVisibility(EditText editText, ImageView imageView, boolean isClosed) {
        if (editText == null || imageView == null) return;

        if (isClosed) {
            // Показываем пароль - меняем на eye_open
            imageView.setImageResource(com.example.ui_kit.R.drawable.eye_open);
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            // Скрываем пароль - меняем на eye_close
            imageView.setImageResource(com.example.ui_kit.R.drawable.eye_close);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        // Перемещаем курсор в конец текста
        editText.setSelection(editText.getText().length());
    }

    private void updateButtonState() {
        String password1 = editTextPassword != null ? editTextPassword.getText().toString().trim() : "";
        String password2 = editTextPassword2 != null ? editTextPassword2.getText().toString().trim() : "";

        boolean isValid = isStrongPassword(password1) && password1.equals(password2);

        if (buttonNext != null) {
            buttonNext.setEnabled(isValid);

            if (isValid) {
                buttonNext.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, com.example.ui_kit.R.color.accent)));
                buttonNext.setAlpha(1.0f);
            } else {
                buttonNext.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, com.example.ui_kit.R.color.accent_inactive)));
                buttonNext.setAlpha(0.7f);
            }
        }
    }

    private boolean isValidForm() {
        String password1 = editTextPassword != null ? editTextPassword.getText().toString().trim() : "";
        String password2 = editTextPassword2 != null ? editTextPassword2.getText().toString().trim() : "";

        if (!password1.equals(password2)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isStrongPassword(password1)) {
            showPasswordRequirements();
            return false;
        }

        return true;
    }

    private boolean isStrongPassword(String password) {
        if (password.length() < 6) return false;

        boolean hasUpperCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) hasSpecialChar = true;

            // Если все условия выполнены, выходим раньше
            if (hasUpperCase && hasDigit && hasSpecialChar) break;
        }

        return hasUpperCase && hasDigit && hasSpecialChar;
    }

    private void showPasswordRequirements() {
        String password = editTextPassword != null ? editTextPassword.getText().toString().trim() : "";

        StringBuilder message = new StringBuilder("Пароль должен содержать:\n");

        if (password.length() < 6) {
            message.append("- Не менее 6 символов\n");
        }

        boolean hasUpperCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) hasSpecialChar = true;
        }

        if (!hasUpperCase) {
            message.append("- Хотя бы одну заглавную букву\n");
        }

        if (!hasDigit) {
            message.append("- Хотя бы одну цифру\n");
        }

        if (!hasSpecialChar) {
            message.append("- Хотя бы один специальный символ (!@#$%^&* и т.д.)\n");
        }

        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
    }
}