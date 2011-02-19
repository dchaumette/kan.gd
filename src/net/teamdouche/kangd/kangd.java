/**
 * Teamdouche
 */
package net.teamdouche.kangd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * kan.gd app
 * 
 * @author optedoblivion
 */
public class kangd extends Activity {

    private static final String TAG = "Kan.gd";
    /**
     * herp derp
     */
    private HttpClient client;
    private HttpPost request;
    private List<NameValuePair> postParams;
    private UrlEncodedFormEntity formEntity;
    private HttpResponse response;
    private BufferedReader in;
    private ClipboardManager clipboardManager;
    private Button shortener;
    private Button resetter;
    private EditText longUrlEditor;
    private Context mContext;

    private final String extraKey = "android.intent.extra.TEXT";
    
    /** 
     * onCreate br0 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clipboardManager = (ClipboardManager) 
                                           getSystemService(CLIPBOARD_SERVICE);
        if(getIntent().hasExtra(extraKey)){
            Bundle extras = getIntent().getExtras();
            if(extras.containsKey(extraKey)){
                String longUrl = extras.getString(extraKey);
                if (!verifyIsUrl(longUrl)){
                    Toast.makeText(this, "Invalid URL!",
                                                    Toast.LENGTH_SHORT).show();
                    finish();
                }
                String shortUrl = shorten(longUrl);
                if (shortUrl != null){
                    clipboardManager.setText(shortUrl);
                    Toast.makeText(this, "URL Copied to clipboard!",
                                                    Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error Occurred!",
                                                    Toast.LENGTH_SHORT).show();
                }
                finish();
            } 
        } else {
            mContext = this;
            setContentView(R.layout.main);
            shortener = (Button) findViewById(R.id.create_btn);
            resetter = (Button) findViewById(R.id.reset_btn);
            longUrlEditor = (EditText) findViewById(R.id.long_url);
            shortener.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View arg0) {
                    String longUrl = longUrlEditor.getText().toString();
                    if (!verifyIsUrl(longUrl)){
                        Toast.makeText(mContext, "Invalid URL!",
                                                    Toast.LENGTH_SHORT).show();
                    } else {
                        String shortUrl = shorten(longUrl);
                        clipboardManager.setText(shortUrl);
                        Toast.makeText(mContext, "URL Copied to clipboard!",
                                                    Toast.LENGTH_SHORT).show();
                        longUrlEditor.setText(shortUrl);
                        longUrlEditor.setEnabled(false);
                    }
                }
            });
            resetter.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View arg0) {
                    longUrlEditor.setText("");
                    longUrlEditor.setEnabled(true);
                }
            });
        }
    }

    /**
     * 
     * Derp Shizzle
     * 
     * @param url
     * @return <code> true </code> if url <code> false </code> if not
     */
    public boolean verifyIsUrl(String url){
        try{
            URL u = new URL(url);
            return true;
        } catch (MalformedURLException e){
            Log.e(TAG, e.toString());
            return false;
        }
        
    }

    /**
     * 
     * herp teh url
     * 
     * @param longUrl
     * @return {@link java.lang.String}
     */
    public String shorten(String longUrl){
        try{
            client = new DefaultHttpClient();
            request = new HttpPost("http://kan.gd/api");
            postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("url", longUrl));
            formEntity = new UrlEncodedFormEntity(postParams);
            request.setEntity(formEntity);
            response = client.execute(request);
            in = new BufferedReader(
                     new InputStreamReader(response.getEntity().getContent()));
            String shortUrl = in.readLine();
            in.close();
            return shortUrl;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}