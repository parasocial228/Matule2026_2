package com.example.matule2026;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class catalog extends AppCompatActivity {
    private LinearLayout modalLayout;
    private View dimBackground;
    private ConstraintLayout mainLayout;
    private Button buttonAddRemove1;
    private Button buttonAddRemove2;
    private View cartLayout; // Вся панель корзины
    private TextView cartViewText; // Текст "В корзину"
    private TextView cartPriceText; // Текст с ценой
    private int cartItemCount = 0;
    private Button modalAddButton;
    private int totalPrice = 0;
    private boolean isItem1Added = false;
    private boolean isItem2Added = false;
    private boolean isModalItemAdded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catalog);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainLayout = findViewById(R.id.main);

        // Настройка навигации по таббару
        setupTabBar();

        // Находим элементы корзины
        setupCartElements();

        // Находим кнопки в primary layouts
        setupAddButtons();

        // Настройка кнопок "Добавить/Убрать"
        setupAddRemoveButtons();

       setupCartClickListener();
        setupProfileIconClickListener();

        // Создание UI элементов для модального окна
        createDimBackground();
        createModal();

        // Инициализация видимости панели корзины
        updateCartVisibility();
    }

    private void setupTabBar() {
        ImageView buttonGlavn = (ImageView)this.findViewById(com.example.ui_kit.R.id.button_glavn);
        ImageView buttonCatalog = (ImageView)this.findViewById(com.example.ui_kit.R.id.button_catalog);
        ImageView buttonProject = (ImageView)this.findViewById(com.example.ui_kit.R.id.button_project);
        ImageView buttonProfile = (ImageView)this.findViewById(com.example.ui_kit.R.id.button_profile);

        buttonGlavn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!catalog.this.getClass().getSimpleName().equals("MainActivity")) {
                    Intent intent = new Intent(catalog.this, glavn.class);
                    catalog.this.startActivity(intent);
                }
            }
        });

        buttonCatalog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!catalog.this.getClass().getSimpleName().equals("CatalogActivity")) {
                    Intent intent = new Intent(catalog.this, catalog.class);
                    catalog.this.startActivity(intent);
                }
            }
        });

        buttonProject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!catalog.this.getClass().getSimpleName().equals("ProjectActivity")) {
                    Intent intent = new Intent(catalog.this, project.class);
                    catalog.this.startActivity(intent);
                }
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!catalog.this.getClass().getSimpleName().equals("ProfileActivity")) {
                    Intent intent = new Intent(catalog.this, profile.class);
                    catalog.this.startActivity(intent);
                }
            }
        });
    }

    private void setupCartElements() {
        // Находим панель корзины
        cartLayout = findViewById(R.id.cart_layout); // Убедитесь, что у include есть id

        if (cartLayout != null) {
            cartViewText = cartLayout.findViewById(com.example.ui_kit.R.id.cartView);
            cartPriceText = cartLayout.findViewById(com.example.ui_kit.R.id.price);
        }
    }

    private void setupAddButtons() {
        // Находим primary layouts по их ID
        View cart1Include = findViewById(R.id.cart1);
        View cart2Include = findViewById(R.id.cart2);

        if (cart1Include != null) {
            // Находим кнопку внутри первого товара
            buttonAddRemove1 = cart1Include.findViewById(com.example.ui_kit.R.id.buttoncart);

            // Назначаем клик на всю карточку для показа модального окна
            cart1Include.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isModalItemAdded = isItem1Added;
                    updateModalButtonState();
                    showModal();
                }
            });
            if (cart2Include != null) {
                buttonAddRemove2 = cart2Include.findViewById(com.example.ui_kit.R.id.buttoncart);

                // Устанавливаем текст для cart2 на "Шорты вторник для машинного вязания"
                TextView nameProduct2 = cart2Include.findViewById(com.example.ui_kit.R.id.nameProduct);
                if (nameProduct2 != null) {
                    nameProduct2.setText("Шорты вторник для машинного вязания");
                }
            }
        }

        if (cart2Include != null) {
            // Находим кнопку внутри второго товара
            buttonAddRemove2 = cart2Include.findViewById(com.example.ui_kit.R.id.buttoncart);

            // Назначаем клик на всю карточку для показа модального окна
            cart2Include.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isModalItemAdded = isItem2Added;
                    updateModalButtonState();
                    showModal();
                }
            });
        }
    }
    private void setupProfileIconClickListener() {
        ImageView profileIcon = findViewById(R.id.imageView12);
        if (profileIcon != null) {
            // Удаляем старый обработчик, если он был установлен через XML
            profileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProfile(v);
                }
            });
        }
    }
    private void setupAddRemoveButtons() {
        View.OnClickListener buttonClickListener = v -> {
            Button button = (Button) v;
            int itemPrice = 300; // Цена товара
            boolean isFirstItem = (button == buttonAddRemove1);

            if (button.getText().equals("Добавить")) {
                updateCartItemCount(1);
                updateTotalPrice(itemPrice, true);
                updateButtonToRemove(button);

                // Обновляем соответствующий флаг
                if (isFirstItem) {
                    isItem1Added = true;
                } else {
                    isItem2Added = true;
                }
            } else {
                updateCartItemCount(-1);
                updateTotalPrice(itemPrice, false);
                updateButtonToAdd(button);

                // Обновляем соответствующий флаг
                if (isFirstItem) {
                    isItem1Added = false;
                } else {
                    isItem2Added = false;
                }
            }

            updateCartVisibility();
            updateCartText();
        };

        if (buttonAddRemove1 != null) {
            // Восстановление состояния кнопки 1
            if (isItem1Added) {
                updateButtonToRemove(buttonAddRemove1);
            } else {
                updateButtonToAdd(buttonAddRemove1);
            }
            buttonAddRemove1.setOnClickListener(buttonClickListener);
        }

        if (buttonAddRemove2 != null) {
            // Восстановление состояния кнопки 2
            if (isItem2Added) {
                updateButtonToRemove(buttonAddRemove2);
            } else {
                updateButtonToAdd(buttonAddRemove2);
            }
            buttonAddRemove2.setOnClickListener(buttonClickListener);
        }
    }

    private void updateModalButtonState() {
        if (isModalItemAdded) {
            updateModalButtonToRemove();
        } else {
            updateModalButtonToAdd();
        }
    }

    private void setupCartClickListener() {
        if (cartLayout != null) {
            cartLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCart(v);
                }
            });
        }
    }

    private void updateCartItemCount(int change) {
        cartItemCount = Math.max(0, cartItemCount + change);
    }

    private void updateTotalPrice(int itemPrice, boolean add) {
        if (add) {
            totalPrice += itemPrice;
        } else {
            totalPrice -= itemPrice;
        }
        totalPrice = Math.max(0, totalPrice);
    }

    private void updateCartText() {
        if (cartViewText != null) {
            if (cartItemCount == 0) {
                cartViewText.setText("В корзину");
            } else if (cartItemCount == 1) {
                cartViewText.setText("1 товар");
            } else {
                cartViewText.setText(cartItemCount + " товара");
            }
        }

        if (cartPriceText != null) {
            cartPriceText.setText(totalPrice + " ₽");
        }
    }

    private void updateCartVisibility() {
        if (cartLayout != null) {
            if (cartItemCount > 0) {
                cartLayout.setVisibility(View.VISIBLE);
            } else {
                cartLayout.setVisibility(View.GONE);
            }
        }
    }

    private void handleModalAddButtonClick() {
        int itemPrice = 300;
        boolean isFirstItemModal = (modalAddButton.getText().toString().contains("300"));

        if (modalAddButton.getText().toString().contains("Добавить")) {
            updateCartItemCount(1);
            updateTotalPrice(itemPrice, true);
            updateModalButtonToRemove();

            // Обновляем состояние соответствующей кнопки в карточке
            if (isFirstItemModal) {
                isItem1Added = true;
                if (buttonAddRemove1 != null) {
                    updateButtonToRemove(buttonAddRemove1);
                }
            } else {
                isItem2Added = true;
                if (buttonAddRemove2 != null) {
                    updateButtonToRemove(buttonAddRemove2);
                }
            }

            isModalItemAdded = true;
        } else {
            updateCartItemCount(-1);
            updateTotalPrice(itemPrice, false);
            updateModalButtonToAdd();

            // Обновляем состояние соответствующей кнопки в карточке
            if (isFirstItemModal) {
                isItem1Added = false;
                if (buttonAddRemove1 != null) {
                    updateButtonToAdd(buttonAddRemove1);
                }
            } else {
                isItem2Added = false;
                if (buttonAddRemove2 != null) {
                    updateButtonToAdd(buttonAddRemove2);
                }
            }

            isModalItemAdded = false;
        }

        updateCartVisibility();
        updateCartText();
    }

    private void updateButtonToAdd(Button button) {
        button.setSelected(false);
        // Если вы используете селектор, просто обновите текст
        button.setText("Добавить");
        button.setBackgroundResource(com.example.ui_kit.R.drawable.blue_background);
        button.setTextColor(ContextCompat.getColor(this, com.example.ui_kit.R.color.white));
    }
    private void updateButtonToRemove(Button button) {
        button.setSelected(true);
        button.setBackgroundResource(com.example.ui_kit.R.drawable.blue_border);
        button.setText("Убрать");
        button.setTextColor(ContextCompat.getColor(this, com.example.ui_kit.R.color.accent));
    }

    private void updateModalButtonToAdd() {
        modalAddButton.setText("Добавить за 300 ₽");
        modalAddButton.setBackgroundTintList(ColorStateList.valueOf(getColor(com.example.ui_kit.R.color.accent)));
        modalAddButton.setTextColor(getColor(R.color.white));
    }

    private void updateModalButtonToRemove() {
        modalAddButton.setText("Убрать");
        modalAddButton.setBackgroundTintList(ColorStateList.valueOf(getColor(com.example.ui_kit.R.color.input_bg)));
        modalAddButton.setTextColor(getColor(com.example.ui_kit.R.color.accent));
    }

    private void createDimBackground() {
        dimBackground = new View(this);
        dimBackground.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        ));
        dimBackground.setBackgroundColor(Color.argb(150, 0, 0, 0));
        dimBackground.setVisibility(View.GONE);
        dimBackground.setOnClickListener(v -> hideModal());
        mainLayout.addView(dimBackground);
    }

    private void createModal() {
        modalLayout = new LinearLayout(this);
        modalLayout.setOrientation(LinearLayout.VERTICAL);
        modalLayout.setPadding(dpToPx(20), dpToPx(30), dpToPx(20), dpToPx(20));
        modalLayout.setBackgroundResource(com.example.ui_kit.R.drawable.modal_rounded);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        modalLayout.setLayoutParams(params);
        modalLayout.setVisibility(View.GONE);

        // Заголовок и кнопка закрытия
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        title.setText("Рубашка Воскресенье для машинного вязания");
        title.setTextColor(getColor(R.color.black));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setMaxLines(2);

        ImageView closeButton = new ImageView(this);
        closeButton.setImageResource(com.example.ui_kit.R.drawable.close_small);
        closeButton.setOnClickListener(v -> hideModal());

        topRow.addView(title);
        topRow.addView(closeButton);

        TextView descriptionLabel = new TextView(this);
        descriptionLabel.setText("Описание");
        descriptionLabel.setTextColor(getColor(com.example.ui_kit.R.color.caption));
        descriptionLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        descriptionLabel.setPadding(0, dpToPx(20), 0, 0);

        TextView descriptionText = new TextView(this);
        descriptionText.setText("Мой выбор для этих шапок – кардные составы, которые раскрываются деликатным пушком. Кашемиры, мериносы, смесовки с ними отлично подойдут на шапку.\n" +
                "Кардные составы берите в большое количество сложений, вязать будем резинку 1х1, плотненько.\n" +
                "Пряжу 1400-1500м в 100г в 4 сложения, пряжу 700м в 2 сложения. Ориентир для конечной толщины – 300-350м в 100г.\n" +
                "Артикулы, из которых мы вязали эту модель: Zermatt Zegna Baruffa, Cashfive, Baby Cashmere Loro Piana, Soft Donegal и другие.\n" +
                "Примерный расход на шапку с подгибом 70-90г.");
        descriptionText.setTextColor(getColor(R.color.black));
        descriptionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        descriptionText.setLineSpacing(0, 1.2f);

        TextView priceLabel = new TextView(this);
        priceLabel.setText("Примерный расход:");
        priceLabel.setTextColor(getColor(com.example.ui_kit.R.color.caption));
        priceLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        priceLabel.setPadding(0, dpToPx(20), 0, 0);

        TextView priceValue = new TextView(this);
        priceValue.setText("80-90 г");
        priceValue.setTextColor(getColor(R.color.black));
        priceValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        modalAddButton = new Button(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, dpToPx(20), 0, dpToPx(10));
        modalAddButton.setLayoutParams(buttonParams);
        modalAddButton.setText("Добавить за 690 ₽");
        modalAddButton.setTextColor(getColor(R.color.white));
        modalAddButton.setBackgroundResource(com.example.ui_kit.R.drawable.blue_background);
        modalAddButton.setBackgroundTintList(ColorStateList.valueOf(getColor(com.example.ui_kit.R.color.accent)));
        modalAddButton.setAllCaps(false);
        modalAddButton.setOnClickListener(v -> handleModalAddButtonClick());

        modalLayout.addView(topRow);
        modalLayout.addView(descriptionLabel);
        modalLayout.addView(descriptionText);
        modalLayout.addView(priceLabel);
        modalLayout.addView(priceValue);
        modalLayout.addView(modalAddButton);

        mainLayout.addView(modalLayout);
    }

    private void showModal() {
        dimBackground.setVisibility(View.VISIBLE);
        modalLayout.setVisibility(View.VISIBLE);

        dimBackground.setAlpha(0f);
        dimBackground.animate().alpha(1f).setDuration(300).start();

        modalLayout.setTranslationY(modalLayout.getHeight());
        modalLayout.animate()
                .translationY(0)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void hideModal() {
        dimBackground.animate()
                .alpha(0f)
                .setDuration(300)
                .start();

        modalLayout.animate()
                .translationY(modalLayout.getHeight())
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    modalLayout.setVisibility(View.GONE);
                    dimBackground.setVisibility(View.GONE);
                })
                .start();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // Методы для открытия других активностей через XML onClick
    public void openGlavn(View view) {
        startActivity(new Intent(this, glavn.class));
    }

    public void openCatalog(View view) {
        startActivity(new Intent(this, catalog.class));
    }

    public void openProject(View view) {
        startActivity(new Intent(this, project.class));
    }

    public void openProfile(View view) {
        startActivity(new Intent(this, profile.class));
    }

    public void openCart(View view) {
        Intent i = new Intent(this, cart.class);
        i.putExtra("item_count", cartItemCount);
        i.putExtra("total_price", totalPrice);
        i.putExtra("is_item1_added",
                buttonAddRemove1 != null && buttonAddRemove1.getText().equals("Убрать"));
        i.putExtra("is_item2_added",
                buttonAddRemove2 != null && buttonAddRemove2.getText().equals("Убрать"));
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("cartItemCount", cartItemCount);
        outState.putInt("totalPrice", totalPrice);
        if (buttonAddRemove1 != null) {
            outState.putBoolean("isAdded1", buttonAddRemove1.getText().equals("Убрать"));
        }
        if (buttonAddRemove2 != null) {
            outState.putBoolean("isAdded2", buttonAddRemove2.getText().equals("Убрать"));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cartItemCount = savedInstanceState.getInt("cartItemCount", 0);
        totalPrice = savedInstanceState.getInt("totalPrice", 0);

        if (buttonAddRemove1 != null && savedInstanceState.getBoolean("isAdded1", false)) {
            updateButtonToRemove(buttonAddRemove1);
        }
        if (buttonAddRemove2 != null && savedInstanceState.getBoolean("isAdded2", false)) {
            updateButtonToRemove(buttonAddRemove2);
        }

        updateCartVisibility();
        updateCartText();
    }
}