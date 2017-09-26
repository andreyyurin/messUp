package ltc.messup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by admin on 16.08.2017.
 */

public class GetAll extends AppCompatActivity implements RewardedVideoAdListener {

    private ActionProcessButton btnGo;
    private DiscreteSeekBar seekBar;
    private int value;
    private Toolbar toolbar;
    private CircleImageView profileImg;
    private FloatingActionButton fab;
    private TextView textSeek;
    private AdView mAdView;
    private static final String APP_ID = "ca-app-pub-8154277548860310~8692901258";
    private RewardedVideoAd mAd;
    private TextView textName, textCoins;
    private Integer messes;
    private String link = "https://andreyyurin55.000webhostapp.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getall_main);

        MobileAds.initialize(getApplicationContext(), APP_ID);

        textName = (TextView) findViewById(R.id.textName);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekbar);
        btnGo = (ActionProcessButton) findViewById(R.id.btnGo);
        btnGo.setMode(ActionProcessButton.Mode.ENDLESS);
        profileImg = (CircleImageView) findViewById(R.id.profile_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textSeek = (TextView) findViewById(R.id.textSeek);
        textCoins = (TextView) findViewById(R.id.textCoins);

        fab.setBackgroundColor(getResources().getColor(R.color.toolbar_color));

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        //loadRewardedVideoAd();

        setSupportActionBar(toolbar);
        seekBar.setProgress(1);
        value = seekBar.getProgress();

        textSeek.setText(String.valueOf(value));

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //coins = Integer.parseInt(response);
                       // textCoins.setText(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            textCoins.setText(object.getString("coins"));
                            messes = object.getInt("mess");
                            if(object.getInt("coins")>3){
                                seekBar.setMax(15);
                                btnGo.setEnabled(true);
                            }else{
                                if(object.getInt("coins")<=1){
                                    seekBar.setMax(1);
                                    seekBar.setMin(1);
                                    btnGo.setEnabled(false);
                                }else {
                                    seekBar.setMax((int) Math.floor(object.getDouble("coins") * 5));
                                    btnGo.setEnabled(true);
                                }
                            }
                            Log.d("xyi", String.valueOf(messes));
                        }catch (JSONException error){

                        }

                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                       // Log.d("Error.Response", response);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(getMyId()));

                return params;
            }
        };
        queue.add(postRequest);

        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_big"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                String urlImage = user.photo_big;
                Log.d("xyi", urlImage);
                Picasso
                        .with(getApplicationContext())
                        .load(urlImage)
                        .into(profileImg);
            }
            @Override
            public void onError(VKError error) {}
            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {}
        });


        request = new VKRequest("account.getProfileInfo");

        request.executeWithListener(new VKRequest.VKRequestListener()
        {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                String status = "";

                try {

                    JSONObject jsonObject = response.json.getJSONObject("response");

                    String first_name = jsonObject.getString("first_name");
                    String last_name = jsonObject.getString("last_name");
                    String screen_name = jsonObject.getString("screen_name");
                    status = jsonObject.getString("status");
                    textName.setText(first_name+" "+last_name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRewardedVideoAd();
            }
        });

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                textSeek.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGo.setProgress(1);

                RequestQueue queue = Volley.newRequestQueue(btnGo.getContext());
                StringRequest postRequest = new StringRequest(Request.Method.POST, link,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                //coins = Integer.parseInt(response);
                                // textCoins.setText(response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    textCoins.setText(object.getString("coins"));
                                    messes = object.getInt("mess");
                                    if(object.getInt("coins")>3){
                                        seekBar.setMax(15);
                                    }else{
                                        if(object.getInt("coins")<=1){
                                            seekBar.setMax(1);
                                            seekBar.setMin(1);
                                            btnGo.setEnabled(false);
                                        }else {
                                            seekBar.setMax((int) Math.floor(object.getDouble("coins") * 5));
                                            btnGo.setEnabled(true);
                                        }
                                    }
                                    Log.d("xyi", String.valueOf(messes));
                                }catch (JSONException error){

                                }

                                Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                // Log.d("Error.Response", response);
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id", String.valueOf(getMyId()));
                        params.put("count", String.valueOf(value));

                        return params;
                    }
                };
                queue.add(postRequest);

                VKRequest request = VKApi.groups().search(VKParameters.from(VKApiConst.Q, "бот", VKApiConst.COUNT, value, VKApiConst.OFFSET, messes));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                    super.onComplete(response);


                        ArrayList<String> messes=new ArrayList<String>();
                        Log.d("xyi", response.responseString);
                        JSONObject object = response.json;
                        try {
                            object = object.getJSONObject("response");
                            JSONArray array = object.getJSONArray("items");
                            Log.d("xyi", String.valueOf(array.length()));
                            for(int i = 0; i<value; i++){
                                JSONObject obj = array.getJSONObject(i);
                                VKRequest request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID,
                                        "-"+obj.getString("id"), VKApiConst.MESSAGE, "Привет"));
                                request.executeWithListener(new VKRequest.VKRequestListener() {
                                    @Override
                                    public void onComplete(VKResponse response) {
                                        super.onComplete(response);
                                        Log.d("ANSWER", "УСПЕХ");
                                        btnGo.setProgress(0);
                                    }

                                    @Override
                                    public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                                        super.onProgress(progressType, bytesLoaded, bytesTotal);
                                        btnGo.setProgress(btnGo.getProgress()+1);
                                    }
                                });
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-8154277548860310/7660032948", new AdRequest.Builder()
                .addTestDevice("4CC1636AE73A6B5D941CFBC28769E6E9")
                .build());
        mAd.setRewardedVideoAdListener(this);
    }


    @Override
    public void onRewardedVideoAdLoaded() {
        mAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //coins = Integer.parseInt(response);
                        // textCoins.setText(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            textCoins.setText(object.getString("coins"));
                            messes = object.getInt("mess");
                            if(object.getInt("coins")>3){
                                seekBar.setMax(15);
                                btnGo.setEnabled(true);
                            }else{
                                if(object.getInt("coins")<=1){
                                    seekBar.setMax(1);
                                    seekBar.setMin(1);
                                    btnGo.setEnabled(false);
                                }else {
                                    seekBar.setMax((int) Math.floor(object.getDouble("coins") * 5));
                                    btnGo.setEnabled(true);
                                }
                            }
                            Log.d("xyi", String.valueOf(messes));
                        }catch (JSONException error){

                        }

                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        // Log.d("Error.Response", response);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(getMyId()));
                params.put("reward", "true");

                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    int getMyId() {
        final VKAccessToken vkAccessToken = VKAccessToken.currentToken();
        return vkAccessToken != null ? Integer.parseInt(vkAccessToken.userId) : 0;
    }
}
