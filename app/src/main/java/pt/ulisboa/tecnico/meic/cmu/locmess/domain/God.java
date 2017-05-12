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
import java.util.List;
import java.util.TreeMap;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.NotInitializedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;

public class God {

    static final String MESSAGEREPOSITORY_FILENAME = "messages.dat";
    private static final String TAG = God.class.getSimpleName();
    private static God ourInstance;
    private Context context;
    private Token token;
    // profile represents the key values of the user
    private List<Message> messageRepository;
    private TreeMap<Integer, MessageDto> cachedMessages;
    private boolean stateHasChanged = false;

    private God(Context context) {
        this.context = context;
        loadState();
        loadMessagesDescentralized();
    }

    public static God getInstance() {
        if (ourInstance == null) throw new NotInitializedException(God.class.getSimpleName());
        return ourInstance;

    }

    public static void init(Context context) {
        ourInstance = new God(context);
    }

    public Context getContext() {
        return context;
    }

    public void startLocationUpdates() {
        Log.d(TAG, "Starting up the update location service.");
        if (!Utils.isMyServiceRunning(context, UpdateLocationService.class))
            context.startService(new Intent(context, UpdateLocationService.class));
    }

    public void stopLocationUpdates() {
        Log.d(TAG, "Shutting down the update location service.");
        context.stopService(new Intent(context, UpdateLocationService.class));
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void saveState() {
        if (!stateHasChanged) return;
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(context.getString(R.string.cached_message_filename), Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(cachedMessages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadState() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                context.openFileInput(context.getString(R.string.cached_message_filename))))) {
            cachedMessages = (TreeMap<Integer, MessageDto>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            cachedMessages = new TreeMap<>();
        }
    }

    public void clearState() throws IOException {
        for (String filename : new String[]{context.getString(R.string.credentials_filename),
                context.getString(R.string.cached_message_filename),
                MESSAGEREPOSITORY_FILENAME}) {
            File file = new File(context.getFilesDir().getPath() + "/" + filename);
            if (file.exists()) file.delete();
        }
    }

    public List<Message> getMessageRepository() {
        return messageRepository;
    }

    private void printMessages() {
        for(Message m : messageRepository)
            Log.d(TAG, "getMessages: " + m.getContent());
    }


    public void addToCache(Integer id) {
        cachedMessages.put(id, null);
        stateHasChanged = true;
    }

    public boolean inCache(MessageDto messageDto) {
        return cachedMessages.containsValue(messageDto);
    }

    public void loadMessagesDescentralized()  {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                context.openFileInput(MESSAGEREPOSITORY_FILENAME)))) {
            messageRepository = (ArrayList<Message>) objectInputStream.readObject();
            printMessages();
        } catch (ClassNotFoundException| IOException e) {
            messageRepository= new ArrayList<>();
        }
    }

    public void saveMessagesDescentralized() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(MESSAGEREPOSITORY_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(messageRepository);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addToMessageRepository(Message message) {
        Log.d(TAG, "addToMessageRepository: Entrei2");
        messageRepository.add(message);
        Log.d(TAG, "addToMessageRepository: EntreEConquistei");
            //stateHasChanged = true;
    }

}
