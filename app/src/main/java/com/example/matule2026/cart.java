package com.example.matule2026;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class cart extends AppCompatActivity {
    // Элементы первой карточки
    private TextView itemPriceTextView1;
    private TextView quantityTextView1;
    private ImageView deleteButton1;
    private ImageView minusButton1;
    private ImageView plusButton1;
    private View cartItemView1;

    // Элементы второй карточки
    private TextView itemPriceTextView2;
    private TextView quantityTextView2;
    private ImageView deleteButton2;
    private ImageView minusButton2;
    private ImageView plusButton2;
    private View cartItemView2;

    // Общие элементы
    private TextView totalAmountTextView;
    private Button checkoutButton;
    private LinearLayout totalLayout;

    // Количество товаров
    private int itemCount1 = 1;
    private int itemCount2 = 1;
    private final int ITEM_PRICE1 = 300;
    private final int ITEM_PRICE2 = 300;

    // Флаги для отображения товаров
    private boolean showItem1 = false;
    private boolean showItem2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получаем данные из Intent
        Intent intent = getIntent();
        if (intent != null) {
            // Получаем информацию о том, какие именно товары добавлены
            boolean isItem1Added = intent.getBooleanExtra("is_item1_added", false);
            boolean isItem2Added = intent.getBooleanExtra("is_item2_added", false);

            // Устанавливаем видимость товаров
            if (isItem1Added) {
                showItem1 = true;
                itemCount1 = 1;
            }
            if (isItem2Added) {
                showItem2 = true;
                itemCount2 = 1;
            }

            // Также проверяем общее количество на всякий случай
            int totalItemCount = intent.getIntExtra("item_count", 0);
            if (totalItemCount > 0 && !isItem1Added && !isItem2Added) {
                if (totalItemCount >= 1) {
                    showItem1 = true;
                    itemCount1 = 1;
                }
                if (totalItemCount >= 2) {
                    showItem2 = true;
                    itemCount2 = 1;
                }
            }
        }

        // Инициализация элементов
        initializeViews();

        // Настройка кнопки "назад" в header
        setupBackButton();

        // Настройка кнопки оформления заказа
        setupCheckoutButton();

        // Устанавливаем начальные значения и видимость
        setupInitialVisibility();
        updateAllQuantityTexts();
        updateItemPrices();
        updateTotalAmount();

        // Устанавливаем обработчики нажатий
        setupClickListeners();
    }

    private void initializeViews() {
        // Получаем View для карточек
        cartItemView1 = findViewById(R.id.cartItem1);
        cartItemView2 = findViewById(R.id.cartItem2);

        // Инициализация элементов ПЕРВОЙ карточки
        if (cartItemView1 != null) {
            // Находим основные элементы
            itemPriceTextView1 = cartItemView1.findViewById(com.example.ui_kit.R.id.price);
            quantityTextView1 = cartItemView1.findViewById(com.example.ui_kit.R.id.counter_sht);
            deleteButton1 = cartItemView1.findViewById(R.id.imageView7);

            // Находим кнопки счетчика
            minusButton1 = cartItemView1.findViewById(com.example.ui_kit.R.id.minusButton);
            plusButton1 = cartItemView1.findViewById(com.example.ui_kit.R.id.plusButton);

            // Если не нашли напрямую, ищем внутри ConstraintLayout
            if (minusButton1 == null || plusButton1 == null) {
                View counterLayout = cartItemView1.findViewById(R.id.main); // ID из counter.xml
                if (counterLayout != null) {
                    minusButton1 = counterLayout.findViewById(com.example.ui_kit.R.id.minusButton);
                    plusButton1 = counterLayout.findViewById(com.example.ui_kit.R.id.plusButton);
                }
            }
        }

        // Инициализация элементов ВТОРОЙ карточки
        if (cartItemView2 != null) {
            itemPriceTextView2 = cartItemView2.findViewById(com.example.ui_kit.R.id.price);
            quantityTextView2 = cartItemView2.findViewById(com.example.ui_kit.R.id.counter_sht);
            deleteButton2 = cartItemView2.findViewById(R.id.imageView7);

            minusButton2 = cartItemView2.findViewById(com.example.ui_kit.R.id.minusButton);
            plusButton2 = cartItemView2.findViewById(com.example.ui_kit.R.id.plusButton);

            if (minusButton2 == null || plusButton2 == null) {
                View counterLayout = cartItemView2.findViewById(R.id.main);
                if (counterLayout != null) {
                    minusButton2 = counterLayout.findViewById(com.example.ui_kit.R.id.minusButton);
                    plusButton2 = counterLayout.findViewById(com.example.ui_kit.R.id.plusButton);
                }
            }
        }

        // Общие элементы
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        checkoutButton = findViewById(R.id.checkoutButton);
        totalLayout = findViewById(R.id.totalLayout);
    }

    private void setupCheckoutButton() {
        if (checkoutButton != null) {
            // Находим кнопку внутри layout button_big
            Button button = checkoutButton.findViewById(com.example.ui_kit.R.id.big_button);
            if (button != null) {
                button.setText("Перейти к оформлению заказа");
                button.setOnClickListener(v -> showSuccessNotification());
            }
        }
    }

    private void setupInitialVisibility() {
        // Устанавливаем видимость карточек в зависимости от флагов
        if (cartItemView1 != null) {
            cartItemView1.setVisibility(showItem1 ? View.VISIBLE : View.GONE);
        }

        if (cartItemView2 != null) {
            cartItemView2.setVisibility(showItem2 ? View.VISIBLE : View.GONE);
        }

        // Обновляем видимость блока с суммой и кнопкой оформления
        updateSummaryVisibility();
    }

    private void updateItemPrices() {
        // Обновляем отображение цен
        if (showItem1 && itemPriceTextView1 != null) {
            itemPriceTextView1.setText(ITEM_PRICE1 + " ₽");
        }

        if (showItem2 && itemPriceTextView2 != null) {
            itemPriceTextView2.setText(ITEM_PRICE2 + " ₽");
        }
    }

    private void setupBackButton() {
        View headerView = findViewById(R.id.header);
        if (headerView != null) {
            ImageView backButton = headerView.findViewById(com.example.ui_kit.R.id.back_cart);
            if (backButton != null) {
                backButton.setOnClickListener(v -> {
                    returnToCatalog();
                });
            }
        }
    }

    private void returnToCatalog() {
        Intent returnIntent = new Intent(cart.this, catalog.class);

        // Передаем обновленные данные обратно
        int updatedItemCount = itemCount1 + itemCount2;
        int updatedTotalPrice = getTotalAmount();

        returnIntent.putExtra("updated_item_count", updatedItemCount);
        returnIntent.putExtra("updated_total_price", updatedTotalPrice);
        returnIntent.putExtra("updated_item1_added", showItem1 && itemCount1 > 0);
        returnIntent.putExtra("updated_item2_added", showItem2 && itemCount2 > 0);

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void setupClickListeners() {
        // Обработчики для ПЕРВОЙ карточки
        if (showItem1) {
            if (minusButton1 != null) {
                minusButton1.setOnClickListener(v -> {
                    if (itemCount1 > 1) {
                        itemCount1--;
                        updateQuantityText(1);
                        updateTotalAmount();
                    } else {
                        // Если осталась 1 штука, удаляем товар
                        removeCard(1);
                    }
                });
            }

            if (plusButton1 != null) {
                plusButton1.setOnClickListener(v -> {
                    itemCount1++;
                    updateQuantityText(1);
                    updateTotalAmount();
                });
            }

            if (deleteButton1 != null) {
                deleteButton1.setOnClickListener(v -> removeCard(1));
            }
        }

        // Обработчики для ВТОРОЙ карточки
        if (showItem2) {
            if (minusButton2 != null) {
                minusButton2.setOnClickListener(v -> {
                    if (itemCount2 > 1) {
                        itemCount2--;
                        updateQuantityText(2);
                        updateTotalAmount();
                    } else {
                        removeCard(2);
                    }
                });
            }

            if (plusButton2 != null) {
                plusButton2.setOnClickListener(v -> {
                    itemCount2++;
                    updateQuantityText(2);
                    updateTotalAmount();
                });
            }

            if (deleteButton2 != null) {
                deleteButton2.setOnClickListener(v -> removeCard(2));
            }
        }
    }

    private void updateAllQuantityTexts() {
        if (showItem1) updateQuantityText(1);
        if (showItem2) updateQuantityText(2);
    }

    private void updateQuantityText(int cardNumber) {
        TextView quantityTextView;
        int count;

        if (cardNumber == 1) {
            quantityTextView = quantityTextView1;
            count = itemCount1;
        } else {
            quantityTextView = quantityTextView2;
            count = itemCount2;
        }

        if (quantityTextView != null) {
            String text = formatQuantityText(count);
            quantityTextView.setText(text);
        }
    }

    private String formatQuantityText(int count) {
        // Склонение слова "штука" в зависимости от количества
        if (count % 10 == 1 && count % 100 != 11) {
            return count + " штука";
        } else if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) {
            return count + " штуки";
        } else {
            return count + " штук";
        }
    }

    private int getTotalAmount() {
        return (itemCount1 * ITEM_PRICE1) + (itemCount2 * ITEM_PRICE2);
    }

    private void updateTotalAmount() {
        int totalAmount = getTotalAmount();
        if (totalAmountTextView != null) {
            totalAmountTextView.setText(totalAmount + " ₽");
        }

        // Обновляем видимость блока с суммой
        updateSummaryVisibility();
    }

    private void updateSummaryVisibility() {
        boolean hasItems = (itemCount1 > 0 || itemCount2 > 0);

        if (totalLayout != null) {
            totalLayout.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        }

        if (checkoutButton != null) {
            checkoutButton.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        }

        // Если нет товаров, показываем сообщение
        if (!hasItems) {
            showEmptyCartMessage();
        }
    }

    private void showEmptyCartMessage() {
        // Можно добавить TextView с сообщением "Корзина пуста"
        // или просто оставить Toast
        Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
    }

    private void removeCard(int cardNumber) {
        if (cardNumber == 1 && cartItemView1 != null) {
            cartItemView1.setVisibility(View.GONE);
            itemCount1 = 0;
            showItem1 = false;
            Toast.makeText(this, "Товар удален из корзины", Toast.LENGTH_SHORT).show();
        } else if (cardNumber == 2 && cartItemView2 != null) {
            cartItemView2.setVisibility(View.GONE);
            itemCount2 = 0;
            showItem2 = false;
            Toast.makeText(this, "Товар удален из корзины", Toast.LENGTH_SHORT).show();
        }

        updateTotalAmount();
        updateSummaryVisibility();
    }

    private void showSuccessNotification() {
        // Создаем затемненный фон
        View dimBackground = new View(this);
        dimBackground.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        dimBackground.setBackgroundColor(Color.argb(180, 0, 0, 0));
        dimBackground.setId(View.generateViewId());

        // Создаем уведомление
        LinearLayout notificationView = new LinearLayout(this);
        notificationView.setOrientation(LinearLayout.VERTICAL);
        notificationView.setGravity(Gravity.CENTER);

        // Создаем фон с закругленными углами
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(dpToPx(12));
        notificationView.setBackground(background);

        notificationView.setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32));

        // Текст уведомления
        TextView notificationText = new TextView(this);
        notificationText.setText("Заказ успешно оформлен");
        notificationText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        notificationText.setTextColor(Color.BLACK);
        notificationText.setTypeface(notificationText.getTypeface(), Typeface.BOLD);
        notificationText.setGravity(Gravity.CENTER);

        // Кнопка закрытия (опционально)
        Button closeButton = new Button(this);
        closeButton.setText("OK");
        closeButton.setBackgroundColor(getResources().getColor(com.example.ui_kit.R.color.accent));
        closeButton.setTextColor(Color.WHITE);
        closeButton.setOnClickListener(v -> {
            // Анимация исчезновения
            notificationView.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(new AccelerateInterpolator())
                    .withEndAction(() -> {
                        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
                        ViewGroup rootContainer = (ViewGroup) notificationView.getParent();
                        if (rootContainer != null) {
                            decorView.removeView(rootContainer);
                        }
                        navigateToGlavn();
                    })
                    .start();
        });

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.topMargin = dpToPx(16);
        closeButton.setLayoutParams(buttonParams);

        notificationView.addView(notificationText);
        notificationView.addView(closeButton);

        FrameLayout.LayoutParams notificationParams = new FrameLayout.LayoutParams(
                dpToPx(280),
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        notificationParams.gravity = Gravity.CENTER;
        notificationView.setLayoutParams(notificationParams);

        // Создаем корневой контейнер
        FrameLayout rootContainer = new FrameLayout(this);
        rootContainer.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        rootContainer.setId(View.generateViewId());

        // Добавляем элементы
        rootContainer.addView(dimBackground);
        rootContainer.addView(notificationView);

        // Добавляем в основную разметку
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        decorView.addView(rootContainer);

        // Анимация появления
        notificationView.setScaleX(0.8f);
        notificationView.setScaleY(0.8f);
        notificationView.setAlpha(0f);

        notificationView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();

        // Автоматическое закрытие через 5 секунд
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (notificationView.getParent() != null) {
                notificationView.animate()
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .alpha(0f)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .withEndAction(() -> {
                            if (notificationView.getParent() != null) {
                                ViewGroup parent = (ViewGroup) notificationView.getParent();
                                ViewGroup grandParent = (ViewGroup) parent.getParent();
                                if (grandParent != null) {
                                    grandParent.removeView(parent);
                                }
                            }
                            navigateToGlavn();
                        })
                        .start();
            }
        }, 5000);
    }

    private void navigateToGlavn() {
        Intent intent = new Intent(cart.this, glavn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

}