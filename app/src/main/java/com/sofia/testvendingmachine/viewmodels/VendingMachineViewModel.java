package com.sofia.testvendingmachine.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sofia.testvendingmachine.R;
import com.sofia.testvendingmachine.models.Product;
import com.sofia.testvendingmachine.services.IVendService;

import java.util.List;

public final class VendingMachineViewModel extends AndroidViewModel {
    @NonNull
    private final MutableLiveData<String> display = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> change = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> product1 = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> product2 = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> product3 = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> product4 = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> product5 = new MutableLiveData<>();

    @Nullable
    private IVendService vendingMachine;

    public VendingMachineViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(@NonNull VendingMachineRepository vendingMachineRepository) {
        // only create a new one if necessary
        if (this.vendingMachine == null) {
            this.vendingMachine = vendingMachineRepository.getVendingMachine();

            // initialize live data
            List<Product> products = this.vendingMachine.getProducts();
            product1.postValue(products.get(0).getName());
            product2.postValue(products.get(1).getName());
            product3.postValue(products.get(2).getName());
            product4.postValue(products.get(3).getName());
            product5.postValue(products.get(4).getName());
            updateDisplay();
        }
    }

    public LiveData<String> getVendingMachineDisplay() {
        return this.display;
    }

    public LiveData<String> getVendingMachineChangeDisplay() {
        return this.change;
    }

    public LiveData<String> getVendingMachineProductDisplay(int productIndex) {
        switch (productIndex) {
            case 0:
                return this.product1;
            case 1:
                return this.product2;
            case 2:
                return this.product3;
            case 3:
                return this.product4;
            case 4:
                return this.product5;
            default:
                throw new IllegalArgumentException(
                        "Only 5 products available but item " + (productIndex - 1) + " was requested");
        }
    }

    public void collectCoins() {
        if (this.vendingMachine == null) {
            throw new UnsupportedOperationException("you must call init() before calling any other methods in this view model");
        }

        this.vendingMachine.collectCoins();
        updateDisplay();
    }

    public boolean insertCoin(int coinValue) {
        if (this.vendingMachine == null) {
            throw new UnsupportedOperationException("you must call init() before calling any other methods in this view model");
        }

        final boolean result = this.vendingMachine.insertCoin(coinValue);
        updateDisplay();
        return result;
    }

    public void purchaseProduct(int productIndex) {
        if (this.vendingMachine == null) {
            throw new UnsupportedOperationException("you must call init() before calling any other methods in this view model");
        }

        this.vendingMachine.purchaseProduct(productIndex);
        updateDisplay();
    }

    public void returnCoins() {
        if (this.vendingMachine == null) {
            throw new UnsupportedOperationException("you must call init() before calling any other methods in this view model");
        }

        this.vendingMachine.returnCoins();
        updateDisplay();
    }

    private void updateDisplay() {
        if (this.vendingMachine == null) {
            throw new UnsupportedOperationException("you must call init() before calling any other methods in this view model");
        }

        this.display.postValue(this.vendingMachine.updateAndGetCurrentMessageForDisplay());
        this.change.postValue(
                this.getApplication().getResources().getString(R.string.vend_action_collect,
                        (float) this.vendingMachine.getUscInReturn() / 100));
    }
}
