package pablo.myexample.drivewayshare;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterFour extends RecyclerView.Adapter<AdapterFour.ViewHolder>  {
    private Context mCtx;
    private List<MyConversation> myConversationList;

    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    public AdapterFour(Context mCtx, List<MyConversation> myConversationList) {
        this.mCtx = mCtx;
        this.myConversationList = myConversationList;
    }

    @Override
    public AdapterFour.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.chat, null);
        return new AdapterFour.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final MyConversation myConversation = myConversationList.get(i);
        viewHolder.chat.setText(myConversation.getChat());
        viewHolder.initial.setText(myConversation.getMyName());
    }

    @Override
    public int getItemCount() {
        return myConversationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView chat;
        TextView initial;

        public ViewHolder(View itemView) {
            super(itemView);
            chat = itemView.findViewById(R.id.chat);
            initial = itemView.findViewById(R.id.initial);
        }
    }
}
