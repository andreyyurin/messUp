package ltc.messup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.CircularProgressButton;
import com.dd.morphingbutton.MorphingButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import ltc.messup.fragments.FirstPage;
import ltc.messup.fragments.SecondPage;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by admin on 16.08.2017.
 */

public class GetAll extends AppCompatActivity implements RewardedVideoAdListener {

    //private ActionProcessButton btnGo;
    private CircularProgressButton btnGo;
    private DiscreteSeekBar seekBar;
    private int value;
    private Toolbar toolbar;
    private CircleImageView profileImg;
    private TextView textSeek;// txtVersion;
    private AdView mAdView;
    private static final String APP_ID = "ca-app-pub-8154277548860310~8692901258";
    private RewardedVideoAd mAd;
    private TextView textName, textCoins;
    private Integer messes;
    private String link = "https://andreyyurin55.000webhostapp.com/";
    private AlertDialog dialog;
    public static VKResponse responseTemp;
    private BoomMenuButton bmb;
    private ViewPager viewPager;
    private Locale mLocale;
    private SharedPreferences sharedPreferences;
    private CircleIndicator indicator;
    private LinearLayout view;
//    private ImageButton vkBtn, instBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Context context = getApplicationContext();
        sharedPreferences = getPreferences(context.MODE_PRIVATE);
        String locale = sharedPreferences.getString("lang","");
        if(!locale.equals("ru") && !locale.equals("en")){
            setLocale("default");
        }else if(locale.equals("ru")){
            setLocale("ru");
        }else {
            setLocale("en");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getall_main);


        MobileAds.initialize(getApplicationContext(), APP_ID);

