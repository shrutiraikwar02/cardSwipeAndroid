package com.cardswipe.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cardswipe.MyApplication;
import com.cardswipe.R;
import com.cardswipe.database.DBHelper;
import com.cardswipe.models.UserData;
import com.cardswipe.swipeLib.cardstack.CardStack;
import com.cardswipe.utils.CircleTransform;

import static android.view.View.VISIBLE;
import static com.cardswipe.swipeLib.cardstack.CardUtils.DIRECTION_BOTTOM_LEFT;
import static com.cardswipe.swipeLib.cardstack.CardUtils.DIRECTION_BOTTOM_RIGHT;
import static com.cardswipe.swipeLib.cardstack.CardUtils.DIRECTION_TOP_RIGHT;

public class CardListAdapter extends ArrayAdapter<UserData> implements CardStack.CardEventListener, View.OnClickListener {

    private Context mContext;
    private View ParentView;
    private CardStack mCardStack;
    private TextView tvName,cardStatus;
    private ImageView imAvatar;
    private int pos;
    private RelativeLayout accept, reject;
    private LinearLayout acceptRejectView;
    private MyApplication app;
    private DBHelper db;


    public CardListAdapter(Context context, CardStack cardStack, MyApplication application) {
        super(context, R.layout.view_shaadi_card);
        mContext = context;
        mCardStack = cardStack;
        app = application;
        mCardStack.setListener(this);
        db = app.getDb();
    }


    @Override
    public View getView(int position, final View contentView, ViewGroup parent) {
        ParentView = contentView;
        tvName = (TextView) (contentView.findViewById(R.id.tvName));
        cardStatus = (TextView) (contentView.findViewById(R.id.cardStatus));
        imAvatar = (ImageView) (contentView.findViewById(R.id.userIMG));
        accept = contentView.findViewById(R.id.acceptView);
        reject = contentView.findViewById(R.id.rejectView);
        acceptRejectView = contentView.findViewById(R.id.linearLayout4);
        LinearLayout declinedView = contentView.findViewById(R.id.declineCard);
        LinearLayout acceptView = contentView.findViewById(R.id.acceptCard);
        acceptView.setOnClickListener(this);
        declinedView.setOnClickListener(this);

        setViewContent(position, getItem(position), contentView);
        return contentView;
    }

    @Override
    public boolean swipeEnd(int section, float distance, int currentPosition, View cardView) {
        Log.d("rae", "swipeEnd  " + currentPosition);
        if (accept != null) {
            accept = cardView.findViewById(R.id.acceptView);
            accept.setVisibility(View.GONE);
        }
        if (reject != null) {
            reject = cardView.findViewById(R.id.rejectView);
            reject.setVisibility(View.GONE);
        }
        if (distance < 450) {
            return false;
        }
        return true;
    }

    @Override
    public boolean swipeStart(int section, float distance, int currentPosition, View cardView) {
        Log.d("rae", "swipeStart  " + currentPosition);
        if (section == DIRECTION_BOTTOM_RIGHT || section == DIRECTION_TOP_RIGHT) {
            if (accept != null) {
                accept = cardView.findViewById(R.id.acceptView);
                accept.setVisibility(VISIBLE);
            }
        } else {
            if (reject != null) {
                reject = cardView.findViewById(R.id.rejectView);
                reject.setVisibility(VISIBLE);
            }
        }

        return true;
    }

    @Override
    public boolean swipeContinue(int section, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void discarded(int mIndex, int direction) {
        Log.d("rae", "discarded  " + mIndex);
    }

    @Override
    public void topCardTapped(int position) {
        pos = position;
        Log.d("rae", "topCardTapped  " + position);

    }

    @Override
    public void onClick(View view) {
        pos++;
        switch (view.getId()) {
            case R.id.acceptCard:
                mCardStack.discardTop(DIRECTION_BOTTOM_RIGHT);
                Toast.makeText(mContext, "Member Accepted " + pos, Toast.LENGTH_SHORT).show();
                UserData user = getItem(pos-1);
                user.setCardStatus(mContext.getResources().getString(R.string.accept));
                app.getDb().addUser(user);
                break;
            case R.id.declineCard:
                mCardStack.discardTop(DIRECTION_BOTTOM_LEFT);
                Toast.makeText(mContext, "Member Declined " + pos, Toast.LENGTH_SHORT).show();
                UserData user1 = getItem(pos-1);
                user1.setCardStatus(mContext.getResources().getString(R.string.decline));
                app.getDb().addUser(user1);
                break;
        }
    }

    private void setViewContent(int position, UserData userData, View view) {
        Log.d("Adapter position =",""+position+" status="+ userData.getCardStatus());
        String name = "";
        if (userData != null) {
            if (userData.getName() != null) {
                if (userData.getName().getTitle() != null)
                    name = userData.getName().getTitle();
                if (userData.getName().getFirst() != null)
                    name += " " + userData.getName().getFirst();
                if (userData.getName().getLast() != null)
                    name += " " + userData.getName().getLast();
                tvName.setText(name);
            }

            if(userData.getPicture()!=null){
                if(userData.getPicture().getThumbnail()!=null){
                    Glide.with(mContext).load(userData.getPicture().getThumbnail()).transform(new CircleTransform(mContext)).into(imAvatar);
                }
            }

            if(userData.getCardStatus().isEmpty()){
                acceptRejectView.setVisibility(VISIBLE);
                cardStatus.setVisibility(View.GONE);
            }else {
                acceptRejectView.setVisibility(View.GONE);
                cardStatus.setVisibility(VISIBLE);
                String msg = mContext.getResources().getString(R.string.card_status_msg)+userData.getCardStatus();
                cardStatus.setText(msg);
            }
        }
    }
}
