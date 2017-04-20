package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import com.google.android.gms.maps.model.LatLng;

import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;

public final class GetLastLocationService extends LocmessService {
    private LatLng result;

    public GetLastLocationService() {
        // TODO : Fix me
        super(null, null);
    }

    @Override
    public void dispatch() {
        result = God.getInstance().getLastLocation();
    }

    public LatLng result(){
        return result;
    }
}
