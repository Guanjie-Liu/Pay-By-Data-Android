package com.example.jack.couchbaseevents;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.SavedRevision;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String DB_NAME = "couchbaseevents";
    public static final String TAG = "couchbaseevents";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Begin Couchbase Events App");

        helloCBL();

        Log.d(TAG, "End Couchbase Events App");

    }

    private void helloCBL() {
        Manager manager = null;
        Database database = null;

        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            Log.d(TAG, "Manager created");
            database = manager.getDatabase(DB_NAME);
            Log.d(TAG, "database is retrieved");
        } catch (Exception e) {
            Log.e(TAG, "Error getting database", e);
            return;
        }

        try {
            startReplications(database);
        }catch(CouchbaseLiteException e){
            Log.e(TAG, "Error on starting Replications", e);
        }
        // Create the document
        String documentId = createDocument(database);
    /* Get and output the contents */
        outputContents(database, documentId);
    /* Update the document and add an attachment */
        updateDoc(database, documentId);
        // Add an attachment
        addAttachment(database, documentId);
    /* Get and output the contents with the attachment */
        outputContentsWithAttachment(database, documentId);
/*
        // delete the document
        try {
            Document retrievedDocument = database.getDocument(documentId);
            retrievedDocument.delete();
            Log.d (TAG, "Deleted document, deletion status = " + retrievedDocument.isDeleted());
        } catch (CouchbaseLiteException e) {
            Log.e (TAG, "Cannot delete document", e);
        }
*/
    }

    private String createDocument(Database database) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "Big Party");
        map.put("location", "My House");
        try {
            // Save the properties to the document
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return documentId;
    }

    private void outputContents(Database database, String documentId){
        // retrieve the document from the database
        Document retrievedDocument = database.getDocument(documentId);
        // display the retrieved document
        Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
    }

    private void updateDoc(Database database, String documentId) {
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data
            Map<String, Object> updatedProperties = new HashMap<String, Object>();
            updatedProperties.putAll(document.getProperties());
            updatedProperties.put("eventDescription", "Everyone is invited!");
            updatedProperties.put("address", "123 Elm St.");
            // Save to the Couchbase local Couchbase Lite DB
            document.putProperties(updatedProperties);
            Log.d(TAG, "Document updated");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
    }

    private void addAttachment(Database database, String documentId) {
        Document document = database.getDocument(documentId);
        try {
        /* Add an attachment with sample data as POC */
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[] { 0, 0, 0, 0 });
            UnsavedRevision revision = document.getCurrentRevision().createRevision();
            revision.setAttachment("binaryData", "application/octet-stream", inputStream);
        /* Save doc & attachment to the local DB */
            revision.save();
            Log.d(TAG, "Attachment has been added");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
    }

    private void outputContentsWithAttachment(Database database, String documentId){
        Document fetchedSameDoc = database.getExistingDocument(documentId);
        SavedRevision saved = fetchedSameDoc.getCurrentRevision();
        // The content of the attachment is a byte[] we created
        Attachment attach = saved.getAttachment("binaryData");
        int i = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(attach.getContent()));
        }catch(CouchbaseLiteException e){
            Log.e(TAG, "Error on wraping inpuStreamReader", e);
        }

            StringBuffer values = new StringBuffer();
        try {
            while (i++ < 4) {
                // We knew the size of the byte array
                // This is the content of the attachment
                values.append(reader.read() + " ");
            }
        }catch(IOException e){
            Log.e(TAG, "Error in reading from StringBuffer", e);
        }

        Log.v("LaurentActivity", "The docID: " + documentId + ", attachment contents was: " + values.toString());
    }

    private URL createSyncURL(boolean isEncrypted){
        URL syncURL = null;
        String host = "http://10.0.2.2";
        String port = "4984";
        String dbName = "couchbaseevents";
        try {
            syncURL = new URL(host + ":" + port + "/" + dbName);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }
        return syncURL;
    }

    private void startReplications(Database database) throws CouchbaseLiteException {
        Log.d(TAG, "Start Replications");
        Replication pull = database.createPullReplication(createSyncURL(false));
        Replication push = database.createPushReplication(createSyncURL(false));
        pull.setContinuous(true);
        push.setContinuous(true);
        pull.start();
        push.start();
    }

}
