package models.util;

import api.http.httpResponse.TruResponse;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

/**
 * Created by User on 19-11-2016.
 */
public class Message {

    public static final String MESSAGE_ERROR = "E";
    public static final String MESSAGE_WARNING = "W";
    public static final String MESSAGE_INFO = "I";

    private String type;
    private String text;

    public Message(String type, String text) throws Exception {
        if(type != MESSAGE_ERROR && type != MESSAGE_INFO && type != MESSAGE_WARNING) {
            throw new Exception("Unrecognized Message Type: \""+type+"\"");
        }
        this.type = type;
        this.text = text;
    }

    public static Boolean checkErrorMessageExists(List<Message> messageList) {
        for(Message message:messageList){
            if(message.getType() == MESSAGE_ERROR) {return Boolean.TRUE;}
        }
        return Boolean.FALSE;
    }


    public static Boolean checkErrorMessageExists(TruResponse response) {
        if(response.getMessages().size() > 0) {
            for(Message message:response.getMessages()){
                if(message.getType() == MESSAGE_ERROR) {return Boolean.TRUE;}
            }
        }
        return Boolean.FALSE;
    }

    public static TruResponse collateMessages(List<TruResponse> source, TruResponse destination) {
        for(TruResponse eachResponse:source) {
            destination.getMessages().addAll(eachResponse.getMessages());
        }
        return destination;
    }

    public static TruResponse collateMessages(TruResponse source, TruResponse destination) {
        try{
            destination.getMessages().addAll(source.getMessages());
        } catch (NullPointerException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return destination;
    }

    public static List<Message> removeDuplicateText(List<Message> messages) {
        Set<Message> items = new HashSet<Message>();
        for (Message item : messages) {
            if (!items.contains(item)) { items.add(item); }
        }
        messages.clear();
        messages.addAll(items);
        return messages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Message rhs = (Message) obj;
        return new EqualsBuilder()
                .append(type, rhs.type)
                .append(text, rhs.text)
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(type).
                append(text).
                toHashCode();
    }

}
