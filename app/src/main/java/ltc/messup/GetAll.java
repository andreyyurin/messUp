package ltc.messup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 16.08.2017.
 */

public class GetAll extends AppCompatActivity {

    private ActionProcessButton btnGo;
    private DiscreteSeekBar seekBar;
    private int value;
    private TextView textSeek;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getall_main);

        seekBar = (DiscreteSeekBar) findViewById(R.id.seekbar);
        btnGo = (ActionProcessButton) findViewById(R.id.btnGo);
        btnGo.setMode(ActionProcessButton.Mode.ENDLESS);

        value = seekBar.getProgress();
        textSeek = (TextView) findViewById(R.id.textSeek);
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
                VKRequest request = VKApi.groups().search(VKParameters.from(VKApiConst.Q, "бот", VKApiConst.COUNT, value));
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
