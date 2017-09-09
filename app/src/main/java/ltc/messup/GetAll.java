package ltc.messup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 16.08.2017.
 */

public class GetAll extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getall_main);

        VKRequest request = VKApi.groups().search(VKParameters.from(VKApiConst.Q, "бот", VKApiConst.COUNT, 999));
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
                    for(int i = 0; i<array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        VKRequest request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID,
                                "-"+obj.getString("id"), VKApiConst.MESSAGE, "Привет"));
                        request.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                Log.d("ANSWER", "УСПЕХ");
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
       /* for(int i = ids.length-1; i>=0; i--) {
            VKRequest request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID,
                    ids[i], VKApiConst.MESSAGE, "messUp"));
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    Log.d("ANSWER", "УСПЕХ");
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
}
