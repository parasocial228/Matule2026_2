package com.example.matule2026;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class profile_create extends AppCompatActivity {

    private Button buttonNext;
    private EditText editTextName, editTextSurname, editTextPatronymic, editTextDate, editTextEmail;
    private Spinner spinnerGender;
    private PopupWindow errorPopup;
    private boolean isFormValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSpinner();
        setupTextWatchers();
        setupNameInput();
        setupButtonClickListener();
        updateButtonState();
    }

    private void initViews() {
        // Кнопка
        buttonNext = findViewById(R.id.button2);
        if (buttonNext != null) {
            buttonNext.setText("Далее");
        }

        // Находим поля из include-элементов
        // Имя
        View includeName = findViewById(R.id.name);
        if (includeName != null) {
            editTextName = includeName.findViewById(com.example.ui_kit.R.id.editText);
            if (editTextName != null) {
                editTextName.setHint("Имя");
            }
        }

        // Фамилия
        View includeSurname = findViewById(R.id.surname);
        if (includeSurname != null) {
            editTextSurname = includeSurname.findViewById(com.example.ui_kit.R.id.editText);
            if (editTextSurname != null) {
                editTextSurname.setHint("Фамилия");
            }
        }

        // Отчество
        View includeSecondName = findViewById(R.id.secondName);
        if (includeSecondName != null) {
            editTextPatronymic = includeSecondName.findViewById(com.example.ui_kit.R.id.editText);
            if (editTextPatronymic != null) {
                editTextPatronymic.setHint("Отчество");
            }
        }

        // Email
        View includeEmail = findViewById(R.id.email);
        if (includeEmail != null) {
            editTextEmail = includeEmail.findViewById(com.example.ui_kit.R.id.editText);
            if (editTextEmail != null) {
                editTextEmail.setHint("Почта");
            }
        }

        // Поиск Spinner для выбора пола
        spinnerGender = findSpinnerInIncludes();

        // Поиск EditText для даты
        editTextDate = findDateEditTextInIncludes();
    }

    // Метод для поиска Spinner в include
    private Spinner findSpinnerInIncludes() {
        View rootView = findViewById(android.R.id.content);
        return findSpinnerRecursive(rootView);
    }

    private Spinner findSpinnerRecursive(View view) {
        if (view instanceof Spinner) {
            // Проверяем, что это наш Spinner (может быть несколько Spinner на экране)
            Spinner spinner = (Spinner) view;
            // Можно проверить по родительскому контейнеру или другим признакам
            return spinner;
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                Spinner found = findSpinnerRecursive(child);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    // Метод для поиска EditText для даты в include
    private EditText findDateEditTextInIncludes() {
        View rootView = findViewById(android.R.id.content);
        return findDateEditTextRecursive(rootView);
    }

    private EditText findDateEditTextRecursive(View view) {
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            // Проверяем hint или другие признаки, что это поле для даты
            String hint = editText.getHint() != null ? editText.getHint().toString() : "";
            if (hint.toLowerCase().contains("дата") || hint.toLowerCase().contains("date")) {
                return editText;
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                EditText found = findDateEditTextRecursive(child);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    private void setupNameInput() {
        setupNameEditText(editTextSurname);
        setupNameEditText(editTextName);
        setupNameEditText(editTextPatronymic);

        if (editTextDate != null) {
            editTextDate.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        }
    }

    private void setupNameEditText(EditText editText) {
        if (editText == null) return;

        // Разрешаем ввод букв (русских и английских), пробелов и дефисов
        editText.setInputType(EditorInfo.TYPE_CLASS_TEXT |
                EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS |
                EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && Character.isLowerCase(s.charAt(0))) {
                    editText.removeTextChangedListener(this);
                    String newText = Character.toUpperCase(s.charAt(0)) + s.subSequence(1, s.length()).toString();
                    editText.setText(newText);
                    editText.setSelection(newText.length());
                    editText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSpinner() {
        if (spinnerGender == null) return;

        String[] genders = {"Пол", "Мужской", "Женский"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                genders
        ) {
            @Override
            public boolean isEnabled(int position) {
                // Делаем первый элемент ("Пол") невыбираемым (как подсказка)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    // Подсказка "Пол" серого цвета
                    textView.setTextColor(Color.GRAY);
                } else {
                    // Остальные элементы черного цвета
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    // В основном виде тоже серый цвет для подсказки
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Устанавливаем слушатель выбора
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Обновляем цвет текста выбранного элемента
                if (view != null && view instanceof TextView) {
                    TextView textView = (TextView) view;
                    if (position == 0) {
                        textView.setTextColor(Color.GRAY);
                    } else {
                        textView.setTextColor(Color.BLACK);
                    }
                }
                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateButtonState();
            }
        });
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

        if (editTextName != null) editTextName.addTextChangedListener(textWatcher);
        if (editTextPatronymic != null) editTextPatronymic.addTextChangedListener(textWatcher);
        if (editTextSurname != null) editTextSurname.addTextChangedListener(textWatcher);
        if (editTextEmail != null) editTextEmail.addTextChangedListener(textWatcher);
        // Убрали TextWatcher для editTextDate
    }

    private void setupButtonClickListener() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName != null ? editTextName.getText().toString().trim() : "";
                String patronymic = editTextPatronymic != null ? editTextPatronymic.getText().toString().trim() : "";
                String surname = editTextSurname != null ? editTextSurname.getText().toString().trim() : "";
                String email = editTextEmail != null ? editTextEmail.getText().toString().trim() : "";
                boolean isGenderSelected = spinnerGender != null && spinnerGender.getSelectedItemPosition() > 0;

                boolean allValid = !name.isEmpty() &&
                        !patronymic.isEmpty() &&
                        !surname.isEmpty() &&
                        isValidEmail(email) &&
                        isGenderSelected &&
                        isValidName(name) &&
                        isValidName(surname) &&
                        isValidName(patronymic);

                if (allValid) {
                    // Переход на creat_password с передачей только ИМЕНИ и ПОЧТЫ
                    Intent intent = new Intent(profile_create.this, creat_password.class);
                    intent.putExtra("USER_NAME", name); // Только имя
                    intent.putExtra("USER_EMAIL", email); // Только email

                    startActivity(intent);
                    finish();
                } else {
                    showErrorMessage(name, patronymic, surname, email, isGenderSelected);
                }
            }
        });
    }

    private void updateButtonState() {
        String name = editTextName != null ? editTextName.getText().toString().trim() : "";
        String patronymic = editTextPatronymic != null ? editTextPatronymic.getText().toString().trim() : "";
        String surname = editTextSurname != null ? editTextSurname.getText().toString().trim() : "";
        String email = editTextEmail != null ? editTextEmail.getText().toString().trim() : "";
        boolean isGenderSelected = spinnerGender != null && spinnerGender.getSelectedItemPosition() > 0;

        // Проверяем все условия (без проверки даты)
        boolean allValid = !name.isEmpty() &&
                !patronymic.isEmpty() &&
                !surname.isEmpty() &&
                isValidEmail(email) &&
                isGenderSelected &&
                isValidName(name) &&
                isValidName(surname) &&
                isValidName(patronymic);

        this.isFormValid = allValid;

        if (this.isFormValid) {
            buttonNext.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, com.example.ui_kit.R.color.accent)));
        } else {
            buttonNext.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, com.example.ui_kit.R.color.accent_inactive)));
        }

        buttonNext.setAlpha(this.isFormValid ? 1.0f : 0.7f);
        buttonNext.setEnabled(true);
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            return false;
        }
        String emailPattern = "^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$";

        if (!email.matches(emailPattern)) {
            return false;
        }

        return email.toLowerCase().endsWith(".ru");
    }

    private boolean isValidName(String text) {
        if (text.isEmpty()) {
            return false;
        }
        // Разрешаем русские и английские буквы, пробелы и дефисы
        String namePattern = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$";
        return text.matches(namePattern);
    }

    private void showErrorMessage(String name, String patronymic, String surname,
                                  String email, boolean isGenderSelected) {
        // Сначала скрываем предыдущее сообщение, если оно показывается
        if (errorPopup != null && errorPopup.isShowing()) {
            errorPopup.dismiss();
        }

        // Загружаем layout для сообщения об ошибке
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.message_error, null);

        // Устанавливаем текст ошибки
        TextView errorTextView = popupView.findViewById(R.id.textView22);
        String errorMessage = getErrorMessage(name, patronymic, surname, email, isGenderSelected);
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

    private String getErrorMessage(String name, String patronymic, String surname,
                                   String email, boolean isGenderSelected) {
        StringBuilder error = new StringBuilder();

        if (name.isEmpty()) {
            error.append("Введите имя\n");
        } else if (!isValidName(name)) {
            error.append("Имя должно содержать только буквы, пробелы и дефисы\n");
        }

        if (patronymic.isEmpty()) {
            error.append("Введите отчество\n");
        } else if (!isValidName(patronymic)) {
            error.append("Отчество должно содержать только буквы, пробелы и дефисы\n");
        }

        if (surname.isEmpty()) {
            error.append("Введите фамилию\n");
        } else if (!isValidName(surname)) {
            error.append("Фамилия должна содержать только буквы, пробелы и дефисы\n");
        }

        if (!isGenderSelected) {
            error.append("Выберите пол\n");
        }

        if (email.isEmpty()) {
            error.append("Введите email\n");
        } else if (!isValidEmail(email)) {
            if (!email.contains("@")) {
                error.append("Email должен содержать @\n");
            } else if (!email.toLowerCase().endsWith(".ru")) {
                error.append("Email должен заканчиваться на .ru\n");
            } else if (!email.matches("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")) {
                error.append("Email должен содержать только строчные буквы и цифры\n");
            } else {
                error.append("Некорректный email\n");
            }
        }

        // Убираем последний перенос строки
        if (error.length() > 0) {
            error.deleteCharAt(error.length() - 1);
        }

        return error.toString().isEmpty() ? "Ошибка при заполнении формы" : error.toString();
    }

    private String getSelectedGender() {
        if (spinnerGender != null && spinnerGender.getSelectedItemPosition() > 0) {
            return spinnerGender.getSelectedItem().toString();
        }
        return "";
    }

    private ProfileData getProfileData() {
        return new ProfileData(
                editTextSurname != null ? editTextSurname.getText().toString().trim() : "",
                editTextName != null ? editTextName.getText().toString().trim() : "",
                editTextPatronymic != null ? editTextPatronymic.getText().toString().trim() : "",
                getSelectedGender(),
                editTextDate != null ? editTextDate.getText().toString().trim() : "",
                editTextEmail != null ? editTextEmail.getText().toString().trim() : ""
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (errorPopup != null && errorPopup.isShowing()) {
            errorPopup.dismiss();
        }
    }

    public static class ProfileData {
        private String surname;
        private String name;
        private String patronymic;
        private String gender;
        private String birthDate;
        private String email;

        public ProfileData(String surname, String name, String patronymic, String gender, String birthDate, String email) {
            this.surname = surname;
            this.name = name;
            this.patronymic = patronymic;
            this.gender = gender;
            this.birthDate = birthDate;
            this.email = email;
        }

        public String getSurname() { return surname; }
        public String getName() { return name; }
        public String getPatronymic() { return patronymic; }
        public String getGender() { return gender; }
        public String getBirthDate() { return birthDate; }
        public String getEmail() { return email; }
    }
}