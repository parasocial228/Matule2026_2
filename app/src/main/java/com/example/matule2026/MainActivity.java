package com.example.matule2026;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private SharedPreferencesHelper prefsHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        prefsHelper = new SharedPreferencesHelper(this);
//        (new Handler()).postDelayed(new Runnable() {
//            public void run() {
//                Intent intent = new Intent(MainActivity.this, registration_login.class);
//                MainActivity.this.startActivity(intent);
//                MainActivity.this.finish();
//            }
//        }, 3000L);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                checkAppStateAndNavigate();
            }
        }, 3000L);
    }

    private void checkAppStateAndNavigate() {
        if (prefsHelper.isFirstLaunch()) {
            // Первый запуск - всегда показываем registration_login
            Intent intent = new Intent(MainActivity.this, registration_login.class);
            startActivity(intent);
        } else {
            // Не первый запуск
            if (prefsHelper.isUserRegistered()) {
                if (prefsHelper.isFullRegistrationComplete() && prefsHelper.isPinCodeSet()) {
                    // Полная регистрация с PIN - показываем логин
                    Intent intent = new Intent(MainActivity.this, login.class);
                    startActivity(intent);
                } else if (prefsHelper.isRegistrationSkipped()) {
                    // Пропущена регистрация - сразу в профиль
                    Intent intent = new Intent(MainActivity.this, profile.class);
                    startActivity(intent);
                } else {
                    // Обычная регистрация (email/password) - сразу в профиль
                    Intent intent = new Intent(MainActivity.this, profile.class);
                    // Передаем email если есть
                    String email = prefsHelper.getUserEmail();
                    if (!email.isEmpty()) {
                        intent.putExtra("email", email);
                    }
                    startActivity(intent);
                }
            } else {
                // Непонятное состояние - показываем регистрацию
                Intent intent = new Intent(MainActivity.this, registration_login.class);
                startActivity(intent);
            }
        }
        finish();
    }
}