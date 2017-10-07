package ltc.messup.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

import ltc.messup.R;

/**
 * Created by admin on 30.09.2017.
 */

public class FirstPage extends Fragment {
    int pageNumber;
    int backColor;

    public static FirstPage newInstance(int page) {
        FirstPage pageFragment = new FirstPage();
        Bundle arguments = new Bundle();
        arguments.putInt("page", page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt("page");

        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page1, null);

        return view;
    }
}
