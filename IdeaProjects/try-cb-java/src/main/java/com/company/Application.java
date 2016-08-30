package com.company;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class Application implements Filter{

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        /*
        System.setProperty("com.couchbase.queryEnabled", "true");
        Cluster cluster = CouchbaseCluster.create();
        Bucket bucket = cluster.openBucket("sync_gateway");
        Query query =
        */
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Value("${hostname}")
    private String hostname;

    @Value("${bucket}")
    private String bucket;

    @Value("${password}")
    private String password;

    public @Bean
    Cluster cluster() {
        return CouchbaseCluster.create(hostname);
    }

    public @Bean
    Bucket bucket() {
        return cluster().openBucket(bucket, password);
    }

    @RequestMapping(value="/user/login", method= RequestMethod.GET)
    public Object login(@RequestParam String user, @RequestParam String password) {
        return Database.login(bucket(), user, password);
    }

    @RequestMapping(value="/user/login", method= RequestMethod.POST)
    public Object createLogin(@RequestBody String json) {
        JsonObject jsonData = JsonObject.fromJson(json);
        return Database.createLogin(bucket(), jsonData.getString("user"), jsonData.getString("password"));
    }

    @RequestMapping("/airport/findAll")
    public List<Map<String, Object>> airports(@RequestParam String search) {
        return Database.findAllAirports(bucket(), search);
    }

    @RequestMapping("/flightPath/findAll")
    public List<Map<String, Object>> all(@RequestParam String from, @RequestParam String to, @RequestParam String leave)
            throws Exception {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(leave));
        return Database.findAllFlightPaths(bucket(), from, to, calendar);
    }
}