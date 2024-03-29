package pt.ulisboa.tecnico.meic.cmu.locmess.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.PersistenceManager;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.StaticFields;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;

/**
 * Created by Diogo on 06/05/2017.
 */

public class MessagesRvAdapter extends RecyclerView.Adapter<MessagesRvAdapter.ViewHolder> {

    public Context context;
    private List<MessageDto> dataset;
    private SimpleDateFormat simpleDateFormat;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.title.setText(dataset.get(position).getTitle());
        boolean me = false;
        if (amIPublisher(dataset.get(position).getPublisher())) {
            holder.v.setBackgroundColor(context.getColor(R.color.cyan));
            me = true;
        }
        if (PersistenceManager.getInstance().inCache(dataset.get(position))) {
            if (me) holder.v.setBackgroundColor(context.getColor(R.color.light_pink));
            else holder.v.setBackgroundColor(context.getColor(R.color.light_yellow));
        }
        holder.content.setText(dataset.get(position).getContent());
        holder.bdate.setText(simpleDateFormat.format(dataset.get(position).getPublicationDate()));
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersistenceManager.getInstance().addToCache(dataset.get(position));
                new Thread() {
                    @Override
                    public void run() {
                        PersistenceManager.getInstance().saveCachedMessages(context);
                    }
                }.start();
                MessageDto messageDto = dataset.get(position);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(holder.v.getContext());
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.dialog, null);

                TextView publisher = (TextView) dialogView.findViewById(R.id.publisher);
                publisher.setText(messageDto.getPublisher());

                TextView date = (TextView) dialogView.findViewById(R.id.date);
                date.setText(simpleDateFormat.format(messageDto.getPublicationDate()));

                TextView content = (TextView) dialogView.findViewById(R.id.content);
                content.setText(messageDto.getContent());

                dialogBuilder.setView(dialogView);

                dialogBuilder.setTitle(messageDto.getTitle());
                dialogBuilder.setPositiveButton(R.string.ok, null);
                dialogBuilder.create().show();

            }
        });
    }

    private boolean amIPublisher(String publisher) {
        return publisher.equals(StaticFields.username);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void removeMsg(MessageDto messageDto) {
        dataset.remove(messageDto);
        notifyDataSetChanged();
    }

    public void addMsg(MessageDto messageDto) {
        dataset.add(messageDto);
        notifyDataSetChanged();
    }

    public void addMsgs(Collection<MessageDto> messages) {
        dataset.addAll(messages);
        notifyDataSetChanged();
    }

    public MessageDto getMessageById(int adapterPosition) {
        return dataset.get(adapterPosition);
    }

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


}



