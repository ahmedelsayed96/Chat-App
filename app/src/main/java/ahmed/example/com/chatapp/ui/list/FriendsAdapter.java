package ahmed.example.com.chatapp.ui.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ahmed.example.com.chatapp.GlideApp;
import ahmed.example.com.chatapp.models.RoomData;
import ahmed.example.com.chatapp.R;

/**
 * Created by root on 04/10/17.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

    private Context context;
    private List<RoomData> roomDataLis;
    private onItemClickListener mOnItemClickListener;

    public FriendsAdapter(Context context,
                          List<RoomData> roomDataLis,
                          onItemClickListener mOnItemClickListener
    ) {
        this.context = context;
        this.roomDataLis = roomDataLis;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.friend_row,
                                                                     parent,
                                                                     false
        ));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bindView(position);

    }

    @Override
    public int getItemCount() {
        return roomDataLis.size();
    }

    interface onItemClickListener {

        void OnItemClickListener(int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView lastMessage;
        private TextView friendName;
        private ImageView friendPic;

        private MyViewHolder(View itemView) {
            super(itemView);
            friendPic = itemView.findViewById(R.id.friend_image);
            friendName = itemView.findViewById(R.id.friend_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            itemView.setOnClickListener(this);
        }

        private void bindView(int position) {
            RoomData roomData = roomDataLis.get(position);

            Log.e("Name", roomData.getFriendData().getName());
            friendName.setText(roomData.getFriendData().getName());
            lastMessage.setText(roomData.getLastMessage());


            GlideApp.with(context)
                    .load(roomData.getFriendData().getImage())
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .apply(RequestOptions.circleCropTransform())
                    .into(friendPic);

        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.OnItemClickListener(getAdapterPosition());
        }
    }
}
