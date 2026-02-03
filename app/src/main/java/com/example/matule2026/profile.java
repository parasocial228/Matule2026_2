package com.example.matule2026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class profile extends AppCompatActivity {

    private TextView textView7; // Для имени
    private TextView textView8; // Для почты
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        prefsHelper = new SharedPreferencesHelper(this);

        // Находим TextView
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);

        // Получаем данные из Intent
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("USER_NAME");
            String userEmail = intent.getStringExtra("USER_EMAIL");

            // Устанавливаем значения
            if (userName != null && !userName.isEmpty()) {
                textView7.setText(userName);
                saveToPreferences("USER_NAME", userName);
            }

            if (userEmail != null && !userEmail.isEmpty()) {
                textView8.setText(userEmail);
                saveToPreferences("USER_EMAIL", userEmail);
                prefsHelper.saveUserEmail(userEmail);
            }
        }

        // Загружаем из SharedPreferences, если Intent пустой
        loadFromPreferences();

        // Навигационные кнопки
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        ImageView buttonGlavn = this.findViewById(com.example.ui_kit.R.id.button_glavn);
        ImageView buttonCatalog = this.findViewById(com.example.ui_kit.R.id.button_catalog);
        ImageView buttonProject = this.findViewById(com.example.ui_kit.R.id.button_project);
        ImageView buttonProfile = this.findViewById(com.example.ui_kit.R.id.button_profile);

        buttonGlavn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!profile.this.getClass().getSimpleName().equals("MainActivity")) {
                    Intent intent = new Intent(profile.this, glavn.class);
                    intent.putExtra("USER_NAME", textView7.getText().toString());
                    intent.putExtra("USER_EMAIL", textView8.getText().toString());
                    startActivity(intent);
                }
            }
        });

        buttonCatalog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!profile.this.getClass().getSimpleName().equals("CatalogActivity")) {
                    Intent intent = new Intent(profile.this, catalog.class);
                    intent.putExtra("USER_NAME", textView7.getText().toString());
                    intent.putExtra("USER_EMAIL", textView8.getText().toString());
                    startActivity(intent);
                }
            }
        });

        buttonProject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!profile.this.getClass().getSimpleName().equals("ProjectActivity")) {
                    Intent intent = new Intent(profile.this, project.class);
                    intent.putExtra("USER_NAME", textView7.getText().toString());
                    intent.putExtra("USER_EMAIL", textView8.getText().toString());
                    startActivity(intent);
                }
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!profile.this.getClass().getSimpleName().equals("ProfileActivity")) {
                    // Обновляем данные в этой же активности
                    Intent intent = new Intent(profile.this, profile.class);
                    intent.putExtra("USER_NAME", textView7.getText().toString());
                    intent.putExtra("USER_EMAIL", textView8.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveToPreferences(String key, String value) {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void loadFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Загружаем только если TextView пустые
        if (textView7.getText().toString().isEmpty()) {
            String savedName = prefs.getString("USER_NAME", "");
            if (!savedName.isEmpty()) {
                textView7.setText(savedName);
            }
        }

        if (textView8.getText().toString().isEmpty()) {
            String savedEmail = prefs.getString("USER_EMAIL", "");
            if (!savedEmail.isEmpty()) {
                textView8.setText(savedEmail);
            }
        }
    }

    public void openRegistration(View view) {
        // Очищаем все данные при выходе
//        prefsHelper.clearAllData();
//
//        // Очищаем SharedPreferences user_data
//        SharedPreferences userPrefs = getSharedPreferences("user_data", MODE_PRIVATE);
//        userPrefs.edit().clear().apply();
//
//        // Очищаем UserProfile
//        SharedPreferences profilePrefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
//        profilePrefs.edit().clear().apply();
        project.clearCurrentUserProjects(this);

        // Очищаем общие настройки
        prefsHelper.clearAllData();

        SharedPreferences userPrefs = getSharedPreferences("user_data", MODE_PRIVATE);
        userPrefs.edit().clear().apply();

        SharedPreferences profilePrefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        profilePrefs.edit().clear().apply();

        // Переходим на регистрацию с очисткой стека активностей
        Intent intent = new Intent(profile.this, registration_login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Сохраняем текущие значения
        saveToPreferences("USER_NAME", textView7.getText().toString());
        saveToPreferences("USER_EMAIL", textView8.getText().toString());
    }
}