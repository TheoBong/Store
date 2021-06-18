package io.github.utils;

import io.github.Store;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TransactionHistory {

    File folder = Store.INSTANCE.getDataFolder();
    String location = "transactions.json";
    Map<String, JSONObject> transactions;

    public TransactionHistory() {
        transactions = new HashMap<String, JSONObject>();
    }


    public void exportConfig() {
        File file = new File(folder, location);

        try {
            file.createNewFile();

            JSONObject mainObject = new JSONObject();
            mainObject.put("transactions", new JSONObject(transactions));

            FileWriter fileWriter = new FileWriter(file.getCanonicalFile());
            fileWriter.write(mainObject.toJSONString());
            fileWriter.close();

        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void importConfig() {
        JSONParser parser = new JSONParser();

        File file = new File(folder, location);

        try {
            if(file.createNewFile()){

                JSONObject mainObject = new JSONObject();
                mainObject.put("transactions", new JSONObject(transactions));

                FileWriter fileWriter = new FileWriter(file.getCanonicalFile());
                fileWriter.write(mainObject.toJSONString());
                fileWriter.close();
            }
        } catch (IOException e) {
        }

        File newFile = new File(folder, location);

        Scanner fileReader;
        try {
            fileReader = new Scanner(newFile);

            try {
                Object config = parser.parse(fileReader.nextLine());
                JSONObject jConfig = (JSONObject)config;

                transactions = ((HashMap)jConfig.get("transactions"));


            } catch (ParseException e) {
                Bukkit.getLogger().warning(e.getMessage());
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
        }


    }

    public JSONObject getTransactionPlayer(String playerUUID){
        for (JSONObject jsonObject : transactions.values()) {
            String storedUUID = (String) jsonObject.get("playerUUID");
            if (storedUUID.equals(playerUUID)) {
                return jsonObject;
            }
        }
        return null;
    }

    public JSONObject getTransaction(String orderId){
        if(!transactions.containsKey(orderId)){
            return null;
        }
        return transactions.get(orderId);
    }

    public void addTransaction(String orderId, JSONObject transaction) {
        transactions.put(orderId, transaction);

        new Thread(() -> {
            exportConfig();
        }).start();
    }

}
