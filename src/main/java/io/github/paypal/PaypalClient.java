package io.github.paypal;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import io.github.Store;

public class PaypalClient {

    public PayPalHttpClient client;

    public PaypalClient(Store store) {
        String clientID = store.getConfig().getString("CLIENT_ID");
        String secret = store.getConfig().getString("SECRET");

        PayPalEnvironment environment = new PayPalEnvironment.Live(clientID, secret);
        client = new PayPalHttpClient(environment);
    }
}
