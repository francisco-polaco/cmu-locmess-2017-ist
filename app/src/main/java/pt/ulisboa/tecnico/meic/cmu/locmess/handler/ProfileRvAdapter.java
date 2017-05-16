package pt.ulisboa.tecnico.meic.cmu.locmess.handler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;

public class ProfileRvAdapter extends RecyclerView.Adapter<ProfileRvAdapter.ViewHolder> {

    public Context context;
    private List<Pair> dataset;

    public ProfileRvAdapter(List<Pair> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @Override
    public ProfileRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_view, parent, false);
        return new ProfileRvAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ProfileRvAdapter.ViewHolder holder, final int position) {
        Pair pair = dataset.get(position);
        holder.title.setText(pair.toString());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void removePair(Pair pair) {
        dataset.remove(pair);
        notifyDataSetChanged();
    }

    public void addPair(Pair pair) {
        dataset.add(pair);
        notifyDataSetChanged();
    }

    public Pair getMessageById(int adapterPosition) {
        return dataset.get(adapterPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View v;
        public TextView title;

        public ViewHolder(View v) {
            super(v);
            this.title = (TextView) v.findViewById(R.id.title);
            this.v = v;
        }
    }
}