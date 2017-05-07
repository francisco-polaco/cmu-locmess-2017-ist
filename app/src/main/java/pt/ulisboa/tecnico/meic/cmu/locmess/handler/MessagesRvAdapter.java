package pt.ulisboa.tecnico.meic.cmu.locmess.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
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
        holder.content.setText(dataset.get(position).getContent());
        holder.bdate.setText(simpleDateFormat.format(dataset.get(position).getPublicationDate()));
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                God.getInstance().addToCache(position + 1);
                new Thread() {
                    @Override
                    public void run() {
                        God.getInstance().saveState();
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



