package com.company;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Query;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.query.QueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Sort;
import com.couchbase.client.java.query.dsl.path.AsPath;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

public class Database {

    private Database() { }

    public static List<Map<String, Object>> findAllAirports(final Bucket bucket, final String params) {
        Statement query;

        AsPath prefix = select("airportname").from(i(bucket.name()));
        if (params.length() == 3) {
            query = prefix.where(x("faa").eq(s(params.toUpperCase())));
        } else if (params.length() == 4 && (params.equals(params.toUpperCase()) || params.equals(params.toLowerCase()))) {
            query = prefix.where(x("icao").eq(s(params.toUpperCase())));
        } else {
            query = prefix.where(i("airportname").like(s(params + "%")));
        }

        QueryResult result = bucket.query(Query.simple(query));
        return extractResultOrThrow(result);
    }

    public static List<Map<String, Object>> findAllFlightPaths(final Bucket bucket, String from, String to, Calendar leave) {
        Statement query = select(x("faa").as("fromAirport"))
                .from(i(bucket.name()))
                .where(x("airportname").eq(s(from)))
                .union()
                .select(x("faa").as("toAirport"))
                .from(i(bucket.name()))
                .where(x("airportname").eq(s(to)));

        QueryResult result = bucket.query(Query.simple(query));

        if (!result.finalSuccess()) {
            throw new DataRetrievalFailureException("Query error: " + result.errors());
        }

        String fromAirport = null;
        String toAirport = null;
        for (QueryRow row : result) {
            if (row.value().containsKey("fromAirport")) {
                fromAirport = row.value().getString("fromAirport");
            }
            if (row.value().containsKey("toAirport")) {
                toAirport = row.value().getString("toAirport");
            }
        }

        Statement joinQuery = select("a.name", "s.flight", "s.utc", "r.sourceairport", "r.destinationairport", "r.equipment")
                .from(i(bucket.name()).as("r"))
                .unnest("r.schedule AS s")
                .join(i(bucket.name()).as("a") + " ON KEYS r.airlineid")
                .where(x("r.sourceairport").eq(s(fromAirport)).and(x("r.destinationairport").eq(s(toAirport))).and(x("s.day").eq(leave.get(Calendar.DAY_OF_MONTH))))
                .orderBy(Sort.asc("a.name"));

        QueryResult otherResult = bucket.query(joinQuery);
        return extractResultOrThrow(otherResult);
    }

    public static ResponseEntity<String> login(final Bucket bucket, final String username, final String password) {
        JsonDocument doc = bucket.get("user::" + username);
        JsonObject responseContent;
        if(BCrypt.checkpw(password, doc.content().getString("password"))) {
            responseContent = JsonObject.create().put("success", true).put("data", doc.content());
        } else {
            responseContent = JsonObject.empty().put("success", false).put("failure", "Bad Username or Password");
        }
        return new ResponseEntity<String>(responseContent.toString(), HttpStatus.OK);
    }

    public static ResponseEntity<String> createLogin(final Bucket bucket, final String username, final String password) {
        JsonObject data = JsonObject.create()
                .put("type", "user")
                .put("name", username)
                .put("password", BCrypt.hashpw(password, BCrypt.gensalt()));
        JsonDocument doc = JsonDocument.create("user::" + username, data);

        try {
            bucket.insert(doc);
            JsonObject responseData = JsonObject.create()
                    .put("success", true)
                    .put("data", data);
            return new ResponseEntity<String>(responseData.toString(), HttpStatus.OK);
        } catch (Exception e) {
            JsonObject responseData = JsonObject.empty()
                    .put("success", false)
                    .put("failure", "There was an error creating account")
                    .put("exception", e.getMessage());
            return new ResponseEntity<String>(responseData.toString(), HttpStatus.OK);
        }
    }

    private static List<Map<String, Object>> extractResultOrThrow(QueryResult result) {
        if (!result.finalSuccess()) {
            throw new DataRetrievalFailureException("Query error: " + result.errors());
        }
        List<Map<String, Object>> content = new ArrayList<Map<String, Object>>();
        for (QueryRow row : result) {
            content.add(row.value().toMap());
        }
        return content;
    }

}