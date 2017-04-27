package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

class Constants {
    public static final String CREDENTIALS_FILENAME = "credentials.dat";
    // Location Repository
    static final int MAX_SIZE = 10;
    // Update Location Service
    static final int UPDATE_INTERVAL = 1000 /** 60*/
            ;
    static final int TWO_MINUTES = UPDATE_INTERVAL; //fixme bad name
    static final int FASTEST_UPDATE_INTERVAL = 1000;
    public static final String CACHED_MGS = "cachedmessages.dat";
}
