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

public class login extends AppCompatActivity {
    private ImageView progress1, progress2, progress3, progress4;
    private List<Button> numberButtons = new ArrayList<>();
    private List<Integer> enteredPin = new ArrayList<>();
    private Handler handler = new Handler();
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsHelper = new SharedPreferencesHelper(this);
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
        if (enteredPin.size() < 4) {
            enteredPin.add(number);
            updateProgressCircles();
            updateButtonBackground(number, true);

            // Проверка пароля после ввода 4 цифр
            if (enteredPin.size() == 4) {
                verifyPinCode();
            }
        }
    }

    private void onDeleteClicked() {
        if (!enteredPin.isEmpty()) {
            int lastNumber = enteredPin.remove(enteredPin.size() - 1);
            updateProgressCircles();
            updateButtonBackground(lastNumber, false);
        }
    }

    private void verifyPinCode() {
        String enteredPinStr = getEnteredPinAsString();
        String savedPin = prefsHelper.getPinCode();

        if (enteredPinStr.equals(savedPin)) {
            // PIN верный - переходим в профиль
            Toast.makeText(this, "Вход выполнен!", Toast.LENGTH_SHORT).show();

            // Загружаем данные пользователя из SharedPreferences
            SharedPreferences userPrefs = getSharedPreferences("user_data", MODE_PRIVATE);
            String userName = userPrefs.getString("user_name", "");
            String userEmail = userPrefs.getString("user_email", "");

            // Сохраняем в SharedPreferencesHelper для быстрого доступа
            if (!userEmail.isEmpty()) {
                prefsHelper.saveUserEmail(userEmail);
            }

            Intent profileIntent = new Intent(login.this, profile.class);
            profileIntent.putExtra("USER_NAME", userName);
            profileIntent.putExtra("USER_EMAIL", userEmail);
            startActivity(profileIntent);
            finish();
        } else {
            // Неверный PIN
            Toast.makeText(this, "Неверный PIN-код!", Toast.LENGTH_SHORT).show();
            resetPinInput();
        }
    }

    private void resetPinInput() {
        enteredPin.clear();
        resetProgressCircles();

        // Небольшая задержка перед сбросом для лучшего UX
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Можно добавить вибрацию или анимацию ошибки
            }
        }, 200);
    }

    private String getEnteredPinAsString() {
        StringBuilder sb = new StringBuilder();
        for (Integer num : enteredPin) {
            sb.append(num);
        }
        return sb.toString();
    }

    private void updateProgressCircles() {
        // Сброс всех кружков прогресса
        resetProgressCircles();

        // Установка активного состояния для заполненных кружков
        try {
            switch (enteredPin.size()) {
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
        switch (enteredPin.size()) {
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
                        button.setBackgroundTintList(ContextCompat.getColorStateList(login.this, com.example.ui_kit.R.color.input_bg));
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

 //   @Override
//    public void onBackPressed() {
//        // Предотвращаем возврат на предыдущую активность
//        // Вместо этого выходим из приложения или остаемся на экране логина
//        moveTaskToBack(true);
//    }
}