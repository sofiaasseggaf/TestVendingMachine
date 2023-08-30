package com.sofia.testvendingmachine.models;


import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * The stock of a product for use in a {@link VendingMachine}
 */
public class Stock {
    @NonNull
    private final Product product;
    private int available;

    public Stock(@NonNull Product product, int available) {
        if (available < 0) {
            throw new IllegalArgumentException("stock must be zero or greater");
        }

        this.product = product;
        this.available = available;
    }

    @NonNull
    public Product getProduct() {
        return product;
    }

    public int getAvailable() {
        return available;
    }

    /**
     * Reduces the available stock of this product by one.
     *
     * @throws UnsupportedOperationException if no product is currently available
     */
    public void reduceAvailable() {
        if (this.available < 1) {
            throw new UnsupportedOperationException(
                    String.format(
                            Locale.US,
                            "No stock for %s is available at this time.",
                            this.product));
        }

        this.available--;
    }

    @Override
    public String toString() {
        return String.format(
                Locale.US,
                "x%d %s",
                available,
                product.toString());
    }
}
