package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;

public class PersistenceManager {

    private static final String MESSAGEREPOSITORY_FILENAME = "messages.dat";
    private static final String TAG = PersistenceManager.class.getSimpleName();

    private static PersistenceManager ourInstance = new PersistenceManager();

    private Token token;

    // profile represents the key values of the user
    private List<Message> messageRepository;
    private TreeMap<Integer, MessageDto> cachedMessages;
    private boolean stateHasChanged = false;

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        return ourInstance;
    }

    public void startLocationUpdates(Context context) {
        Log.d(TAG, "Starting up the update location service.");
        if (!Utils.isMyServiceRunning(context, UpdateLocationService.class))
            context.startService(new Intent(context, UpdateLocationService.class));
    }

    public void stopLocationUpdates(Context context) {
        Log.d(TAG, "Shutting down the update location service.");
        context.stopService(new Intent(context, UpdateLocationService.class));
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void saveCachedMessages(Context context) {
        if (!stateHasChanged) return;
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(context.getString(R.string.cached_message_filename), Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(cachedMessages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flushAndLoadCachedMessages(Context context) {
        loadCachedMessages(context, true);
    }

    public void loadCachedMessages(Context context) {
        loadCachedMessages(context, false);
    }

    private void loadCachedMessages(Context context, boolean flush) {
        if (flush || cachedMessages == null) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                    context.openFileInput(context.getString(R.string.cached_message_filename))))) {
                cachedMessages = (TreeMap<Integer, MessageDto>) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                cachedMessages = new TreeMap<>();
            }
        }
    }

    public void clearState(Context context) throws IOException {
        for (String filename : new String[]{context.getString(R.string.credentials_filename),
                context.getString(R.string.cached_message_filename),
                MESSAGEREPOSITORY_FILENAME}) {
            File file = new File(context.getFilesDir().getPath() + "/" + filename);
            if (file.exists()) file.delete();
        }
    }

    public void addToCache(MessageDto messageDto) {
        cachedMessages.put(messageDto.getId(), messageDto);
        stateHasChanged = true;
    }

    public boolean inCache(MessageDto messageDto) {
        return cachedMessages.containsValue(messageDto);
    }

    public Collection<MessageDto> retrieveCache() {
        return cachedMessages.values();
    }
    public List<Message> getMessageRepository() {
        return messageRepository;
    }

    public void loadMessagesDescentralized(Context context) {
        loadMessagesDescentralized(context, false);
    }

    public void flushAndLoadMessagesDescentralized(Context context) {
        loadMessagesDescentralized(context, true);
    }

    private void loadMessagesDescentralized(Context context, boolean flush) {
        if (flush || messageRepository == null) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                    context.openFileInput(MESSAGEREPOSITORY_FILENAME)))) {
                messageRepository = (ArrayList<Message>) objectInputStream.readObject();
                printMessages();
            } catch (ClassNotFoundException | IOException e) {
                messageRepository = new ArrayList<>();
            }
        }
    }

    public void saveMessagesDescentralized(Context context) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(MESSAGEREPOSITORY_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(messageRepository);
        } catch (IOException ignored) {
        }
    }

    private void printMessages() {
        for (Message m : messageRepository)
            Log.d(TAG, "getMessages: " + m.getContent());
    }

    public void addToMessageRepository(Message message) {
        Log.d(TAG, "addToMessageRepository: Entrei2");
        messageRepository.add(message);
        Log.d(TAG, "addToMessageRepository: EntreEConquistei");
        //stateHasChanged = true;
    }

}
