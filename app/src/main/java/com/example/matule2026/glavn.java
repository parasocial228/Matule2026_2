package com.example.matule2026;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class glavn extends AppCompatActivity {

    // Переменные для кнопок "Добавить/Убрать"
    private Button buttonAddRemove1;
    private Button buttonAddRemove2;

    // Состояния кнопок
    private boolean isItem1Added = false;
    private boolean isItem2Added = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_glavn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCategoryRadioGroup();
        setupAddButtons();
        setupBottomNavigation();
    }

    private void setupCategoryRadioGroup() {
        RadioGroup radioGroup = findViewById(com.example.ui_kit.R.id.categoryRadioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);

                if (radioButton != null) {
                    String category = radioButton.getText().toString();
                    // Логика при выборе категории
                    // Можете добавить фильтрацию товаров здесь
                }
            }
        });

        // Устанавливаем первую кнопку выбранной по умолчанию
        RadioButton firstButton = findViewById(com.example.ui_kit.R.id.vse);
        if (firstButton != null) {
            firstButton.setChecked(true);
        }
    }

    private void setupAddButtons() {
        // Находим primary layouts по их ID
        View cart1Include = findViewById(R.id.cart1);
        View cart2Include = findViewById(R.id.cart2);

        // Находим кнопки внутри include элементов
        if (cart1Include != null) {
            buttonAddRemove1 = cart1Include.findViewById(com.example.ui_kit.R.id.buttoncart);

            // Устанавливаем текст для cart1 (если нужно)
            TextView nameProduct1 = cart1Include.findViewById(com.example.ui_kit.R.id.nameProduct);
            if (nameProduct1 != null) {
                nameProduct1.setText("Рубашка Воскресенье для машинного вязания");
            }
        }

        if (cart2Include != null) {
            buttonAddRemove2 = cart2Include.findViewById(com.example.ui_kit.R.id.buttoncart);

            // Устанавливаем текст для cart2 на "Шорты вторник для машинного вязания"
            TextView nameProduct2 = cart2Include.findViewById(com.example.ui_kit.R.id.nameProduct);
            if (nameProduct2 != null) {
                nameProduct2.setText("Шорты вторник для машинного вязания");
            }
        }

        // Настраиваем обработчики кликов для кнопок
        setupAddRemoveButtonListeners();
    }

    private void setupAddRemoveButtonListeners() {
        // Обработчик для первой кнопки
        if (buttonAddRemove1 != null) {
            // Устанавливаем начальное состояние
            updateButtonState(buttonAddRemove1, isItem1Added);

            buttonAddRemove1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isItem1Added = !isItem1Added;
                    updateButtonState(buttonAddRemove1, isItem1Added);
                }
            });
        }

        // Обработчик для второй кнопки
        if (buttonAddRemove2 != null) {
            // Устанавливаем начальное состояние
            updateButtonState(buttonAddRemove2, isItem2Added);

            buttonAddRemove2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isItem2Added = !isItem2Added;
                    updateButtonState(buttonAddRemove2, isItem2Added);
                }
            });
        }
    }

    private void updateButtonState(Button button, boolean isSelected) {
        if (isSelected) {
            // Состояние "Убрать": белый фон с синей границей, синий текст
            button.setBackgroundResource(com.example.ui_kit.R.drawable.blue_border);
            button.setText("Убрать");
            button.setTextColor(ContextCompat.getColor(this, com.example.ui_kit.R.color.accent));
        } else {
            // Состояние "Добавить": синий фон, белый текст
            button.setBackgroundResource(com.example.ui_kit.R.drawable.blue_background);
            button.setText("Добавить");
            button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }

    private void setupBottomNavigation() {
        ImageView buttonGlavn = this.findViewById(com.example.ui_kit.R.id.button_glavn);
        ImageView buttonCatalog = this.findViewById(com.example.ui_kit.R.id.button_catalog);
        ImageView buttonProject = this.findViewById(com.example.ui_kit.R.id.button_project);
        ImageView buttonProfile = this.findViewById(com.example.ui_kit.R.id.button_profile);

        buttonGlavn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!glavn.this.getClass().getSimpleName().equals("MainActivity")) {
                    Intent intent = new Intent(glavn.this, glavn.class);
                    startActivity(intent);
                }
            }
        });

        buttonCatalog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!glavn.this.getClass().getSimpleName().equals("CatalogActivity")) {
                    Intent intent = new Intent(glavn.this, catalog.class);
                    startActivity(intent);
                }
            }
        });

        buttonProject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!glavn.this.getClass().getSimpleName().equals("ProjectActivity")) {
                    Intent intent = new Intent(glavn.this, project.class);
                    startActivity(intent);
                }
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!glavn.this.getClass().getSimpleName().equals("ProfileActivity")) {
                    Intent intent = new Intent(glavn.this, profile.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Сохраняем состояния кнопок при повороте экрана
        outState.putBoolean("isItem1Added", isItem1Added);
        outState.putBoolean("isItem2Added", isItem2Added);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Восстанавливаем состояния кнопок после поворота экрана
        if (savedInstanceState != null) {
            isItem1Added = savedInstanceState.getBoolean("isItem1Added", false);
            isItem2Added = savedInstanceState.getBoolean("isItem2Added", false);

            // Обновляем визуальное состояние кнопок
            if (buttonAddRemove1 != null) {
                updateButtonState(buttonAddRemove1, isItem1Added);
            }
            if (buttonAddRemove2 != null) {
                updateButtonState(buttonAddRemove2, isItem2Added);
            }
        }
    }
}