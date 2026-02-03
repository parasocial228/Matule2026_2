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

        this.initViews();
        this.setupPasswordVisibilityToggle();
        this.setupTextWatchers();
        this.updateButtonState();
    }

    private void initViews() {
        // Email поле из текущего модуля
        this.emailEditText = (EditText) this.findViewById(R.id.editTextTextEmailAddress);

        // Находим include с кнопкой и ищем кнопку внутри него
        View includeButton = this.findViewById(R.id.includeButton);
        if (includeButton != null) {
            this.loginButton = (Button) includeButton.findViewById(com.example.ui_kit.R.id.big_button);
            if (this.loginButton != null) {
                this.loginButton.setText("Далее");
                // Кнопка всегда активна для обработки кликов
                this.loginButton.setEnabled(true);
                this.loginButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String email = registration_login.this.emailEditText.getText().toString().trim();
                        String password = registration_login.this.passwordEditText.getText().toString().trim();

                        boolean emailValid = registration_login.this.isValidEmail(email);
                        boolean passwordValid = !TextUtils.isEmpty(password);

                        if (emailValid && passwordValid) {
                            // Сохраняем данные в SharedPreferencesHelper
                            prefsHelper.setFirstLaunchComplete();
                            prefsHelper.setUserRegistered(true);
                            prefsHelper.setRegistrationSkipped(false);
                            prefsHelper.saveUserEmail(email);

                            // Сохраняем в user_data для обратной совместимости
                            SharedPreferences userPrefs = getSharedPreferences("user_data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = userPrefs.edit();
                            editor.putString("user_email", email);
                            editor.putString("user_password", password);
                            editor.apply();

                            Intent intent = new Intent(registration_login.this, profile.class);
                            intent.putExtra("email", email);
                            registration_login.this.startActivity(intent);
                            registration_login.this.finish();
                        } else {
                            registration_login.this.showErrorMessage(email, password);
                        }
                    }
                });
            }
        }

        // Для пароля: находим LinearLayout с input_password
        View rootView = findViewById(android.R.id.content);
        View passwordContainer = findPasswordContainer(rootView);

        if (passwordContainer != null) {
            this.passwordEditText = (EditText) passwordContainer.findViewById(com.example.ui_kit.R.id.editTextTextPassword);
            this.passwordVisibilityIcon = (ImageView) passwordContainer.findViewById(com.example.ui_kit.R.id.imageView6);

            // По умолчанию скрываем иконку видимости
            if (this.passwordVisibilityIcon != null) {
                this.passwordVisibilityIcon.setVisibility(View.GONE);
            }

            if (this.passwordEditText != null) {
                // Обработчик фокуса на EditText для пароля
                this.passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (passwordVisibilityIcon != null) {
                            if (hasFocus) {
                                passwordVisibilityIcon.setVisibility(View.VISIBLE);
                            } else {
                                passwordVisibilityIcon.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        }
    }

    // Метод для поиска контейнера с паролем в иерархии вью
    private View findPasswordContainer(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            // Проверяем, является ли текущий вью LinearLayout с нужным background
            if (view instanceof android.widget.LinearLayout) {
                android.widget.LinearLayout linearLayout = (android.widget.LinearLayout) view;
                // Проверяем наличие EditText и ImageView внутри
                EditText editText = linearLayout.findViewById(com.example.ui_kit.R.id.editTextTextPassword);
                ImageView imageView = linearLayout.findViewById(com.example.ui_kit.R.id.imageView6);

                if (editText != null && imageView != null) {
                    return linearLayout;
                }
            }

            // Рекурсивно проверяем дочерние элементы
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View found = findPasswordContainer(viewGroup.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private void setupPasswordVisibilityToggle() {
        if (this.passwordVisibilityIcon != null && this.passwordEditText != null) {
            this.passwordVisibilityIcon.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    registration_login.this.togglePasswordVisibility();
                }
            });
        }
    }

    private void togglePasswordVisibility() {
        if (this.passwordEditText == null || this.passwordVisibilityIcon == null) return;

        if (this.isPasswordVisible) {
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordVisibilityIcon.setImageResource(com.example.ui_kit.R.drawable.eye_close);
            this.isPasswordVisible = false;
        } else {
            this.passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            this.passwordVisibilityIcon.setImageResource(com.example.ui_kit.R.drawable.eye_open);
            this.isPasswordVisible = true;
        }

        this.passwordEditText.setSelection(this.passwordEditText.getText().length());
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registration_login.this.updateButtonState();
            }

            public void afterTextChanged(Editable s) {
            }
        };

        if (this.emailEditText != null) {
            this.emailEditText.addTextChangedListener(textWatcher);
        }

        if (this.passwordEditText != null) {
            this.passwordEditText.addTextChangedListener(textWatcher);
        }
    }

    private void updateButtonState() {
        if (this.emailEditText == null || this.passwordEditText == null || this.loginButton == null) {
            return;
        }

        String email = this.emailEditText.getText().toString().trim();
        String password = this.passwordEditText.getText().toString().trim();
        boolean emailValid = this.isValidEmail(email);
        boolean passwordValid = !TextUtils.isEmpty(password);
        this.isFormValid = emailValid && passwordValid;

        // Меняем только внешний вид кнопки, но оставляем ее активной
        if (this.isFormValid) {
            this.loginButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, com.example.ui_kit.R.color.accent)));
        } else {
            this.loginButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, com.example.ui_kit.R.color.accent_inactive)));
        }

        this.loginButton.setAlpha(this.isFormValid ? 1.0F : 0.7F);
        // Кнопка всегда остается enabled, чтобы обрабатывать клики
        this.loginButton.setEnabled(true);
    }

    private boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        // Паттерн: только маленькие буквы и цифры для имени и доменного имени
        String emailPattern = "^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$";

        // Проверяем соответствие паттерну
        if (!email.matches(emailPattern)) {
            return false;
        }

        // Дополнительная проверка на .ru окончание
        return email.toLowerCase().endsWith(".ru");
    }

    private void showErrorMessage(String email, String password) {
        // Сначала скрываем предыдущее сообщение, если оно показывается
        if (errorPopup != null && errorPopup.isShowing()) {
            errorPopup.dismiss();
        }

        // Загружаем layout для сообщения об ошибке
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.message_error, null);

        // Устанавливаем текст ошибки
        android.widget.TextView errorTextView = popupView.findViewById(R.id.textView22);
        String errorMessage = getErrorMessage(email, password);
        errorTextView.setText(errorMessage);

        // Настраиваем PopupWindow
        errorPopup = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Настраиваем фон
        errorPopup.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        errorPopup.setOutsideTouchable(false);

        // Находим кнопку закрытия и настраиваем обработчик
        ImageView dismissButton = popupView.findViewById(R.id.imageView);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (errorPopup != null && errorPopup.isShowing()) {
                    errorPopup.dismiss();
                }
            }
        });

        // Показываем сообщение об ошибке по центру экрана
        View mainView = findViewById(R.id.main);
        errorPopup.showAtLocation(
                mainView,
                android.view.Gravity.CENTER,
                0,
                0
        );

        // Автоматически скрываем через 5 секунд
        mainView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (errorPopup != null && errorPopup.isShowing()) {
                    errorPopup.dismiss();
                }
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
            // Более детальная проверка для email
            if (!email.contains("@")) {
                return "Email должен содержать @";
            } else if (!email.toLowerCase().endsWith(".ru")) {
                return "Email должен заканчиваться на .ru";
            } else if (!email.matches("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")) {
                return "Email должен содержать только строчные буквы и цифры";
            } else {
                return "Некорректный email";
            }
        } else {
            return "Ошибка при заполнении формы";
        }
    }

    public void openCreatProfile(View view) {
        prefsHelper.setFirstLaunchComplete();
        prefsHelper.setRegistrationSkipped(false);

        Intent i = new Intent(this, profile_create.class);
        this.startActivity(i);
        this.finish();
    }

    public void skip(View view) {
        prefsHelper.setFirstLaunchComplete();
        prefsHelper.setUserRegistered(true);
        prefsHelper.setRegistrationSkipped(true);
        prefsHelper.setHasProfile(true);

        Intent i = new Intent(this, profile.class);
        this.startActivity(i);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (errorPopup != null && errorPopup.isShowing()) {
            errorPopup.dismiss();
        }
    }
}