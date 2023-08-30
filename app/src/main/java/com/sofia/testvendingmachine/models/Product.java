package com.sofia.testvendingmachine.models;

import java.util.Locale;
import androidx.annotation.NonNull;
/**
 * A product
 */
public class Product {
    private final String name;

    /**
     * cost in cents (100th of a US dollar)
     */
    private final int costInUsc;

    /**
     * Construct a product instance
     *
     * @param name      product name; must not be empty
     * @param costInUsc cost in cents (100th of a US dollar)
     */
    public Product(@NonNull String name, int costInUsc) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("name may not be empty");
        }

        if (costInUsc < 0) {
            throw new IllegalArgumentException("costInUsd must be zero or greater");
        }

        this.name = name;
        this.costInUsc = costInUsc;
    }

    public String getName() {
        return name;
    }

    public int getCostInUsc() {
        return costInUsc;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s/$%3.2f", name, (float) costInUsc / 100);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {
            Product p = (Product) obj;
            return p.name.equals(name) &&
                    p.costInUsc == costInUsc;
        }

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + name.hashCode();
        hash = hash * 13 + costInUsc;
        return hash;
    }
}
