package io.github.paypal;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import io.github.Store;
import lombok.Getter;

public class PaypalClient {

    private static String ClientID;
    private static String Secret;
    static PayPalEnvironment environment;
    static PayPalHttpClient client;

    private @Getter Store instance;

    public PaypalClient(Store instance) {

        this.instance = instance;

        ClientID = instance.getConfig().getString("CLIENT_ID");
        Secret = instance.getConfig().getString("SECRET");

        this.environment = new PayPalEnvironment.Live(ClientID, Secret);
        this.client = new PayPalHttpClient(this.environment);
    }

    public PayPalHttpClient client() {
        return this.client;
    }
}
