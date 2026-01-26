package com.example.matule2026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class creat_password_login extends AppCompatActivity {
    private ImageView progress1, progress2, progress3, progress4;
    private List<Button> numberButtons = new ArrayList<>();
    private List<Integer> password = new ArrayList<>();
    private Handler handler = new Handler();

    // Переменные для хранения только имени и почты
    private String userName;
    private String userEmail;
    private String userPasswordFromPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creat_password_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получаем только имя и email из creat_password
        Intent intent = getIntent();
        userName = intent.getStringExtra("USER_NAME");
        userEmail = intent.getStringExtra("USER_EMAIL");
        userPasswordFromPrevious = intent.getStringExtra("USER_PASSWORD");

        initializeViews();
        setupClickListeners();
        resetProgressCircles();
    }

    private void initializeViews() {
        // Инициализация прогресс-кружков
        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        progress4 = findViewById(R.id.progress4);

        // Инициализация цифровых кнопок
        numberButtons.add(findViewById(R.id.password1));
        numberButtons.add(findViewById(R.id.password2));
        numberButtons.add(findViewById(R.id.password3));
        numberButtons.add(findViewById(R.id.password4));
        numberButtons.add(findViewById(R.id.password5));
        numberButtons.add(findViewById(R.id.password6));
        numberButtons.add(findViewById(R.id.password7));
        numberButtons.add(findViewById(R.id.password8));
        numberButtons.add(findViewById(R.id.password9));
        numberButtons.add(findViewById(R.id.password0));
    }

    private void setupClickListeners() {
        // Обработчики для цифровых кнопок
        for (int i = 0; i < numberButtons.size(); i++) {
            final int number = i == 9 ? 0 : i + 1; // 0 находится на последней позиции
            numberButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNumberClicked(number);
                }
            });
        }

        // Обработчик для кнопки удаления
        ImageView deleteButton = findViewById(R.id.imageView11);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClicked();
                }
            });
        }
    }

    private void onNumberClicked(int number) {
        if (password.size() < 4) {
            password.add(number);
            updateProgressCircles();
            updateButtonBackground(number, true);

            // Проверка пароля после ввода 4 цифр
            if (password.size() == 4) {
                savePasswordAndNavigate();
            }
        }
    }

    private void onDeleteClicked() {
        if (!password.isEmpty()) {
            int lastNumber = password.remove(password.size() - 1);
            updateProgressCircles();
            updateButtonBackground(lastNumber, false);
        }
    }

    private void updateProgressCircles() {
        // Сброс всех кружков прогресса
        resetProgressCircles();

        // Установка активного состояния для заполненных кружков
        // Используем progress_blue для активного состояния и progress для неактивного
        try {
            switch (password.size()) {
                case 1:
                    progress1.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    break;
                case 2:
                    progress1.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    progress2.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    break;
                case 3:
                    progress1.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    progress2.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    progress3.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    break;
                case 4:
                    progress1.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    progress2.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    progress3.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    progress4.setImageResource(com.example.ui_kit.R.drawable.progress_blue);
                    break;
            }
        } catch (Exception e) {
            // Если drawable не найден, используем альтернативный метод
            updateProgressCirclesWithTint();
        }
    }

    private void updateProgressCirclesWithTint() {
        // Альтернативный метод - изменение цвета через tint
        int activeColor = ContextCompat.getColor(this, com.example.ui_kit.R.color.accent);
        int inactiveColor = ContextCompat.getColor(this, R.color.white);

        // Сброс всех кружков
        progress1.setColorFilter(inactiveColor, android.graphics.PorterDuff.Mode.SRC_IN);
        progress2.setColorFilter(inactiveColor, android.graphics.PorterDuff.Mode.SRC_IN);
        progress3.setColorFilter(inactiveColor, android.graphics.PorterDuff.Mode.SRC_IN);
        progress4.setColorFilter(inactiveColor, android.graphics.PorterDuff.Mode.SRC_IN);

        // Установка активного цвета
        switch (password.size()) {
            case 1:
                progress1.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 2:
                progress1.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                progress2.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 3:
                progress1.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                progress2.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                progress3.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 4:
                progress1.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                progress2.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                progress3.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                progress4.setColorFilter(activeColor, android.graphics.PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    private void resetProgressCircles() {
        // Сброс всех кружков к неактивному состоянию (обычный progress)
        progress1.setImageResource(com.example.ui_kit.R.drawable.progress);
        progress2.setImageResource(com.example.ui_kit.R.drawable.progress);
        progress3.setImageResource(com.example.ui_kit.R.drawable.progress);
        progress4.setImageResource(com.example.ui_kit.R.drawable.progress);

        // Очищаем цветовые фильтры
        progress1.clearColorFilter();
        progress2.clearColorFilter();
        progress3.clearColorFilter();
        progress4.clearColorFilter();
    }

    private void updateButtonBackground(int number, boolean isPressed) {
        final Button button = getButtonByNumber(number);

        if (button != null) {
            if (isPressed) {
                // Меняем цвет кнопки при нажатии
                button.setBackgroundTintList(ContextCompat.getColorStateList(this, com.example.ui_kit.R.color.accent));

                // Возвращаем исходный цвет через 200 мс
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button.setBackgroundTintList(ContextCompat.getColorStateList(creat_password_login.this, com.example.ui_kit.R.color.input_bg));
                    }
                }, 200);
            } else {
                button.setBackgroundTintList(ContextCompat.getColorStateList(this, com.example.ui_kit.R.color.input_bg));
            }
        }
    }

    private Button getButtonByNumber(int number) {
        switch (number) {
            case 0:
                return findViewById(R.id.password0);
            case 1:
                return findViewById(R.id.password1);
            case 2:
                return findViewById(R.id.password2);
            case 3:
                return findViewById(R.id.password3);
            case 4:
                return findViewById(R.id.password4);
            case 5:
                return findViewById(R.id.password5);
            case 6:
                return findViewById(R.id.password6);
            case 7:
                return findViewById(R.id.password7);
            case 8:
                return findViewById(R.id.password8);
            case 9:
                return findViewById(R.id.password9);
            default:
                return null;
        }
    }

    private void savePasswordAndNavigate() {
        // Сохраняем PIN-код
        String pinCode = getPasswordAsString();

        // Сохраняем данные пользователя
        saveUserData(pinCode);

        // Переходим на профиль с передачей только имени и email
        Intent profileIntent = new Intent(creat_password_login.this, profile.class);
        profileIntent.putExtra("USER_NAME", userName);
        profileIntent.putExtra("USER_EMAIL", userEmail);
        startActivity(profileIntent);

        // Завершаем все предыдущие активности
        finishAffinity();
    }

    private void saveUserData(String pinCode) {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Сохраняем только имя и email
        editor.putString("user_name", userName);
        editor.putString("user_email", userEmail);

        // Сохраняем пароли
        editor.putString("user_password", userPasswordFromPrevious);
        editor.putString("user_pin", pinCode);

        // Флаги состояния
        editor.putBoolean("profile_created", true);
        editor.putBoolean("password_set", true);
        editor.putBoolean("pin_set", true);

        editor.apply();

        // Показываем сообщение об успешном сохранении
        Toast.makeText(this, "Данные успешно сохранены!", Toast.LENGTH_SHORT).show();
    }

    private String getPasswordAsString() {
        StringBuilder sb = new StringBuilder();
        for (Integer num : password) {
            sb.append(num);
        }
        return sb.toString();
    }
}