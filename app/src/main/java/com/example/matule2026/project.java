package com.example.matule2026;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class project extends AppCompatActivity {
    private LinearLayout projectsContainer;
    private ImageView addProjectButton;
    private SharedPreferencesHelper prefsHelper;

    // Ключи для SharedPreferences
    private static final String PROJECT_COUNT_KEY = "project_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация хелпера
        prefsHelper = new SharedPreferencesHelper(this);

        // Настройка таббара
        setupTabbar();

        initializeViews();

        // Настраиваем статическую карточку
        setupStaticCard();

        // Загружаем сохраненные проекты для текущего пользователя
        loadSavedProjects();

        // Проверяем, есть ли новый проект из creat_project
        checkForNewProjectFromIntent();
    }

    private void setupTabbar() {
        ImageView buttonGlavn = findViewById(com.example.ui_kit.R.id.button_glavn);
        ImageView buttonCatalog = findViewById(com.example.ui_kit.R.id.button_catalog);
        ImageView buttonProject = findViewById(com.example.ui_kit.R.id.button_project);
        ImageView buttonProfile = findViewById(com.example.ui_kit.R.id.button_profile);

        buttonGlavn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!project.this.getClass().getSimpleName().equals("MainActivity")) {
                    Intent intent = new Intent(project.this, glavn.class);
                    project.this.startActivity(intent);
                }
            }
        });

        buttonCatalog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!project.this.getClass().getSimpleName().equals("CatalogActivity")) {
                    Intent intent = new Intent(project.this, catalog.class);
                    project.this.startActivity(intent);
                }
            }
        });

        buttonProject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!project.this.getClass().getSimpleName().equals("ProjectActivity")) {
                    Intent intent = new Intent(project.this, project.class);
                    project.this.startActivity(intent);
                }
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!project.this.getClass().getSimpleName().equals("ProfileActivity")) {
                    Intent intent = new Intent(project.this, profile.class);
                    project.this.startActivity(intent);
                }
            }
        });
    }

    private void initializeViews() {
        // Находим контейнер для проектов
        projectsContainer = findViewById(R.id.projects_container);

        // Находим кнопку добавления проекта
        addProjectButton = findViewById(R.id.imageView13);
        if (addProjectButton != null) {
            addProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCreatProject();
                }
            });
        }
    }

    private void setupStaticCard() {
        // Находим статическую карточку
        View staticCard = findViewById(R.id.static_project_card);
        if (staticCard != null) {
            // Находим элементы внутри include
            TextView staticTitle = staticCard.findViewById(com.example.ui_kit.R.id.project_title);
            TextView staticTime = staticCard.findViewById(com.example.ui_kit.R.id.project_time);

            // Устанавливаем текст для статической карточки
            staticTitle.setText("Мой первый проект");
            staticTime.setText("Прошло 2 дня");

            final String staticProjectName = "Мой первый проект";

            // Добавляем обработчик нажатия на кнопку "Открыть"
            staticCard.findViewById(com.example.ui_kit.R.id.open_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProjectDetails(staticProjectName);
                }
            });

            // Добавляем обработчик нажатия на всю карточку
            staticCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProjectDetails(staticProjectName);
                }
            });
        }
    }

    private void checkForNewProjectFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("PROJECT_NAME")) {
            String projectName = intent.getStringExtra("PROJECT_NAME");
            if (projectName != null && !projectName.isEmpty()) {
                // Сохраняем новый проект для текущего пользователя
                saveNewProject(projectName);

                // Добавляем карточку
                addProjectCard(projectName, "Только что");

                // Показываем сообщение
                Toast.makeText(this, "Проект '" + projectName + "' создан!", Toast.LENGTH_SHORT).show();

                // Очищаем данные из интента
                intent.removeExtra("PROJECT_NAME");
            }
        }
    }

    private String getCurrentUserPrefsName() {
        // Получаем email текущего пользователя
        String email = prefsHelper.getUserEmail();

        // Создаем уникальное имя файла для проектов этого пользователя
        if (!email.isEmpty()) {
            return "projects_" + email.hashCode(); // Пример: projects_-123456789
        }

        // Для гостей или если email не установлен
        return "projects_guest";
    }

    private void saveNewProject(String projectName) {
        // Используем уникальное имя файла для текущего пользователя
        String prefsName = getCurrentUserPrefsName();
        SharedPreferences prefs = getSharedPreferences(prefsName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Получаем текущее количество проектов
        int projectCount = prefs.getInt(PROJECT_COUNT_KEY, 0);

        // Увеличиваем счетчик
        projectCount++;

        // Сохраняем проект
        String currentTime = getCurrentTime();
        String projectKey = "project_" + projectCount;
        String projectData = projectName + "|" + currentTime;

        editor.putString(projectKey, projectData);
        editor.putInt(PROJECT_COUNT_KEY, projectCount);
        editor.apply();
    }

    private void loadSavedProjects() {
        // Используем уникальное имя файла для текущего пользователя
        String prefsName = getCurrentUserPrefsName();
        SharedPreferences prefs = getSharedPreferences(prefsName, MODE_PRIVATE);

        // Получаем количество проектов
        int projectCount = prefs.getInt(PROJECT_COUNT_KEY, 0);

        // Загружаем все проекты пользователя
        for (int i = 1; i <= projectCount; i++) {
            String projectKey = "project_" + i;
            String projectData = prefs.getString(projectKey, null);

            if (projectData != null) {
                String[] parts = projectData.split("\\|");
                if (parts.length >= 2) {
                    String projectName = parts[0];
                    String projectTime = parts[1];

                    // Пропускаем статический проект "Мой первый проект" (он уже есть в layout)
                    if (!projectName.equals("Мой первый проект")) {
                        addProjectCard(projectName, projectTime);
                    }
                }
            }
        }
    }

    private void addProjectCard(String projectName, String timeText) {
        if (projectsContainer == null) return;

        if (projectName == null || projectName.isEmpty()) {
            projectName = "Без названия";
        }

        // Создаем final переменную для использования внутри анонимного класса
        final String finalProjectName = projectName;

        // Инфлейтим макет карточки (используем существующий project.xml)
        LayoutInflater inflater = LayoutInflater.from(this);
        View projectCard = inflater.inflate(com.example.ui_kit.R.layout.project, null);

        // Находим элементы карточки
        TextView titleTextView = projectCard.findViewById(com.example.ui_kit.R.id.project_title);
        TextView timeTextView = projectCard.findViewById(com.example.ui_kit.R.id.project_time);

        // Устанавливаем данные
        titleTextView.setText(projectName);
        timeTextView.setText(timeText);

        // Добавляем обработчик нажатия на кнопку "Открыть"
        projectCard.findViewById(com.example.ui_kit.R.id.open_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProjectDetails(finalProjectName);
            }
        });

        // Добавляем обработчик нажатия на всю карточку
        projectCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProjectDetails(finalProjectName);
            }
        });

        // Добавляем карточку ПОСЛЕ статической карточки (на позицию 1)
        // Позиция 0 - это статическая карточка
        if (projectsContainer.getChildCount() > 0) {
            projectsContainer.addView(projectCard, 1);
        } else {
            projectsContainer.addView(projectCard);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void openProjectDetails(String projectName) {
        // Здесь можно открыть детали проекта
        Toast.makeText(this, "Открыт проект: " + projectName, Toast.LENGTH_SHORT).show();
    }

    private void openCreatProject() {
        Intent intent = new Intent(this, creat_project.class);
        startActivity(intent);
    }

    public void openCreatProject(View view) {
        Intent intent = new Intent(this, creat_project.class);
        this.startActivity(intent);
    }

    // Статический метод для удаления проектов текущего пользователя
    public static void clearCurrentUserProjects(Context context) {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(context);
        String email = helper.getUserEmail();

        // Определяем имя файла для текущего пользователя
        String prefsName;
        if (!email.isEmpty()) {
            prefsName = "projects_" + email.hashCode();
        } else {
            prefsName = "projects_guest";
        }

        // Удаляем все проекты этого пользователя
        SharedPreferences prefs = context.getSharedPreferences(prefsName, MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkForNewProjectFromIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForNewProjectFromIntent();
    }
}