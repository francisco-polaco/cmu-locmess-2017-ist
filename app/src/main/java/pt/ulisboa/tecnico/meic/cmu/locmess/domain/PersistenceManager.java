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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;

public class PersistenceManager {

    private static final String MESSAGEREPOSITORY_FILENAME = "messages.dat";
    private static final String MESSAGETOCARRY_FILENAME = "messagestocarry.dat";
    private static final String PROFILE_FILENAME = "profile.dat";
    private static final String MESSAGECOUNTER_FILENAME = "messagecounter.dat";
    private static final String TAG = PersistenceManager.class.getSimpleName();
    private static PersistenceManager ourInstance = new PersistenceManager();

    private Token token;

    // profile represents the key values of the user
    private HashMap<MessageDto,Message> messageRepository;
    private TreeMap<Integer, MessageDto> cachedMessages;
    private HashMap<MessageDto,Message> messageToCarry;
    private List<Pair> Profile;
    private boolean stateHasChanged = false;
    private int messageCounter = 0;

    public List<Pair> getProfile() {
        return Profile;
    }

    public void addPair(Pair p) {
        Profile.add(p);
    }

    public void removePair(Pair p) {
        Profile.remove(p);
    }

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

    public HashMap<MessageDto, Message> getMessageToCarry() {
        return messageToCarry;
    }

    public int getMessageCounter() {
        return messageCounter;
    }

    public void setMessageCounter(int messageCounter) {
        this.messageCounter = messageCounter;
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

    public void loadMessagesDescentralized(Context context) {
        loadMessagesDescentralized(context, false);
    }

    public void loadMessagesToCarry(Context context) {
        loadMessagesToCarry(context, false);
    }

    public void flushAndLoadMessagesDescentralized(Context context) {
        loadMessagesDescentralized(context, true);
    }

    public void flushAndLoadProfile(Context context) {
        loadProfile(context, true);
    }

    private void loadProfile(Context context, boolean flush) {
        if (flush || Profile == null) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                    context.openFileInput(PROFILE_FILENAME)))) {
                Profile = (List<Pair>) objectInputStream.readObject();
            } catch (ClassNotFoundException| IOException e) {
                Profile= new ArrayList<>();
            }
        }
    }

    public void saveProfile(Context context) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(PROFILE_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(Profile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMessageCounter(Context context) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                    context.openFileInput(MESSAGECOUNTER_FILENAME)))) {
                messageCounter = (int) objectInputStream.readObject();
            } catch (ClassNotFoundException| IOException e) {
                e.getStackTrace();
            }
    }

    public void saveMessageCounter(Context context) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(MESSAGECOUNTER_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(messageCounter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMessagesDescentralized(Context context, boolean flush) {
        if (flush || messageRepository == null) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                    context.openFileInput(MESSAGEREPOSITORY_FILENAME)))) {
                messageRepository = (HashMap<MessageDto,Message>) objectInputStream.readObject();
            } catch (ClassNotFoundException| IOException e) {
                messageRepository= new HashMap<>();
            }
        }
    }

    public void saveMessagesDescentralized(Context context) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(MESSAGEREPOSITORY_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(messageRepository);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMessagesToCarry(Context context, boolean flush) {
        if (flush || messageToCarry == null) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                    context.openFileInput(MESSAGETOCARRY_FILENAME)))) {
                messageToCarry = (HashMap<MessageDto,Message>) objectInputStream.readObject();
            } catch (ClassNotFoundException| IOException e) {
                messageToCarry= new HashMap<>();
            }
        }
    }

    public void saveMessagesToCarry(Context context) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(MESSAGETOCARRY_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(messageToCarry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void addToMessageRepository(Message message) {
        messageRepository.put(createMessageDTO(message),message);
    }

    public Message getMessage(MessageDto messageDto) {
        return messageRepository.get(messageDto);
    }
    public HashMap<MessageDto,Message> getMessageRepository(){return messageRepository;}

    public void removeFromMessageRepository(MessageDto message) {
        if(messageRepository.containsKey(message))
            messageRepository.remove(message);
    }

    public MessageDto createMessageDTO(Message message){
        Date convertedDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            convertedDate = simpleDateFormat.parse(message.getBeginDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new MessageDto(0, message.getTitle(), message.getContent(), message.getOwner(), convertedDate);
    }

    public boolean inMessageRepository(MessageDto messageDto) {
        return messageRepository.containsKey(messageDto);
    }



}
