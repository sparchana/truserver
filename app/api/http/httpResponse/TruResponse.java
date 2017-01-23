package api.http.httpResponse;

import com.avaje.ebean.Model;
import models.util.Message;
import play.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 23-11-2016.
 */

public class TruResponse {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    private List<Message> messages;
    private Model entity;
    public int status;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException e ) {
                Logger.info(e.toString());
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    public TruResponse() {
        this.status = STATUS_FAILURE;
        messages = new ArrayList<Message>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Model getEntity() {
        return entity;
    }

    public void setEntity(Model entity) {
        this.entity = entity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
