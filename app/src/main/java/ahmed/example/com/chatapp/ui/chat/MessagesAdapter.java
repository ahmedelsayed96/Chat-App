package ahmed.example.com.chatapp.ui.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ahmed.example.com.chatapp.GlideApp;
import ahmed.example.com.chatapp.models.MessageData;
import ahmed.example.com.chatapp.models.UserData;
import ahmed.example.com.chatapp.R;

/**
 * Created by root on 05/10/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessageData> messages;
    private Context context;
    private UserData myUserData, friendUserData;

    public MessagesAdapter(List<MessageData> messages, Context context, String name) {
        this.messages = messages;
        this.context = context;
    }

    public void setMembersData(UserData myUserData, UserData friendUserData) {
        this.myUserData = myUserData;
        this.friendUserData = friendUserData;

    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getAuthor().equals(myUserData.getUid())) {
            return 1;
        }
        return 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_row_me,
                                                                         parent,
                                                                         false
            ));
        }
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_row,
                                                                     parent,
                                                                     false
        ));

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView author, messageTextView;
        ImageView authorImage, image;

        public MyViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.message_author);
            image = itemView.findViewById(R.id.message_image);
            authorImage = itemView.findViewById(R.id.message_author_image);
            messageTextView = itemView.findViewById(R.id.message_text);
        }

        void bind(int i) {
            MessageData message = messages.get(i);


            switch (message.getType()) {
                case "text":
                    image.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(message.getMessage());
                    break;
                case "image":
                    image.setVisibility(View.VISIBLE);
                    messageTextView.setVisibility(View.GONE);

                    GlideApp.with(context)
                            .load(message.getImage())
                            .placeholder(R.color.page)
                            .error(R.color.red)
                            .into(image);
                    break;
            }

            if (author != null) author.setText(friendUserData.getName());
            if (authorImage != null)
                GlideApp.with(context)
                        .load(friendUserData.getImage())
                        .placeholder(R.drawable.user_pic)
                        .error(R.drawable.user_pic)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.authorImage);


        }
    }
}
