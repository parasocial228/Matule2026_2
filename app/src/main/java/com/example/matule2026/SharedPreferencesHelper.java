package com.example.matule2026;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_USER_REGISTERED = "user_registered";
    private static final String KEY_SKIPPED_REGISTRATION = "skipped_registration";
    private static final String KEY_PIN_CODE = "pin_code";
    private static final String KEY_FULL_REGISTRATION_COMPLETE = "full_registration_complete";
    private static final String KEY_HAS_PROFILE = "has_profile";
    private static final String KEY_USER_EMAIL = "user_email";

    private SharedPreferences prefs;

    public SharedPreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Проверяем, первый ли запуск
    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    // Устанавливаем, что первый запуск был
    public void setFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    // Проверяем, зарегистрирован ли пользователь
    public boolean isUserRegistered() {
        return prefs.getBoolean(KEY_USER_REGISTERED, false);
    }

    // Устанавливаем, что пользователь зарегистрирован
    public void setUserRegistered(boolean registered) {
        prefs.edit().putBoolean(KEY_USER_REGISTERED, registered).apply();
    }

    // Пропустил ли пользователь регистрацию
    public boolean isRegistrationSkipped() {
        return prefs.getBoolean(KEY_SKIPPED_REGISTRATION, false);
    }

    // Устанавливаем флаг пропуска регистрации
    public void setRegistrationSkipped(boolean skipped) {
        prefs.edit().putBoolean(KEY_SKIPPED_REGISTRATION, skipped).apply();
    }

    // Полная регистрация завершена
    public boolean isFullRegistrationComplete() {
        return prefs.getBoolean(KEY_FULL_REGISTRATION_COMPLETE, false);
    }

    // Устанавливаем флаг завершения полной регистрации
    public void setFullRegistrationComplete(boolean complete) {
        prefs.edit().putBoolean(KEY_FULL_REGISTRATION_COMPLETE, complete).apply();
    }

    // Есть ли профиль
    public boolean hasProfile() {
        return prefs.getBoolean(KEY_HAS_PROFILE, false);
    }

    // Устанавливаем флаг наличия профиля
    public void setHasProfile(boolean hasProfile) {
        prefs.edit().putBoolean(KEY_HAS_PROFILE, hasProfile).apply();
    }

    // Сохраняем PIN-код
    public void savePinCode(String pinCode) {
        prefs.edit().putString(KEY_PIN_CODE, pinCode).apply();
    }

    // Получаем сохраненный PIN-код
    public String getPinCode() {
        return prefs.getString(KEY_PIN_CODE, "");
    }

    // Проверяем, установлен ли PIN-код
    public boolean isPinCodeSet() {
        return !getPinCode().isEmpty();
    }

    // Сохраняем email пользователя
    public void saveUserEmail(String email) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    // Получаем email пользователя
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    // Очищаем все данные (для тестирования)
    public void clearAllData() {
        prefs.edit().clear().apply();
    }
}