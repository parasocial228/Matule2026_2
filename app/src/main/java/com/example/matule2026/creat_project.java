package com.example.matule2026;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class creat_project extends AppCompatActivity {
    // Спиннеры
    private Spinner spinnerType, spinnerToWhom, spinnerCategories;

    // Поля ввода
    private EditText editTextName, editTextDateStart, editTextDateEnd, editTextSource;

    // Кнопки и изображение
    private ImageView fotoImageView;
    private Button buttonConfirm;

    // Изображение
    private Uri selectedImageUri;

    // Лаунчеры для получения результата
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    // Popup для ошибок
    private PopupWindow errorPopup;

    // Флаг для предотвращения рекурсии в DateMaskTextWatcher
    private boolean isFormatting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creat_project);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView buttonGlavn = this.findViewById(com.example.ui_kit.R.id.button_glavn);
        ImageView buttonCatalog = this.findViewById(com.example.ui_kit.R.id.button_catalog);
        ImageView buttonProject = this.findViewById(com.example.ui_kit.R.id.button_project);
        ImageView buttonProfile = this.findViewById(com.example.ui_kit.R.id.button_profile);
        buttonGlavn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!creat_project.this.getClass().getSimpleName().equals("MainActivity")) {
                    Intent intent = new Intent(creat_project.this, glavn.class);
                    startActivity(intent);
                }

            }
        });
        buttonCatalog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!creat_project.this.getClass().getSimpleName().equals("CatalogActivity")) {
                    Intent intent = new Intent(creat_project.this, catalog.class);
                    startActivity(intent);
                }

            }
        });
        buttonProject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!creat_project.this.getClass().getSimpleName().equals("ProjectActivity")) {
                    Intent intent = new Intent(creat_project.this, project.class);
                    startActivity(intent);
                }

            }
        });
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!creat_project.this.getClass().getSimpleName().equals("ProfileActivity")) {
                    Intent intent = new Intent(creat_project.this, profile.class);
                    startActivity(intent);
                }

            }
        });

        // Инициализация элементов
        initializeViews();

        // Настройка спиннеров
        setupSpinners();

        // Настройка лаунчеров
        setupPermissionLauncher();
        setupImagePickerLauncher();

        // Настройка обработчиков
        setupListeners();

        // Настройка кнопки подтверждения
        setupConfirmButton();

        // Настройка таббара
