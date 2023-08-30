package com.sofia.testvendingmachine.services;

import com.sofia.testvendingmachine.models.Product;
import androidx.annotation.NonNull;
import java.util.List;


/**
 * Service for core vend functionality
 */
public interface IVendService {
    /**
     * Accepts a coin (based on its value) and adds to current in-process cash
     * if valid
     *
     * @param usc US cent amount (100th of a USD) of the coin inserted
     * @return true if the coin was accepted; false if the coin was rejected
     * and added to the coins-in-return
     */
    boolean insertCoin(int usc);

    /**
     * Updates the current display message based on the state of the vending
     * machine and returns what that should show to the user
     * <p>
     * Non-deterministic: display may change from one call to the next
     * depending on the state of the machine
     *
     * @return the message to display to the user, will be listed as USD if
     * any value is shown
     */
    @NonNull
    String updateAndGetCurrentMessageForDisplay();

    /**
     * Gets the value of coins in the return (either that were rejected during
     * insert, or user requested a return
     *
     * @return the value of coins in the return, as cents (100th of a USD)
     */
    int getAcceptedUsc();

    /**
     * Gets the value of coins in the return (either that were rejected during
     * insert, or user requested a return
     *
     * @return the value of coins in the return, as cents (100th of a USD)
     */
    int getUscInReturn();

    /**
     * Purchases the requested product.
     * <p>
     * If enough currency and product is available, will deliver to the user
     * and deduct the value from the in-process cash.
     * <p>
     * If the product may not be purchased at this time, will return false.
     * <p>
     * Either success or failure will update the display appropriately; make
     * sure to check and display to the user
     *
     * @param productIndex the index of the requested product
     * @return true if the product was purchased and delivered to the user;
     * false if invalid for any reason
     */
    boolean purchaseProduct(int productIndex);

    /**
     * User may request coins to return all available currency in the machine
     * not used for a purchase yet; available coins go to the return, aka
     * {@link #getUscInReturn()}
     */
    void returnCoins();

    /**
     * Simulate user collecting the coins in the return.
     * <p>
     * Effectively zeros out anything in {@link #getUscInReturn()}.
     */
    void collectCoins();

    /**
     * List of all products in the machine
     *
     * @return list of products
     */
    @NonNull
    List<Product> getProducts();
}
