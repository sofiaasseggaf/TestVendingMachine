package com.sofia.testvendingmachine.viewmodels;

import com.sofia.testvendingmachine.models.Product;
import com.sofia.testvendingmachine.models.Stock;
import com.sofia.testvendingmachine.models.VendingMachine;
import com.sofia.testvendingmachine.services.IVendService;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton repository that creates vending machines.
 * <p>
 * A bit overkill for this app, but showcases the pattern for use cases like network access or
 * other IO or slow operations.
 */
public final class VendingMachineRepository {
    // normally would inject this as a singleton as an IoC; instead, old-school for the demo
    private static VendingMachineRepository instance = null;

    // and each repository only needs one VendingMachine
    private static IVendService vendingMachine = null;

    private VendingMachineRepository() {
        // normally stock would load out of a database, but hard-coded for demo
        final List<Stock> stock = new ArrayList<>();
        stock.add(new Stock(new Product("Biskuit", 6000), 2));
        stock.add(new Stock(new Product("Chips", 8000), 1));
        stock.add(new Stock(new Product("Oreo", 10000), 50));
        stock.add(new Stock(new Product("Tango", 12000), 2));
        stock.add(new Stock(new Product("Cokelat", 15000), 4));
        vendingMachine = new VendingMachine(stock);
    }

    public static VendingMachineRepository getInstance() {
        if (instance == null) {
            instance = new VendingMachineRepository();
        }

        return instance;
    }

    IVendService getVendingMachine() {
        return vendingMachine;
    }
}
