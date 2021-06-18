package io.github.paypal;

import io.github.Store;
import io.github.utils.PendingTransactions;
import org.json.simple.JSONObject;

import java.util.Map;

public class CaptureTask implements Runnable {
    private Store store;

    public CaptureTask(Store store) {
        this.store = store;
    }

    @Override
    public void run() {
        PendingTransactions pending = store.getPendingTransactions();
        Map<String, JSONObject> transactions;
        transactions = pending.pendingTransactionList();

        for (Map.Entry<String,JSONObject> entry : transactions.entrySet()) {
            String orderId = entry.getKey();
            JSONObject pendingTransaction = entry.getValue();


            long expiry = (long) pendingTransaction.get("expiry");
            String player = (String) pendingTransaction.get("playerUUID");
            double cost = (double) pendingTransaction.get("cost");
            String item = (String) pendingTransaction.get("item");

            if (expiry < System.currentTimeMillis()) {
                PendingTransactions pendingTransactions = Store.INSTANCE.getPendingTransactions();
                pendingTransactions.removePendingTransaction(orderId);
            } else {
                CaptureOrder.main(store, orderId, item, player, cost, expiry);
            }
        }
    }
}