        responseTemp = new VKResponse();
        textName = (TextView) findViewById(R.id.textName);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekbar);
        btnGo = (CircularProgressButton) findViewById(R.id.btnGo);
        //btnGo.setMode(ActionProcessButton.Mode.ENDLESS);
        profileImg = (CircleImageView) findViewById(R.id.profile_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textSeek = (TextView) findViewById(R.id.textSeek);
        textCoins = (TextView) findViewById(R.id.textCoins);
        viewPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        dialog = new SpotsDialog(this);

        bmb = (BoomMenuButton) findViewById(R.id.bmb);

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        //loadRewardedVideoAd();

        setSupportActionBar(toolbar);
        seekBar.setProgress(1);
        btnGo.setProgress(0);
        value = seekBar.getProgress();

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        textSeek.setText(String.valueOf(value));
        indicator.setViewPager(viewPager);
        (new MyPagerAdapter(getSupportFragmentManager())).registerDataSetObserver(indicator.getDataSetObserver());

       // btnGo.getBackground().setColorFilter(new LightingColorFilter(0x000000,getResources().getColor(R.color.toolbar_color)));
        //for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
       /* for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_done);
            bmb.addBuilder(builder);
        }*/

        view = (LinearLayout) getLayoutInflater().inflate(R.layout.alert_view, null);
        final ImageButton vkBtn = (ImageButton) view.findViewById(R.id.vk_btn);
        final ImageButton instBtn = (ImageButton) view.findViewById(R.id.inst_btn);
        final TextView txtVersion = (TextView) view.findViewById(R.id.version_txt);

        try {
            txtVersion.setText(txtVersion.getText()+getPackageManager().getPackageInfo(getPackageName(), 0).versionName.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        vkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_vk)));
                startActivity(browserIntent);
            }
        });

        instBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_inst)));
                startActivity(browserIntent);
            }
        });

        SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.icwatch)
                .rippleEffect(true)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        loadRewardedVideoAd();
                    }
                })
                .shadowEffect(true)
                .normalColor(getResources().getColor(R.color.menu_item1))
                .pieceColor(getResources().getColor(R.color.menu_item1piece));
        bmb.addBuilder(builder);

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GetAll.this);
        alertBuilder.setTitle("")
                .setCancelable(false)
                .setView(view)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        final android.app.AlertDialog alertDialog = alertBuilder.create();
        SimpleCircleButton.Builder builder1 = new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_info)
                .rippleEffect(true)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        alertDialog.show();
                    }
                })
                .shadowEffect(true)
                .normalColor(getResources().getColor(R.color.menu_item2))
                .pieceColor(getResources().getColor(R.color.menu_item2piece));
        bmb.addBuilder(builder1);
        SimpleCircleButton.Builder builder2 = new SimpleCircleButton.Builder();
        if (getLocale().equals("ru")) {
           builder2
                    .normalImageRes(R.drawable.ru_icon)
                    .rippleEffect(true)
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            if(Objects.equals(getLocale(), "ru")){
                                setLocale("en");
                            }else{
                                setLocale("ru");
                            }
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .shadowEffect(true)
                    .normalColor(getResources().getColor(R.color.menu_item4))
                    .pieceColor(getResources().getColor(R.color.menu_item4piece));
        }else{
            builder2
                    .normalImageRes(R.drawable.en_icon)
                    .rippleEffect(true)
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            if(Objects.equals(getLocale(), "ru")){
                                setLocale("en");
                            }else{
                                setLocale("ru");
                            }
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .shadowEffect(true)
                    .normalColor(getResources().getColor(R.color.menu_item4))
                    .pieceColor(getResources().getColor(R.color.menu_item4piece));
        }
        bmb.addBuilder(builder2);

        SimpleCircleButton.Builder builder3 = new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_exit)
                .rippleEffect(true)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        VKSdk.logout();
                        startActivity(new Intent(GetAll.this, MainActivity.class));
                        finish();
                    }
                })
                .shadowEffect(true)
                .normalColor(getResources().getColor(R.color.menu_item3))
                .pieceColor(getResources().getColor(R.color.menu_item3piece));
        bmb.addBuilder(builder3);



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
               // btnGo.setProgress(seekbar);

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
                btnGo.setClickable(false);
                btnGo.setIndeterminateProgressMode(true); // turn on indeterminate progress
                btnGo.setProgress(50);
                 // set progress > 0 & < 100 to display indeterminate progress
                //
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                VKRequest request = VKApi.groups().search(VKParameters.from(VKApiConst.Q, "бот", VKApiConst.COUNT, value, VKApiConst.OFFSET, messes));
                request.executeWithListener(new VKRequest.VKRequestListener() {

                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        MyAsyncTask mt = new MyAsyncTask();
                        mt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,response);
                    }
                });
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //dialog.show();
//                MyAsyncTask mt = new MyAsyncTask();
//                mt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,response);
                //btnGo.setProgress(seekbar);
                //btnGo.setIndeterminateProgressMode(false);

            }
        });
    }
    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-8154277548860310/7660032948", new AdRequest.Builder()
                .addTestDevice("4CC1636AE73A6B5D941CFBC28769E6E9")
                .build());
        mAd.setRewardedVideoAdListener(this);
        dialog.show();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return FirstPage.newInstance(1);
                case 1: return SecondPage.newInstance(2);
                default: return FirstPage.newInstance(1);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        dialog.dismiss();mAd.show();
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

    private class MyAsyncTask extends AsyncTask<VKResponse, Integer, Integer> {

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // [... Обновите индикатор хода выполнения, уведомления или
            // элемент пользовательского интерфейса ...]
        }


        @Override
        protected Integer doInBackground(VKResponse... parameter) {
            int myProgress = 0;
            // [... Выполните задачу в фоновом режиме, обновите переменную myProgress...]
            Log.d("xyi", parameter[0].responseString);
            JSONObject object = parameter[0].json;
            try {
                object = object.getJSONObject("response");
                JSONArray array = object.getJSONArray("items");
                Log.d("xyi", String.valueOf(array.length()));
                for(int i = 0; i<value; i++){
                    //btnGo.setProgress(btnGo.getProgress()+100/value);
                    JSONObject obj = array.getJSONObject(i);
                    VKRequest request2 = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID,
                            "-"+obj.getString("id"), VKApiConst.MESSAGE, "Привет"));
                    request2.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Log.d("ANSWER", "УСПЕХ");
                        }

                        @Override
                        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                            super.onProgress(progressType, bytesLoaded, bytesTotal);
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
            // [... Продолжение выполнения фоновой задачи ...]
            // Верните значение, ранее переданное в метод onPostExecute
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            btnGo.setProgress(0);
            btnGo.setClickable(true);
            //  dialog.dismiss();
        }
    }

    private String getLocale(){
        String locale = sharedPreferences.getString("lang","");
        return locale;
    }

    private void setLocale(String lang){
        mLocale = new Locale(lang);
        Locale.setDefault(mLocale);
        Configuration configuration = new Configuration();
        configuration.locale = mLocale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        if(Objects.equals(lang, "en")) {
            ed.putString("lang", "en");
        }else if (Objects.equals(lang, "ru")){
            ed.putString("lang", "ru");
        }
        ed.commit();
    }
}
