package io.github.paypal;

import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.*;
import io.github.Store;
import io.github.utils.PendingTransactions;
import io.github.utils.TransactionHistory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import xyz.derkades.derkutils.ListUtils;

import java.io.IOException;
import java.util.List;

public class CaptureOrder {
    public static void main(Store store, String orderId, String item, String playerUUID, double cost, long expiry) {

        Order order = null;
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);

        try {
            HttpResponse<Order> response = PaypalClient.client.execute(request);
            order = response.result();
            System.out.println("Captured Purchase! Info: " + order.purchaseUnits().get(0).payments().captures().get(0).id());
            order.purchaseUnits().get(0).payments().captures().get(0).links()
                    .forEach(link -> System.out.println(link.rel() + " => " + link.method() + ":" + link.href()));

            JSONObject transaction = new JSONObject();
            transaction.put("playerUUID", playerUUID);
            transaction.put("item", item);
            transaction.put("cost", cost);
            transaction.put("created", expiry - 3600000);
            transaction.put("captured", System.currentTimeMillis());

            PendingTransactions pending = store.getPendingTransactions();
            pending.removePendingTransaction(orderId);

            TransactionHistory transactionHistory = store.getTransactionHistory();
            transactionHistory.addTransaction(orderId, transaction);

            Configuration config = store.getConfig();

            List<String> commands = config.getStringList("ITEMS." + item + ".COMMANDS");

            if (commands == null) {
                // If the action is null (so 'slot' is not found in the config) it is probably a wildcard
                commands = config.getStringList("ITEMS.-1.COMMANDS");

                if (commands == null) { //If it is still null it must be missing
                    System.out.println("No commands to run");
                    return;
                }
            }

            Player player = Bukkit.getPlayer(playerUUID);

            commands = ListUtils.replaceInStringList(commands,
                    new Object[] { "{player}" },
                    new Object[] { player.getName() });

            commands.forEach(command -> store.getServer().dispatchCommand(store.getServer().getConsoleSender(), command));

        } catch (IOException ioe) {
            if (ioe instanceof HttpException) {
                // Something went wrong server-side
                HttpException he = (HttpException) ioe;
                System.out.println(he.getMessage());
            } else {
                // Something went wrong client-side
                System.out.println("U-oh! I made a fucky wucky");
            }
        }
    }
}