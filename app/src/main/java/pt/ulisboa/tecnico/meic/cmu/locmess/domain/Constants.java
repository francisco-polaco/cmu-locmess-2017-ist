package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

class Constants {
    static final String CREDENTIALS_FILENAME = "credentials.dat";
    static final String MESSAGEREPOSITORY_FILENAME = "messages.dat";
    // Location Repository
    static final int MAX_SIZE = 10;
    // Update Location Service
    static final int UPDATE_INTERVAL = 1000 /** 60*/
            ;
    static final int INTERVAL = UPDATE_INTERVAL;
    static final int FASTEST_UPDATE_INTERVAL = 1000;
    static final String CACHED_MGS = "cachedmessages.dat";
}
