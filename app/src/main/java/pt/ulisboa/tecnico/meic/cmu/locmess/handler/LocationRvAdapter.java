package pt.ulisboa.tecnico.meic.cmu.locmess.handler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location;

public class LocationRvAdapter extends RecyclerView.Adapter<LocationRvAdapter.ViewHolder> {

    public Context context;
    private List<Location> dataset;

    public LocationRvAdapter(List<Location> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @Override
    public LocationRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_view, parent, false);
        return new LocationRvAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final LocationRvAdapter.ViewHolder holder, final int position) {
        Location location = dataset.get(position);
        holder.title.setText(location.getName());
        holder.content.setText(location.toString());
        holder.type.setText(location instanceof GPSLocation ? "GPS" : "AP");
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void removeLoc(Location location) {
        dataset.remove(location);
        notifyDataSetChanged();
    }

    public void addLoc(Location location) {
        dataset.add(location);
        notifyDataSetChanged();
    }

    public Location getMessageById(int adapterPosition) {
        return dataset.get(adapterPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View v;
        public TextView title;
        public TextView content;
        public TextView type;

        public ViewHolder(View v) {
            super(v);
            this.title = (TextView) v.findViewById(R.id.title);
            this.content = (TextView) v.findViewById(R.id.content);
            this.type = (TextView) v.findViewById(R.id.type);
            this.v = v;
        }
    }
}


