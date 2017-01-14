package api.http.httpRequest;

import play.Logger;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by User on 26-11-2016.
 */
public class TruRequest {

    private List<String> changedFields;

    // String = Name of the entity
    // Long = Entity Id to be deleted
    private List<Map<String,Long>> deleteFields = new ArrayList<>();

    public List<String> getChangedFields() {
        return changedFields;
    }

    public void setChangedFields(List<String> changedFields) {
        this.changedFields = changedFields;
    }

    public String toString(Object caller) {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( caller.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = caller.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(caller) );
            } catch ( IllegalAccessException e ) {
                Logger.info(e.toString());
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    public List<Map<String, Long>> getDeleteFields() {
        return deleteFields;
    }

    public void setDeleteFields(List<Map<String, Long>> deleteFields) {
        if(this.deleteFields != null && this.deleteFields.size() > 0){this.deleteFields.clear();}

        for(Map<String,Long> each:deleteFields){
            Map<String,Long> eachCopy = new HashMap<String, Long>();
            eachCopy.put(each.keySet().toArray()[0].toString().toLowerCase(),each.get(each.keySet().toArray()[0]));
            this.deleteFields.add(eachCopy);
        }
        //this.deleteFields = deleteFields;
    }

    public List<Long> getDeleteIdsByEntityName(String entityName){

        Logger.info(this.getClass().getSimpleName()+".getDeleteIdsByEntityName: entityName="+entityName);
        ListIterator iterator = deleteFields.listIterator();
        List<Long> idsToDeleteList = new ArrayList<>();

        while(iterator.hasNext()){
            Map<String,Long> each = (Map<String, Long>) iterator.next();
            if(each.containsKey(entityName.toLowerCase())){ idsToDeleteList.add(each.get(entityName.toLowerCase())); }
        }

        Logger.info(this.getClass().getSimpleName()+".getDeleteIdsByEntityName: idsToDeleteList has "+idsToDeleteList.size()+" elements");
        return idsToDeleteList;
    }

}
