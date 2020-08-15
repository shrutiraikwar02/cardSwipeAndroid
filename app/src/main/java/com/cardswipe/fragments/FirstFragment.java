package com.cardswipe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cardswipe.MyApplication;
import com.cardswipe.R;
import com.cardswipe.adapters.CardListAdapter;
import com.cardswipe.models.UserData;
import com.cardswipe.swipeLib.cardstack.CardStack;
import com.cardswipe.webservices.APICallback;
import com.cardswipe.webservices.APICalling;
import com.cardswipe.webservices.RestAPI;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

import static com.cardswipe.R.string.get_result;

public class FirstFragment extends Fragment implements APICallback {

    private Context mContext;
    private CardStack mCardStack;
    private MyApplication app;
    private RestAPI restAPI;
    protected APICalling apiCalling;
    private Gson gson;
    private int resultLimit = 10;
    private int pageNo = 0;
    private List<UserData> usersData;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        app = (MyApplication) getActivity().getApplicationContext();
        app.setCurrentActivity(getActivity());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        initAction(view);
    }

    /*
     * Method is use to init the variables
     * */
    private void init(View view) {
        mContext = getContext();
        mCardStack = (CardStack) view.findViewById(R.id.swipeView);
        mCardStack.setContentResource(R.layout.view_shaadi_card);
        mCardStack.setStackMargin(20);
        mCardStack.setGravity(Gravity.TOP);

        restAPI = APICalling.webServiceInterface();
        app.setApiCallback(this);
        apiCalling = new APICalling(getContext());
        gson = new Gson();
    }

    /*
     * Method is use to perform action on init the variables
     * */
    private void initAction(View view) {
        callAPIToGetData();
    }

    /*
     * Method is use to get data from server
     * */
    private void callAPIToGetData() {
       /* HashMap<String, Object> values = apiCalling.getHashMapObject(
                "Result", resultLimit);
        Log.v("TAG", "getNotificationSetting Request :- " + values);
*/
        try {
            Call<JsonElement> call = restAPI.getResult((resultLimit));
            if (apiCalling != null) {
                apiCalling.callAPI(app, call, getString(get_result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setList() {
        CardListAdapter mCardAdapter = new CardListAdapter(getContext(), mCardStack, app);
        for (UserData user : usersData) {
            mCardAdapter.add(user);
        }

        mCardStack.setAdapter(mCardAdapter);
    }

    private void saveDataIntoDB() {
        //before add data to db remove old data to manage latest data of user
        app.getDb().deleteUser();
        for (UserData user : usersData) {
            app.getDb().addUser(user);
        }
    }

    @Override
    public void apiCallback(JsonObject json, String from) {
        if (from != null) {
            if (json != null) {
                if (from.equals(getString(get_result))) {
                    usersData = (List<UserData>) apiCalling.getDataList(json, "results", UserData.class);
                    if (usersData != null && !usersData.isEmpty()) {
                        setList();
                        saveDataIntoDB();
                    }
                }
            }else {
                usersData = app.getDb().getUsers();
                app.getDb().getAcceptedUsers();
                app.getDb().getDeclinedUsers();
                app.getDb().getUnTouchedUsers();
                setList();
            }
        }

    }
}