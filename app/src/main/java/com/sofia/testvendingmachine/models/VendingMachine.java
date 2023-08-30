package com.sofia.testvendingmachine.models;

import androidx.annotation.NonNull;

import com.sofia.testvendingmachine.services.IVendService;
import com.annimon.stream.Stream;
import java.util.List;
import java.util.Locale;

/**
 * A vending machine
 */
public class VendingMachine implements IVendService {

    private static final int DUA_RIBU = 2000;

    private static final int LIMA_RIBU = 5000;

    private static final int SEPULUH_RIBU = 10000;

    private static final int DUA_PULUH_RIBU = 20000;

    private static final int LIMA_PULUH_RIBU = 50000;

    private static final String MSG_STATIC_FORMAT_AVAILABLE = "$%3.2f";
    private static final String MSG_STATIC_INSERT_COIN = "INSERT COIN";
    private static final String MSG_STATIC_EXACT_CHANGE_ONLY = "EXACT CHANGE ONLY";
    private static final String MSG_FORMAT_PRICE = "HARGA $%3.2f";
    private static final String MSG_NORMAL_SOLD_OUT = "SOLD OUT";
    private static final String MSG_NORMAL_THANK_YOU = "THANK YOU";

    private final List<Stock> availableStock;

    /**
     * In-flight/current value of currency provided by the current/last user
     * for use in purchases
     * <p>
     * Starts with no currency in flight—no freebies for the first user!
     */
    private int currencyInUsc = 0;

    /**
     * Value of coins in the return tray
     */
    private int returnInUsc = 0;

    /**
     * Track how much change is available in the machine.
     * <p>
     * For this demo, assumes the value can be covered by any amount of coins.
     * In other words, does not track individual coins available as change.
     * <p>
     * Starts with $4.00 in change (since the kata requirements did not specify
     * the amount)
     */
    private int changeInUsc = 400;

    @NonNull
    private String lastMessage = MSG_STATIC_INSERT_COIN;

    /**
     * Construct a machine instance
     *
     * @param availableStock available products and their current stock
     */
    public VendingMachine(@NonNull List<Stock> availableStock) {
        this.availableStock = availableStock;

        // initialize first message, just in case INSERT COINS is not the default based on the available stock provided
        this.updateAndGetCurrentMessageForDisplay();
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "$%3.2f in flight, $%3.2f in change, and %d products",
                (float) currencyInUsc / 100,
                (float) changeInUsc / 100,
                availableStock.size());
    }

    @Override
    public boolean insertCoin(int usc) {
        // check for valid coin
        switch (usc) {
            case DUA_RIBU:
            case LIMA_RIBU:
            case SEPULUH_RIBU:
            case DUA_PULUH_RIBU:
            case LIMA_PULUH_RIBU:
                // valid
                this.currencyInUsc += usc;
                this.lastMessage = String.format(
                        Locale.US,
                        MSG_STATIC_FORMAT_AVAILABLE,
                        (float) this.currencyInUsc / 100);
                return true;
            default:
                // invalid coins: pennies, drachmas, kronors, pfennigs, etc.
                this.returnInUsc += usc;
                return false;
        }
    }

    @NonNull
    @Override
    public String updateAndGetCurrentMessageForDisplay() {
        String msgToDeliver = this.lastMessage;

        // now that any temporary message is saved for deliver, reset the next call to the
        // current state of the machine
        if (this.currencyInUsc == 0) {
            // no money inserted yet
            this.lastMessage = MSG_STATIC_INSERT_COIN;

            // REQUIREMENT: When the machine is not able to make change with the money in the machine for any of the items that it sells, it will display EXACT CHANGE ONLY instead of INSERT COIN.
            // Personal note: The provided description is a bit trivialized. The real logic needs to check change coins available and the matrix of what possible combinations can provide what values, the minimum and maximum amount of value for an accepted coin, whether or not accepted coins may also be used in change (for example, paper dollars can't be returned as change, but other coins should be able to funnel through the system as change if too many are provided), the price of all items, and figure out what the limits of all those items combined are; which gets complicated;
            // therefore, sticking with the naive algorithm of is there enough change value to at least match the price of the most expensive item
            for (Stock stock : this.availableStock) {
                if (stock.getProduct().getCostInUsc() > this.changeInUsc) {
                    this.lastMessage = MSG_STATIC_EXACT_CHANGE_ONLY;
                    break;
                }
            }
        } else {
            this.lastMessage = String.format(
                    Locale.US,
                    MSG_STATIC_FORMAT_AVAILABLE,
                    (float) this.currencyInUsc / 100);
        }

        return msgToDeliver;
    }

    @Override
    public int getAcceptedUsc() {
        return currencyInUsc;
    }

    @Override
    public int getUscInReturn() {
        return returnInUsc;
    }

    @Override
    public boolean purchaseProduct(int productIndex) {
        return productIndex < availableStock.size() &&
                tryToPurchase(availableStock.get(productIndex));
    }

    private boolean tryToPurchase(@NonNull final Stock stock) {
        // check stock
        if (stock.getAvailable() == 0) {
            this.lastMessage = MSG_NORMAL_SOLD_OUT;
            return false;
        }

        // check available currency compared to price
        Product product = stock.getProduct();

        if (this.currencyInUsc - product.getCostInUsc() < 0) {
            // not enough money
            this.lastMessage = String.format(
                    Locale.US,
                    MSG_FORMAT_PRICE,
                    (float) product.getCostInUsc() / 100);
            return false;
        }

        // passed tests; buy! buy! buy!

        // reduce stock
        stock.reduceAvailable();

        // take cost from active currency...
        this.currencyInUsc -= product.getCostInUsc();

        // ...and add it to the stock of change available
        // TODO: until exact coin change is implemented (as opposed to just value processing), the following does not make sense since the machine would never run out of change
        // this.changeInUsc += product.getCostInUsc();
        // so instead pull change value out of change and do NOT recycle in provided coins back into the change purse
        this.changeInUsc -= this.currencyInUsc;

        // then zero out currency, returning to the user anything left;
        this.returnInUsc += this.currencyInUsc;
        this.currencyInUsc = 0;

        this.lastMessage = MSG_NORMAL_THANK_YOU;

        // while the user enjoys their purchase, report success
        return true;
    }

    @Override
    public void returnCoins() {
        // these two statements should be transactional (instead of the current atomic but separate) to ensure thread-safety, but this isn't banking software—it is a demo for crying out loud
        this.returnInUsc += this.currencyInUsc;
        this.currencyInUsc = 0;

        // reset state of display
        this.updateAndGetCurrentMessageForDisplay();
    }

    @Override
    public void collectCoins() {
        this.returnInUsc = 0;
    }

    @NonNull
    @Override
    public List<Product> getProducts() {
        // some Stream API (with backward compatibility library)
        return Stream.of(this.availableStock).map(Stock::getProduct).toList();
    }
}
