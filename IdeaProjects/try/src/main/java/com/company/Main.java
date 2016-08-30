package com.company;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;

import javax.management.Query;

import java.io.*;
import java.util.Iterator;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

public class Main {

    public static void main(String[] args) {
        // Initialize the Connection
        Cluster cluster = CouchbaseCluster.create("localhost");
        Bucket bucket = cluster.openBucket("sync_gateway");
        bucket.bucketManager().createPrimaryIndex(true, false);

        // read in the query
        if(args.length == 0){
            System.err.println("Must specify a dpaq file");
            System.exit(1);
        }
        String input = args[0];
        JsonObject dpaq = jsonReader(input);
        double latitude_start = dpaq.getDouble("Latitude_start");
        double latitude_end = dpaq.getDouble("Latitude_end");
        double longitude_start = dpaq.getDouble("Longitude_start");
        double longitude_end = dpaq.getDouble("Longitude_end");
        int accuracy = dpaq.getInt("Accuracy");
        Long time_start = dpaq.getLong("Time_start");
        Long time_end = dpaq.getLong("Time_end");

        // execute the query
        N1qlQueryResult result = bucket.query(select("Latitude","Longitude","Accuracy","Time","PBD_ID")
                .from("sync_gateway")
                .where(x("Type").eq(s("Location"))
                        .and(x("Accuracy").lte(accuracy))
                        .and(x("Time").gte(time_start))
                        .and(x("Time").lte(time_end))
                        .and(x("Latitude").gt(latitude_start))
                        .and(x("Latitude").lt(latitude_end))
                        .and(x("Longitude").gt(longitude_start))
                        .and(x("Longitude").lt(longitude_end))));

        // Print each found Row
        for (N1qlQueryRow row : result) {
            System.out.println(row);
        }

        // record the transaction
        String collector = (String) dpaq.get("Collector_Id");
        JsonObject transaction = jsonReader("location_transaction.json");
        JsonObject collector_record = JsonObject.create();
        // get this collector's transaction record if it exists
        if(transaction.containsKey(collector)){
            collector_record = (JsonObject) transaction.get(collector);
        }

        String requestId = result.requestId();
        JsonObject this_record = JsonObject.create();
        Iterator<N1qlQueryRow> iterator = result.rows();
        N1qlQueryRow n1qlQueryRow;
        JsonObject row;
        String pbdId;
        JsonArray output = JsonArray.create();

        while(iterator.hasNext()){
            n1qlQueryRow = iterator.next();
            row = n1qlQueryRow.value();
            pbdId = (String) row.get("PBD_ID");
            // increment the provider data counter
            if(this_record.containsKey(pbdId)){
                this_record.put(pbdId, this_record.getInt(pbdId)+1);
            }
            else{
                this_record.put(pbdId, 1);
            }
            // add the data to the output file
            output.add(row);
        }
        // store this transaction record
        collector_record.put(requestId, this_record);
        transaction.put(collector, collector_record);
        fileWriter(transaction.toString(), "location_transaction.json");
        // store the output
        fileWriter(output.toString(), "results/"+requestId);
        System.out.println("output file is: " + requestId + ".json");

    }

    private static JsonObject jsonReader(String filename){
        String filepath = "/home/jack/IdeaProjects/try/src/main/DPA/" + filename;
        try {
            InputStream fin = new FileInputStream(filepath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            fin.close();
            String string = sb.toString();
            JsonObject jsonObject = JsonObject.fromJson(string);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static void fileWriter(String content, String filename){
        String filepath = "/home/jack/IdeaProjects/try/src/main/DPA/" + filename;

        try {
            Writer output;
            output = new BufferedWriter(new FileWriter(filepath));
            output.append(content);
            output.close();
        }catch(Exception e){}
    }
}
