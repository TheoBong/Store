package io.github.paypal;

import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import io.github.Store;
import io.github.utils.PendingTransactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DepositHandler extends PaypalClient {
    private Store store;

    public DepositHandler(final Store store) {
        super(store);
        this.store = store;
    }

    public void createDepositOrder(final double cost, final String item, final Player player) {

        getInstance().getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
            final double total;

            if (store.getConfig().getBoolean("ITEMS." + item + ".PASS_FEE")) {
                total = (1000.0 / 956.0) * (cost + 0.30);
            } else {
                total = cost;
            }

            final DecimalFormat df = new DecimalFormat("#.##");

            final OrdersCreateRequest request = new OrdersCreateRequest();
            request.header("prefer", "return=representation");

            final OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");
            final PaymentMethod method = new PaymentMethod().payeePreferred("IMMEDIATE_PAYMENT_REQUIRED");
            final ApplicationContext context = new ApplicationContext()
                    .brandName(store.getConfig().getString("BRAND_NAME"))
                    .landingPage("LOGIN")
                    .userAction("PAY_NOW")
                    .shippingPreference("NO_SHIPPING")
                    .paymentMethod(method);
            orderRequest.applicationContext(context);
            final List<PurchaseUnitRequest> unitRequests = new ArrayList<>();
            final PurchaseUnitRequest unitRequest = new PurchaseUnitRequest()
                    .description("$" + df.format(cost) + " deposit for " + player.getUniqueId().toString())
                    .amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(df.format(total)));
            unitRequests.add(unitRequest);
            orderRequest.purchaseUnits(unitRequests);
            request.requestBody(orderRequest);

            HttpResponse<Order> repsonse = null;

            try {
                repsonse = this.client().execute(request);
            } catch (final IOException e) {
                e.printStackTrace();
            }

            HttpResponse<Order> finalRepsonse = repsonse;

            Logger logger = getInstance().getLogger();
            logger.info("");
            logger.info("New deposit!");
            logger.info("UUID: " + player.getUniqueId().toString());
            logger.info("Item: " + item);
            logger.info("Cost: $" + df.format(cost));
            logger.info("Total: $" + df.format(total));
            logger.info("ID: " + finalRepsonse.result().id());
            logger.info("");

            JSONObject pendingTransaction = new JSONObject();
            pendingTransaction.put("playerUUID", player.getUniqueId().toString());
            pendingTransaction.put("item", item);
            pendingTransaction.put("cost", cost);
            pendingTransaction.put("expiry", System.currentTimeMillis() + store.getConfig().getLong("EXPIRE_AFTER"));

            PendingTransactions pendingTransactions = store.getPendingTransactions();
            pendingTransactions.addPendingTransaction(finalRepsonse.result().id(), pendingTransaction);

            player.sendMessage(ChatColor.GREEN + "Here is your link for $" + df.format(cost) + " plus PayPal fees:");
            player.sendMessage(ChatColor.WHITE + "" + ChatColor.UNDERLINE + finalRepsonse.result().links().get(1).href());
        });
    }
}
