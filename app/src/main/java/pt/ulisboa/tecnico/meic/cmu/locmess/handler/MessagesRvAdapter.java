package pt.ulisboa.tecnico.meic.cmu.locmess.handler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.presentation.Login;
import pt.ulisboa.tecnico.meic.cmu.locmess.presentation.ShowMessage;

/**
 * Created by Diogo on 06/05/2017.
 */

public class MessagesRvAdapter extends RecyclerView.Adapter<MessagesRvAdapter.ViewHolder> {

    private List<MessageDto> dataset;
    private SimpleDateFormat simpleDateFormat;
    public Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View v;
        public TextView title;
        public TextView content;
        public TextView bdate;

        public ViewHolder(View v) {
            super(v);
            this.title = (TextView) v.findViewById(R.id.title);
            this.content = (TextView) v.findViewById(R.id.content);
            this.bdate = (TextView) v.findViewById(R.id.bdate);
            this.v = v;
        }
    }

    public MessagesRvAdapter(List<MessageDto> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
        this.simpleDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
    }

    @Override
    public MessagesRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(dataset.get(position).getTitle());
        holder.content.setText(dataset.get(position).getContent());
        holder.bdate.setText(simpleDateFormat.format(dataset.get(position).getPublicationDate()));
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(context, ShowMessage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void removeMsg(MessageDto messageDto) {
        dataset.remove(messageDto);
        notifyDataSetChanged();
    }

    public MessageDto getMessageById(int adapterPosition) {
        return dataset.get(adapterPosition);
    }


}



