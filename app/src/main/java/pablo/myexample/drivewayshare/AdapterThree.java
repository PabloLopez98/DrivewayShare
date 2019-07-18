package pablo.myexample.drivewayshare;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterThree extends RecyclerView.Adapter<AdapterThree.ViewHolder> {

    private Context mCtx;
    private List<MyChat> chatList;

    DatabaseReference databaseReference, databaseReference2, databaseReference3, databaseReference4;
    ValueEventListener valueEventListener, valueEventListener2, valueEventListener3, valueEventListener4;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public AdapterThree(Context mCtx, List<MyChat> chatList) {
        this.mCtx = mCtx;
        this.chatList = chatList;
    }

    @Override
    public AdapterThree.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.chatcardview, null);
        return new AdapterThree.ViewHolder(view, onItemClickListener);
    }


    @Override
    public void onBindViewHolder(final AdapterThree.ViewHolder viewHolder, int i) {
        final MyChat myChat = chatList.get(i);
        viewHolder.chatName.setText(myChat.getChatName());
        Picasso.with(mCtx).load(myChat.getChatImageUrl()).fit().centerCrop().into(viewHolder.imageChatView);
        viewHolder.clientOrNotText.setText(myChat.getClientOrNotText());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView chatName;
        ImageView imageChatView;
        TextView clientOrNotText;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            chatName = itemView.findViewById(R.id.textViewChatName);
            imageChatView = itemView.findViewById(R.id.imageChatView);
            clientOrNotText = itemView.findViewById(R.id.clientOrNotText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