//        setupTabBar();

        // Изначально кнопка неактивна
        updateButtonState();
    }

    private void initializeViews() {
        // Спиннеры из include
        View typeSelectInclude = findViewById(R.id.typeSelectInclude);
        View whomSelectInclude = findViewById(R.id.whomSelectInclude);
        View categorySelectInclude = findViewById(R.id.categorySelectInclude);

        // Находим спиннеры в include
        spinnerType = findSpinnerInView(typeSelectInclude);
        spinnerToWhom = findSpinnerInView(whomSelectInclude);
        spinnerCategories = findSpinnerInView(categorySelectInclude);

        // Поля ввода из include
        View nameInputInclude = findViewById(R.id.nameInputInclude);
        View sourceInputInclude = findViewById(R.id.sourceInputInclude);
        View dateStartInclude = findViewById(R.id.dateStartInclude);
        View dateEndInclude = findViewById(R.id.dateEndInclude);

        editTextName = findEditTextInView(nameInputInclude);
        editTextSource = findEditTextInView(sourceInputInclude);
        editTextDateStart = findEditTextInView(dateStartInclude);
        editTextDateEnd = findEditTextInView(dateEndInclude);

        // Кнопка и изображение
        fotoImageView = findViewById(R.id.foto);
        buttonConfirm = findViewById(R.id.Podtverd);
    }

    private Spinner findSpinnerInView(View includeView) {
        if (includeView != null) {
            return includeView.findViewById(com.example.ui_kit.R.id.spinner);
        }
        return null;
    }

    private EditText findEditTextInView(View includeView) {
        if (includeView != null) {
            return includeView.findViewById(com.example.ui_kit.R.id.editText);
        }
        return null;
    }

    private void setupSpinners() {
        // Настройка спиннера "Тип"
        if (spinnerType != null) {
            setupSpinnerWithHint(spinnerType, "Выберите тип", new String[]{"тип 1", "тип 2", "тип 3"});
        }

        // Настройка спиннера "Кому"
        if (spinnerToWhom != null) {
            setupSpinnerWithHint(spinnerToWhom, "Кому", new String[]{"Себе", "Другому"});
        }

        // Настройка спиннера "Категория"
        if (spinnerCategories != null) {
            setupSpinnerWithHint(spinnerCategories, "Категория", new String[]{"Женщинам", "Мужчинам"});
        }
    }

    private void setupSpinnerWithHint(Spinner spinner, String hint, String[] items) {
        String[] allItems = new String[items.length + 1];
        allItems[0] = hint;
        System.arraycopy(items, 0, allItems, 1, items.length);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, allItems) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    textView.setEnabled(false);
                    textView.setClickable(false);
                } else {
                    textView.setTextColor(Color.BLACK);
                }

                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                int selectedPosition = spinner.getSelectedItemPosition();
                if (selectedPosition == 0) {
                    textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    textView.setTextColor(Color.BLACK);
                }

                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean allGranted = true;
                    for (Boolean isGranted : permissions.values()) {
                        if (!isGranted) {
                            allGranted = false;
                            break;
                        }
                    }

                    if (allGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this,
                                "Для выбора изображения необходимо разрешение на доступ к галерее",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), selectedImageUri);
                            fotoImageView.setImageBitmap(bitmap);
                            updateButtonState(); // Обновляем состояние кнопки после выбора изображения
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this,
                                    "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupListeners() {
        // Обработчик клика на изображение
        if (fotoImageView != null) {
            fotoImageView.setOnClickListener(v -> {
                checkPermissionAndPickImage();
            });
        }

        // Обработчики изменения текста
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

        if (editTextName != null) editTextName.addTextChangedListener(textWatcher);
        if (editTextSource != null) editTextSource.addTextChangedListener(textWatcher);

        // Даты могут быть пустыми, но слушаем изменения
        if (editTextDateStart != null) editTextDateStart.addTextChangedListener(textWatcher);
        if (editTextDateEnd != null) editTextDateEnd.addTextChangedListener(textWatcher);

        // Обработчики спиннеров
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateButtonState();
            }
        };

        if (spinnerType != null) spinnerType.setOnItemSelectedListener(spinnerListener);
        if (spinnerToWhom != null) spinnerToWhom.setOnItemSelectedListener(spinnerListener);
        if (spinnerCategories != null) spinnerCategories.setOnItemSelectedListener(spinnerListener);
    }

    private String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        } else {
            return new String[0];
        }
    }

    private boolean hasRequiredPermissions() {
        String[] permissions = getRequiredPermissions();
        if (permissions.length == 0) {
            return true;
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void checkPermissionAndPickImage() {
        String[] requiredPermissions = getRequiredPermissions();

        if (requiredPermissions.length == 0) {
            openGallery();
            return;
        }

        if (hasRequiredPermissions()) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(requiredPermissions);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Выберите изображение");

        if (intent.resolveActivity(getPackageManager()) != null) {
            imagePickerLauncher.launch(chooser);
        } else {
            Toast.makeText(this,
                    "На устройстве не найдено приложение для выбора изображений",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setupConfirmButton() {
        if (buttonConfirm != null) {
            buttonConfirm.setOnClickListener(v -> {
                if (!areAllFieldsFilled()) {
                    showErrorMessage();
                } else {
                    saveProjectData();
                }
            });

            // Изначально делаем кнопку неактивной
            buttonConfirm.setAlpha(0.7f);
        }
    }

    private boolean areAllFieldsFilled() {
        boolean typeSelected = spinnerType != null && isSpinnerValueSelected(spinnerType);
        boolean toWhomSelected = spinnerToWhom != null && isSpinnerValueSelected(spinnerToWhom);
        boolean categorySelected = spinnerCategories != null && isSpinnerValueSelected(spinnerCategories);
        boolean nameFilled = editTextName != null && !editTextName.getText().toString().trim().isEmpty();
        boolean sourceFilled = editTextSource != null && !editTextSource.getText().toString().trim().isEmpty();
        boolean imageSelected = selectedImageUri != null;

        // Даты начала и окончания не обязательны
        boolean dateStartFilled = true;
        boolean dateEndFilled = true;

        return typeSelected && toWhomSelected && categorySelected &&
                nameFilled && dateStartFilled && dateEndFilled &&
                sourceFilled && imageSelected;
    }

    private void updateButtonState() {
        if (buttonConfirm == null) return;

        boolean allFilled = areAllFieldsFilled();

        if (allFilled) {
            // Делаем кнопку активной
            buttonConfirm.setAlpha(1.0f);
        } else {
            // Делаем кнопку неактивной
            buttonConfirm.setAlpha(0.7f);
        }

        // Кнопка всегда кликабельна
        buttonConfirm.setEnabled(true);
    }

    private void showErrorMessage() {
        if (errorPopup != null && errorPopup.isShowing()) {
            errorPopup.dismiss();
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.message_error, null);

        TextView errorTextView = popupView.findViewById(R.id.textView22);
        String errorMessage = getErrorMessage();
        errorTextView.setText(errorMessage);

        errorPopup = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        errorPopup.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        errorPopup.setOutsideTouchable(true);
        errorPopup.setTouchable(true);

        ImageView dismissButton = popupView.findViewById(R.id.imageView);
        if (dismissButton != null) {
            dismissButton.setOnClickListener(v -> {
                if (errorPopup != null && errorPopup.isShowing()) {
                    errorPopup.dismiss();
                }
            });
        }

        View mainView = findViewById(R.id.main);
        errorPopup.showAtLocation(
                mainView,
                android.view.Gravity.CENTER,
                0,
                0
        );

        // Автоматически скрываем через 5 секунд
        mainView.postDelayed(() -> {
            if (errorPopup != null && errorPopup.isShowing()) {
                errorPopup.dismiss();
            }
        }, 5000);
    }

    private String getErrorMessage() {
        StringBuilder error = new StringBuilder();

        if (spinnerType == null || !isSpinnerValueSelected(spinnerType)) {
            error.append("Выберите тип проекта\n");
        }

        if (editTextName == null || editTextName.getText().toString().trim().isEmpty()) {
            error.append("Введите название проекта\n");
        }

        if (spinnerToWhom == null || !isSpinnerValueSelected(spinnerToWhom)) {
            error.append("Выберите, кому предназначен проект\n");
        }

        if (editTextSource == null || editTextSource.getText().toString().trim().isEmpty()) {
            error.append("Введите источник описания\n");
        }

        if (spinnerCategories == null || !isSpinnerValueSelected(spinnerCategories)) {
            error.append("Выберите категорию\n");
        }

        if (selectedImageUri == null) {
            error.append("Выберите изображение для проекта\n");
        }

        // Убираем последний перенос строки
        if (error.length() > 0) {
            error.deleteCharAt(error.length() - 1);
        }

        return error.toString().isEmpty() ? "Заполните все обязательные поля" : error.toString();
    }

    private void saveProjectData() {
        String projectType = getSelectedSpinnerValue(spinnerType);
        String projectName = editTextName != null ? editTextName.getText().toString().trim() : "";
        String dateStart = editTextDateStart != null ? editTextDateStart.getText().toString().trim() : "";
        String dateEnd = editTextDateEnd != null ? editTextDateEnd.getText().toString().trim() : "";
        String toWhom = getSelectedSpinnerValue(spinnerToWhom);
        String source = editTextSource != null ? editTextSource.getText().toString().trim() : "";
        String category = getSelectedSpinnerValue(spinnerCategories);

        // Проверяем, не пустое ли название
        if (projectName.isEmpty()) {
            projectName = "Без названия";
        }

        Toast.makeText(this, "Проект успешно создан!", Toast.LENGTH_SHORT).show();

        // Переход на экран project с передачей данных
        Intent intent = new Intent(this, project.class);
        intent.putExtra("PROJECT_NAME", projectName);
        intent.putExtra("PROJECT_TYPE", projectType);
        intent.putExtra("PROJECT_DATE_START", dateStart);
        intent.putExtra("PROJECT_DATE_FINISH", dateEnd);
        intent.putExtra("PROJECT_TO_WHOM", toWhom);
        intent.putExtra("PROJECT_SOURCE", source);
        intent.putExtra("PROJECT_CATEGORY", category);

        // Используем флаги для правильной навигации
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private String getSelectedSpinnerValue(Spinner spinner) {
        if (spinner == null) return "";

        int position = spinner.getSelectedItemPosition();
        if (position > 0) {
            return (String) spinner.getItemAtPosition(position);
        }
        return "";
    }

    private boolean isSpinnerValueSelected(Spinner spinner) {
        return spinner != null && spinner.getSelectedItemPosition() > 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (errorPopup != null && errorPopup.isShowing()) {
            errorPopup.dismiss();
        }
    }
}