package pt.ulisboa.tecnico.meic.cmu.locmess.domain.Repository;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;

/**
 * Created by jp_s on 5/7/2017.
 */

public class MessageRepository {
    private static final MessageRepository messageRepository = new MessageRepository();
    private List<Message> messagesList = new ArrayList<Message>();

    private MessageRepository(){}

    public static MessageRepository getInstance( ) {
        return messageRepository;
    }



}
