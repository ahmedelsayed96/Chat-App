package ahmed.example.com.chatapp.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;

import java.util.ArrayList;
import java.util.List;

import ahmed.example.com.chatapp.GlideApp;
import ahmed.example.com.chatapp.models.RoomData;
import ahmed.example.com.chatapp.R;


/**
 * Created by root on 22/09/17.
 */

public class WidgetServiceIntent extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        Log.e("size",intent.getExtras().getParcelableArrayList(MyIntentService.ROOMS).size()+"");
        return new WidgetRemoteFactory(getApplicationContext(), intent);
    }

    class WidgetRemoteFactory implements RemoteViewsFactory {

        List<RoomData> roomDatas = new ArrayList<>();
        private Context mContext;


        public WidgetRemoteFactory(Context mContext, Intent intent) {
            roomDatas = MyIntentService.roomDatas;

            Log.e("size:", roomDatas.size() + "");
            this.mContext = mContext;
        }

        @Override
        public void onCreate() {
            getData();
        }

        @Override
        public void onDataSetChanged() {
            getData();

        }

        void getData() {
//
        }

        @Override
        public void onDestroy() {


        }

        @Override
        public int getCount() {
            return roomDatas.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            if (roomDatas.size() == 0) return null;
            final RemoteViews remoteViews = new RemoteViews(WidgetServiceIntent.this.getPackageName(),
                                                            R.layout.friend_row_widget
            );
            final RoomData roomData = roomDatas.get(i);

            remoteViews.setTextViewText(R.id.friend_name, roomData.getFriendData().getName());
            remoteViews.setTextViewText(R.id.last_message, roomData.getLastMessage());


            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    AppWidgetTarget appWidgetTarget = new AppWidgetTarget(WidgetServiceIntent.this,
                                                                          R.id.friend_image,
                                                                          remoteViews,
                                                                          new ComponentName(mContext,
                                                                                            ChatAppWidget.class
                                                                          )
                    );

                    GlideApp.with(WidgetServiceIntent.this)
                            .asBitmap()
                            .load(roomData.getFriendData().getImage())
                            .placeholder(R.drawable.user_pic)
                            .error(R.drawable.user_pic)
                            .apply(RequestOptions.circleCropTransform())
                            .into(appWidgetTarget);
                }
            });


            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}