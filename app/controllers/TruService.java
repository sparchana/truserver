package controllers;

import api.http.httpRequest.TruRequest;
import api.http.httpResponse.Recruiter.RecruiterLeadResponse;
import api.http.httpResponse.TruResponse;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.base.Defaults;
import models.util.Message;
import org.jetbrains.annotations.Nullable;
import play.Logger;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static com.avaje.ebean.Expr.eq;
import static models.util.Util.ACTION_CREATE;
import static models.util.Util.ACTION_UPDATE;

/**
 * Created by User on 23-11-2016.
 */
public abstract class TruService {

    protected Model entity;
    private Field keyField;

    public String getResponseClassName() {
        return "api.http.httpResponse.TruResponse";
    }

    public abstract String getEntityClassName();

    public Boolean checkSaveAllowed(TruResponse response) {
        return (!Message.checkErrorMessageExists(response));
    }

    public Boolean save() {

        if(entity == null) return Boolean.FALSE;

        java.lang.reflect.Method method = null;
        String methodname = "";

        try {
            methodname = "save";
            method = entity.getClass().getMethod(methodname);
            Logger.info("Save " + methodname + " found in " + entity.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            Logger.info("Save " + methodname + " not found in " + entity.getClass().getSimpleName());
            return Boolean.FALSE;
        }

        if (method != null) {
            try {
                method.invoke(entity);
                Logger.info("Save executed for " + entity.getClass().getSimpleName());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logger.info("Save not executed for " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public List<TruResponse> createAsChildren(Object request, Model parent) {
        return createAsChildren(request, parent, 1);
    }

    public List<TruResponse> createAsChildren(Object request, Model parent, long limit) {
        List<TruResponse> responseList = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            responseList.add((TruResponse) createAsChild(request, parent));
        }
        return responseList;
    }

    // Use for 1:1 relationship between Parent and Child
    public Boolean setChildReference(TruResponse childResponse, String childAttributeName) {

        // check if child exists
        Model child = childResponse.getEntity();

        if ((child != null) && (isCompatible(child,childAttributeName))) {
            Logger.info(this.getClass().getSimpleName()+".isCompatible returned TRUE. Child="+child.getClass().getSimpleName()+", childAttributeName="+childAttributeName);
            java.lang.reflect.Method method = null;
            String methodname = "";

            // identify child reference setter method in parent
            try {
                methodname = "set";
                methodname += childAttributeName.substring(0, 1).toUpperCase() + childAttributeName.substring(1);
                method = entity.getClass().getMethod(methodname, child.getClass());
                //Logger.info("Children reference setter "+methodname+" found in "+entity.getClass().getSimpleName());
            } catch (NoSuchMethodException e) {
                Logger.info("Child reference setter " + methodname + " not found in " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                return Boolean.FALSE;
            }

            // set child reference in parent
            try {
                method.invoke(entity, child);
                Logger.info("Child reference setter " + methodname + " executed");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logger.info("Child reference setter " + methodname + " could not be executed in " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    // Use for 1:N relationship between Parent and Child
    public Boolean setChildrenReference(List<TruResponse> childrenResponse, String childListAttributeName) {

        // check if children exist
        List<Model> children = new ArrayList<>();
        for (TruResponse each : childrenResponse) {
            if ((each.getEntity() != null) && (isCompatible(each.getEntity(),childListAttributeName))) {
                Logger.info(this.getClass().getSimpleName()+".isCompatible returned TRUE. Child="+each.getClass().getSimpleName()+", childListAttributeName="+childListAttributeName);
                children.add(each.getEntity());
            }
        }

        if (children.size() > 0) {
            java.lang.reflect.Method method = null;
            String methodname = "";

            // identify child reference setter method in parent
            try {
                methodname = "set";
                methodname += childListAttributeName.substring(0, 1).toUpperCase() + childListAttributeName.substring(1);
                method = entity.getClass().getMethod(methodname, List.class);
                Logger.info("Children reference setter " + methodname + " found in " + entity.getClass().getSimpleName());
            } catch (NoSuchMethodException e) {
                Logger.info("Children reference setter " + methodname + " not found in " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                return Boolean.FALSE;
            }

            // set child reference in parent
            try {
                method.invoke(entity, children);
                Logger.info("Children reference setter " + methodname + " executed");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logger.info("Children reference setter " + methodname + " could not be executed in " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    public Object createAsChild(Object request, Model parent) {

        // Create the child
        TruResponse response = (TruResponse) create(request);

        if (response.getStatus() == TruResponse.STATUS_SUCCESS) {
            // Get handle to the child
            Model child = response.getEntity();

            if(child == null){
                response.setStatus(TruResponse.STATUS_FAILURE);
                return response;
            }

            java.lang.reflect.Method method = null;
            String methodname = "";

            // Set reference to parent
            try {
                methodname = "set";
                methodname += parent.getClass().getSimpleName().substring(0, 1).toUpperCase() + parent.getClass().getSimpleName().substring(1);
                method = child.getClass().getMethod(methodname, parent.getClass());
                Logger.info("Parent setter " + methodname + " found in " + child.getClass().getSimpleName());
            } catch (NoSuchMethodException | NullPointerException e) {
                Logger.info("Parent setter " + methodname + " not found in " + child.getClass().getSimpleName() + " Throws " + e.toString());
                response.setStatus(TruResponse.STATUS_FAILURE);
                return response;
            }

            try {
                method.invoke(child, parent);
                Logger.info("Parent setter " + methodname + " executed.");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) {
                Logger.info("Parent setter " + methodname + " could not be executed in " + child.getClass().getSimpleName() + " Throws " + e.toString());
                response.setStatus(TruResponse.STATUS_FAILURE);
                return response;
            }

            response.setStatus(TruResponse.STATUS_SUCCESS);
            response.setEntity(child);
        }

        return response;
    }

    public Object create(Object request) {

        TruResponse response = null;
        try {
            response = (TruResponse) Class.forName(this.getResponseClassName()).newInstance();
        } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Logger.info("Exception " + e.getMessage() + " triggered while instantiating " + this.getResponseClassName());
            return null;
        }

        entity = null;
        try {
            entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
        } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
            e.printStackTrace();
            return response;
        }

        List<Message> messageList = new ArrayList<Message>();
        List<Message> thisField = new ArrayList<Message>();
        Method method = null;
        String methodname = "";
        Boolean changeDetected = Boolean.FALSE;

        for (Field field : entity.getClass().getDeclaredFields()) {

            Boolean setAllowed = Boolean.TRUE;
            Object requestFieldValue;

            // ignore list: parent, Id, UUID, Create/Update Timestamp and special EBean Model "find"
            if (field.getName().startsWith("_") ||
                    field.getName().toUpperCase().contains("UUID") ||
                    field.getName().equalsIgnoreCase("find") ||
                    field.isAnnotationPresent(Id.class) ||
                    field.isAnnotationPresent(CreatedTimestamp.class) ||
                    field.isAnnotationPresent(UpdatedTimestamp.class)) continue;

            // try to set join reference
            if (field.isAnnotationPresent(JoinColumn.class)) {
                try{
                    Logger.info(this.getClass().getSimpleName()+".create(): About to call requestJoinEntity with field="+field.getName()+", request="+request.getClass().getSimpleName());
                    Model joinEntity = requestJoinEntity(field.getName(), (TruRequest) adjustRequest((TruRequest) request).get(0));
                    Logger.info("trying to set join reference: joinEntity="+((joinEntity != null)?joinEntity.getClass().getSimpleName():"null"));
                    if(joinEntity != null) {
                        if(setJoinReference(joinEntity,field.getName())) {changeDetected = Boolean.TRUE;}
                    }
                    setAllowed = Boolean.FALSE;
                }
                catch (NullPointerException e) {
                    continue;
                }
            }

            // ignore list: JSON references
            if(field.isAnnotationPresent(JsonManagedReference.class) ||
               field.isAnnotationPresent(JsonBackReference.class)) continue;

            // check if validator exists
            try {
                methodname = "validate";
                methodname += field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                method = entity.getClass().getMethod(methodname, request.getClass(), String.class, entity.getClass());
                //Logger.info("Validator "+methodname+" found in "+entity.getClass().getSimpleName());
            } catch (NoSuchMethodException e) {
                //Logger.info("Validator "+methodname+" not found in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                method = null;
            }

            // if exists, validate
            if (method != null) {
                try {
                    thisField.addAll((List<Message>) method.invoke(entity, request, ACTION_CREATE, entity));
                    messageList.addAll(thisField);
                    Logger.info("Validator " + methodname + " executed. " + thisField.size() + " messages generated.");
                } catch (ClassCastException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                    //Logger.info("Validator "+methodname+" could not be executed in "+request.getClass().getSimpleName()+" Throws "+e.toString());
                }

                // if validation returned errors, set flag to indicate showstopper
                if (thisField.size() > 0) {
                    for (Message message : thisField) {
                        if (message.getType().equals(Message.MESSAGE_ERROR)) {
                            setAllowed = Boolean.FALSE;
                            Logger.info("Error reported! Create will abort after validations...");
                            break;
                        }
                    }
                }
            }

            // if validation for this field succeeded, move value to entity
            // Step 1 - get value from request
            if (setAllowed == Boolean.TRUE) {

                try {
                    methodname = "get";
                    methodname += field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                    method = request.getClass().getMethod(methodname);
                    Logger.info("Getter "+methodname+" found in "+request.getClass().getSimpleName());
                } catch (NoSuchMethodException e) {
                    //Logger.info("Getter "+methodname+" not found in "+request.getClass().getSimpleName()+" Throws "+e.toString());
                    continue;
                }

                try {
                    requestFieldValue = method.invoke(request);
                    Logger.info("Getter " + methodname + " executed in "+request.getClass().getSimpleName());
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    //Logger.info("Getter "+methodname+" could not be executed in "+request.getClass().getSimpleName()+" Throws "+e.toString());
                    continue;
                }

                if(requestFieldValue == null) Logger.info(request.getClass().getSimpleName()+"."+field.getName()+" is null");

                if(requestFieldValue != null){
                    if(!(requestFieldValue.equals(((field.getType().isPrimitive())?Defaults.defaultValue(field.getType()):null)))) {
                        changeDetected = Boolean.TRUE;
                        // Step 2 - set value in entity
                        try {
                            methodname = "set";
                            methodname += field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                            method = entity.getClass().getMethod(methodname, field.getType());
                            Logger.info("Setter "+methodname+" found in "+entity.getClass().getSimpleName());
                        } catch (NoSuchMethodException e) {
                            Logger.info("Setter "+methodname+" not found in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                            continue;
                        }

                        try {
                            method.invoke(entity, requestFieldValue);
                            Logger.info("Setter " + methodname + " executed.");
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            Logger.info("Setter "+methodname+" could not be executed in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                        }
                    }
                }

            }

        }

        // Call business logic validation for create. Collect all validation messages
        messageList.addAll(validateCreate(entity, messageList));

        // Push get/set validation + business logic validation messages into response
        try {
            methodname = "setMessages";
            method = response.getClass().getMethod(methodname, List.class);
            //Logger.info("Message Setter "+methodname+" found in "+response.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            //Logger.info("Message Setter "+methodname+" not found in "+response.getClass().getSimpleName()+" Throws "+e.toString());
            method = null;
        }

        if (method != null) {
            try {
                method.invoke(response, messageList);
                Logger.info("Message Setter " + methodname + " executed.");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //Logger.info("Message Setter "+methodname+" could not be executed in "+response.getClass().getSimpleName()+" Throws "+e.toString());
            }
        }

        // Set response status
        try {
            methodname = "setStatus";
            method = response.getClass().getMethod(methodname, int.class);
            //Logger.info("Response Status Setter "+methodname+" found in "+response.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            //Logger.info("Response Status Setter "+methodname+" not found in "+response.getClass().getSimpleName());
            method = null;
        }

        if (method != null) {
            try {
                // Fails if nothing changed or if something changed but generated error messages
                if ((!changeDetected) || (Message.checkErrorMessageExists(response))) {
                    method.invoke(response, RecruiterLeadResponse.STATUS_FAILURE);
                    // invalidate entity (This will prevent dirty entities from being persisted if a save is called)
                    Logger.info("Invalidating "+entity.getClass().getSimpleName()+" during create."+this.getClass().getSimpleName());
                    entity = null;
                } else {
                    // Push entity into response
                    response = pushEntityIntoResponse(entity, response);
                    method.invoke(response, RecruiterLeadResponse.STATUS_SUCCESS);
                }
                Logger.info("Response Status Setter " + methodname + " executed.");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //Logger.info("Response Status Setter "+methodname+" could not be executed in "+response.getClass().getSimpleName()+" Throws "+e.toString());
            }
        }

        return response;
    }

    private TruResponse pushEntityIntoResponse(Model entity, TruResponse response) {
        Method method = null;
        String methodname = "";
        // Push entity into response
        try {
            methodname = "setEntity";
            method = response.getClass().getMethod(methodname, Model.class);
            //Logger.info("Setter "+methodname+" found in "+response.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            //Logger.info("Setter "+methodname+" not found in "+response.getClass().getSimpleName()+" Throws "+e.toString());
            method = null;
        }

        if (method != null) {
            try {
                method.invoke(response, entity);
                Logger.info("Push " + entity.getClass().getSimpleName() + " into " + response.getClass().getSimpleName() + " executed.");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //Logger.info("Setter "+methodname+" could not be executed in "+response.getClass().getSimpleName()+" Throws "+e.toString());
            }
        }
        return response;
    }

    private List<TruResponse> createReadResponse(List<Model> entityList) {
        List<TruResponse> responseList = new ArrayList<>();
        for (Model eachEntity : entityList) {
            // create response
            TruResponse response = null;
            try {
                response = (TruResponse) Class.forName(this.getResponseClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Logger.info("Exception " + e.getMessage() + " triggered while instantiating " + this.getResponseClassName());
                continue;
            }

            if (response != null) {
                response.setStatus(TruResponse.STATUS_SUCCESS);
                // push entity into response
                responseList.add(pushEntityIntoResponse(eachEntity, response));
            }
        }

        return responseList;
    }

    private List<TruResponse> read(List<?> keys, String methodname) {

        if (entity == null) {
            try {
                entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                return null;
            }
        }

        java.lang.reflect.Method method = null;
        List<Model> entityList = new ArrayList<>();
        //List<TruResponse> responseList = new ArrayList<>();

        // Check read method exists
        try {
            method = entity.getClass().getMethod(methodname, List.class);
            //Logger.info("Read "+methodname+" found in "+entity.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            //Logger.info("Read "+methodname+" not found in "+entity.getClass().getSimpleName());
            method = null;
        }

        if (method != null) {
            try {
                entityList = (List<Model>) method.invoke(entity, keys);
                Logger.info("Read " + methodname + " executed in " + entity.getClass().getSimpleName());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //Logger.info("Read "+methodname+" could not be executed in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
            }
            return createReadResponse(entityList);
        }

        return null;
    }

    public List<TruResponse> readAll() {
        if (entity == null) {
            try {
                entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                return null;
            }
        }

        java.lang.reflect.Method method = null;
        List<Model> entityList = new ArrayList<>();

        // Check read method exists
        try {
            method = entity.getClass().getMethod("readAll");
            //Logger.info("Read "+methodname+" found in "+entity.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            //Logger.info("Read "+methodname+" not found in "+entity.getClass().getSimpleName());
            method = null;
        }

        if (method != null) {
            try {
                entityList = (List<Model>) method.invoke(entity);
                Logger.info("Read readAll() executed in " + entity.getClass().getSimpleName());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //Logger.info("Read "+methodname+" could not be executed in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
            }

            return createReadResponse(entityList);
        }

        return null;
    }

    public List<TruResponse> readByUUID(List<String> uuids) {
        return read(uuids, "readByUUID");
    }

    public List<TruResponse> readById(List<Long> ids) {
        return read(ids, "readById");
    }

    public TruResponse addMessage(TruResponse response, Message message) {
        if (response != null && message != null) {
            List<Message> messageList = response.getMessages();
            if (messageList.add(message)) {
                response.setMessages(messageList);
                return response;
            }
        }
        return null;
    }

    public TruResponse addMessage(TruResponse response, String type, String text) {
        Message message = null;
        if (response != null) {
            try {
                message = new Message(type, text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return addMessage(response, message);
    }

    public Boolean setJoinReference(Model joinReference, String attributeName) {

        Logger.info(this.getClass().getSimpleName()+".setJoinReference: attributeName="+attributeName+", joinReference="+((joinReference != null)?joinReference.getClass().getSimpleName():"null"));

        if(joinReference != null){
            if (entity == null) {
                try {
                    entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
                } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                    return Boolean.FALSE;
                }
            }

            java.lang.reflect.Method method = null;
            try {
                if (entity.getClass().getDeclaredField(attributeName).isAnnotationPresent(JoinColumn.class)) {
                    String methodname = "set";
                    try {
                        methodname += attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
                        method = entity.getClass().getMethod(methodname, joinReference.getClass());
                        //Logger.info("Setter "+methodname+" found in "+entity.getClass().getSimpleName());
                    } catch (NoSuchMethodException e) {
                        //Logger.info("Setter "+methodname+" not found in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                        return Boolean.FALSE;
                    }

                    try {
                        method.invoke(entity, joinReference);
                        Logger.info("Setter " + methodname + " executed.");
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        //Logger.info("Setter "+methodname+" could not be executed in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                        return Boolean.FALSE;
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    public List<Message> validateCreate(Model entity, List<Message> messageList) {
        return messageList;
    }

    // Entity = updated values from the front end
    // DB Entity = database copy
    // changed fields = which fields were changed
    public List<Message> validateUpdate(Model entity, List<Message> messageList, Model dbEntity, List<String> changedFields) {
        return messageList;
    }

    public TruResponse update(TruRequest request) {
        return update(request, Boolean.FALSE, null);
    }

    @Nullable
    private TruResponse update(TruRequest request, Boolean asChild, Model parent) {

        TruResponse response = null;
        try {
            response = (TruResponse) Class.forName(this.getResponseClassName()).newInstance();
        } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Logger.info("Exception " + e.getMessage() + " triggered while instantiating " + this.getResponseClassName());
            return null;
        }

        if (request.getChangedFields().size() > 0) {

            Boolean changeDetected = Boolean.FALSE;
            Model dbEntity = null;

            entity = null;
            try {
                entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                return response;
            }

            // get id field
            if (getKeyField() == null) {
                setKeyField(entity);
            }

            // get id value from request
            String methodname = "get";
            java.lang.reflect.Method method = null;
            try {
                methodname += keyField.getName().substring(0, 1).toUpperCase() + keyField.getName().substring(1);
                method = request.getClass().getMethod(methodname);
                //Logger.info("Getter "+methodname+" found in "+request.getClass().getSimpleName());
            } catch (NoSuchMethodException e) {
                //Logger.info("Getter "+methodname+" not found in "+request.getClass().getSimpleName()+" Throws "+e.toString());
                Logger.info(entity.getClass().getSimpleName()+".update: Did not find id read method (" + methodname + ")");
                e.printStackTrace();
                return response;
            }

            Long idValue = 0L;
            try {
                idValue = (Long) method.invoke(request);
                Logger.info("Getter " + methodname + " executed on "+request.getClass().getSimpleName());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //Logger.info("Getter "+methodname+" could not be executed in "+request.getClass().getSimpleName()+" Throws "+e.toString());
                Logger.info(entity.getClass().getSimpleName()+".update: Could not execute id read (" + methodname + ")");
                e.printStackTrace();
                return response;
            }

            if (idValue != null && idValue > 0) {

                // fetch existing entity
                List<Long> idList = new ArrayList<>();
                idList.add(idValue);
                try {
                    entity = readById(idList).get(0).getEntity();
                    dbEntity = readById(idList).get(0).getEntity();
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    Logger.info(entity.getClass().getSimpleName()+".update: Failed to read Id '" + idValue + "' (" + methodname + ")");
                    e.printStackTrace();
                    return response;
                    //return (TruResponse) ((asChild && (parent != null)) ? createAsChildren(request, parent) : create(request));
                }

                // do for each changed field
                List<Message> messageList = new ArrayList<Message>();
                for (String eachField : request.getChangedFields()) {

                    Field changeField = null;
                    Object oldValue = null;
                    Object newValue = null;
                    List<Message> thisField = new ArrayList<Message>();

                    // does this field exist in the entity?
                    try {
                        changeField = entity.getClass().getDeclaredField(eachField);
                    } catch (NoSuchFieldException e) {
                        Logger.info(entity.getClass().getSimpleName()+".update: Field " + eachField + " not found");
                        continue;
                    }

                    // can this field be changed?
                    if (changeField.isAnnotationPresent(Id.class) || changeField.getName().toUpperCase().contains("UUID")) {
                        Logger.info(entity.getClass().getSimpleName()+".update: Field " + eachField + " cannot be updated manually. Skipping...");
                        continue;
                    }
                    // is this a join field?
                    else if(changeField.isAnnotationPresent(JoinColumn.class)){
                        newValue = requestJoinEntity(changeField.getName(),request);
                        if(newValue == null) continue;
                        Logger.info("New Value for " + changeField.getName() + "= " + ((newValue == null)? "null":newValue.toString()));
                    }
                    else {
                        // get changed value from request
                        try {
                            method = null;
                            methodname = "get";
                            methodname += changeField.getName().substring(0, 1).toUpperCase() + changeField.getName().substring(1);
                            method = request.getClass().getMethod(methodname);
                            //Logger.info("Getter "+methodname+" found in "+request.getClass().getSimpleName());
                        } catch (NoSuchMethodException | NullPointerException e) {
                            //Logger.info("Getter "+methodname+" not found in "+request.getClass().getSimpleName()+" Throws "+e.toString());
                            continue;
                        }

                        try {
                            newValue = method.invoke(request);
                            Logger.info("New Value for " + changeField.getName() + "= " + ((newValue == null)? "null":newValue.toString()));
                            //Logger.info("Getter "+methodname+" executed.");
                        } catch (NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            //Logger.info("Getter "+methodname+" could not be executed in "+request.getClass().getSimpleName()+" Throws "+e.toString());
                            continue;
                        }
                    }

                    // get existing value from entity
                    try {
                        method = null;
                        methodname = "get";
                        methodname += changeField.getName().substring(0, 1).toUpperCase() + changeField.getName().substring(1);
                        method = entity.getClass().getMethod(methodname);
                        //Logger.info("Getter "+methodname+" found in "+entity.getClass().getSimpleName());
                    } catch (NoSuchMethodException | NullPointerException e) {
                        //Logger.info("Getter "+methodname+" not found in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                        e.printStackTrace();
                        continue;
                    }

                    try {
                        oldValue = method.invoke(entity);
                        Logger.info("Old Value for " + changeField.getName() + "= " + ((oldValue == null)? "null":oldValue.toString()));
                        //Logger.info("Getter "+methodname+" executed.");
                    } catch (NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        //Logger.info("Getter "+methodname+" could not be executed in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                        e.printStackTrace();
                        continue;
                    }

                    // has anything changed?
                    try{
                        if (!(newValue.equals(oldValue))) {

                            Logger.info("Updating "+changeField.getName());
                            changeDetected = Boolean.TRUE;

                            // validate method exists?
                            try {
                                method = null;
                                methodname = "validate";
                                methodname += changeField.getName().substring(0, 1).toUpperCase() + changeField.getName().substring(1);
                                method = entity.getClass().getMethod(methodname, request.getClass(), String.class, entity.getClass());
                                //Logger.info("Validator "+methodname+" found in "+entity.getClass().getSimpleName());
                            } catch (NoSuchMethodException e) {
                                //Logger.info("Validator "+methodname+" not found in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                                method = null;
                            }

                            // if exists, validate
                            if (method != null) {
                                try {
                                    thisField.addAll((List<Message>) method.invoke(entity, request, ACTION_UPDATE, entity));
                                    messageList.addAll(thisField);
                                    Logger.info("Validator " + methodname + " executed. " + thisField.size() + " messages generated.");
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                    //Logger.info("Validator "+methodname+" could not be executed in "+request.getClass().getSimpleName()+" Throws "+e.toString());
                                }
                            }

                            if (Message.checkErrorMessageExists(thisField)) {
                                Logger.info(entity.getClass().getSimpleName()+".update: Error reported! Update will abort after validations...");
                            } else {
                                // update value in entity
                                try {
                                    methodname = "set";
                                    methodname += changeField.getName().substring(0, 1).toUpperCase() + changeField.getName().substring(1);
                                    method = entity.getClass().getMethod(methodname, changeField.getType());
                                    //Logger.info("Setter "+methodname+" found in "+entity.getClass().getSimpleName());
                                } catch (NoSuchMethodException e) {
                                    //Logger.info("Setter "+methodname+" not found in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                                    continue;
                                }

                                try {
                                    method.invoke(entity, newValue);
                                    Logger.info("Setter " + methodname + " executed.");
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                    //Logger.info("Setter "+methodname+" could not be executed in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
                                }
                            }

                        }
                    }
                    // this catch is to catch NPEs which the object.equals method might throw
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }

                }

                // Call business logic validation for update. Collect all validation messages
                messageList.addAll(validateUpdate(entity, messageList, dbEntity, request.getChangedFields()));

                // Push get/set validation + business logic validation messages into response
                try {
                    methodname = "setMessages";
                    method = response.getClass().getMethod(methodname, List.class);
                    //Logger.info("Message Setter "+methodname+" found in "+response.getClass().getSimpleName());
                } catch (NoSuchMethodException e) {
                    //Logger.info("Message Setter "+methodname+" not found in "+response.getClass().getSimpleName()+" Throws "+e.toString());
                    method = null;
                }

                if (method != null) {
                    try {
                        method.invoke(response, messageList);
                        Logger.info("Message Setter " + methodname + " executed.");
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        //Logger.info("Message Setter "+methodname+" could not be executed in "+response.getClass().getSimpleName()+" Throws "+e.toString());
                    }
                }

                // Set response status
                try {
                    methodname = "setStatus";
                    method = response.getClass().getMethod(methodname, int.class);
                    //Logger.info("Response Status Setter "+methodname+" found in "+response.getClass().getSimpleName());
                } catch (NoSuchMethodException e) {
                    //Logger.info("Response Status Setter "+methodname+" not found in "+response.getClass().getSimpleName());
                    method = null;
                }

                if (method != null) {
                    try {
                        // Fails if nothing changed or if something changed but generated error messages
                        if ((!changeDetected) || (Message.checkErrorMessageExists(response))) {
                            // reset entity (This will prevent dirty entities from being persisted if a save is called)
                            Logger.info("Resetting "+entity.getClass().getSimpleName()+" during update."+this.getClass().getSimpleName());
                            entity = dbEntity;
                            method.invoke(response, TruResponse.STATUS_FAILURE);
                        } else {
                            method.invoke(response, TruResponse.STATUS_SUCCESS);
                        }
                        Logger.info("Response Status Setter " + methodname + " executed.");
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        //Logger.info("Response Status Setter "+methodname+" could not be executed in "+response.getClass().getSimpleName()+" Throws "+e.toString());
                    }
                    // Push entity into response
                    response = pushEntityIntoResponse(entity, response);
                }

                return response;

            } else {
                Logger.info("During Update: Read (" + methodname + ") for " + entity.getClass().getSimpleName() + " returned following Id: " + ((idValue == null)?"null":idValue));
                if(areChangedFieldsRelevant(request)){
                    Logger.info("Found changed fields in "+entity.getClass().getSimpleName()+". Calling "+((asChild && (parent != null)) ? "createAsChild":"create"));
                    return (TruResponse) ((asChild && (parent != null)) ? createAsChild(adjustRequest(request).get(0), parent) : create(adjustRequest(request).get(0)));
                }
                else return response;
            }
        }
        // nothing changed
        return response;

    }

    public TruRequest copyChangedFields(TruRequest fromRequest, TruRequest toRequest) {
        toRequest.setChangedFields(fromRequest.getChangedFields());
        return toRequest;
    }

    public TruRequest copyDeleteFields(TruRequest fromRequest, TruRequest toRequest) {
        toRequest.setDeleteFields(fromRequest.getDeleteFields());
        return toRequest;
    }

    public TruResponse updateAsChild(TruRequest request, Model parent) {
        // Update the child
        if(parent != null){return update(request, Boolean.TRUE, parent);}
        else {
            Logger.info(this.getClass().getSimpleName()+".updateAsChild: Invoked with null Parent! Returning null response");
            TruResponse response = null;
            try {
                response = (TruResponse) Class.forName(this.getResponseClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Logger.info("Exception " + e.getMessage() + " triggered while instantiating " + this.getResponseClassName());
                return null;
            }
            return response;
        }
    }

    public List<TruResponse> updateAsChildren(TruRequest request, Model parent) {
        List<TruResponse> responseList = new ArrayList<>();
        responseList.add(updateAsChild(request, parent));
        return responseList;
    }

    public String getRequestClassName() {
        if(entity == null) {
            try {
                entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                return null;
            }
        }
        return (entity.getClass().getSimpleName() + "Request");
    }

    public List<Object> adjustRequest(TruRequest request) {

        Logger.info("adjustRequest: request.getClass().getSimpleName() = "+request.getClass().getSimpleName());
        Logger.info("adjustRequest: getRequestClassName() = "+getRequestClassName());

        // direct match
        if (request.getClass().getSimpleName().equals(getRequestClassName())) {
            List<Object> convertedRequest = new ArrayList<>();
            convertedRequest.add(request);
            return convertedRequest;
        } else {

            Field field = null;
            String methodname = "";
            Method method = null;

            // input should have required request as an attribute
            try {
                field = request.getClass().getDeclaredField(Character.toLowerCase(getRequestClassName().charAt(0)) + getRequestClassName().substring(1));
                methodname = "get" + getRequestClassName();
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
            catch (NoSuchFieldException e){
            // maybe it's a list attribute?
                try {
                    field = request.getClass().getDeclaredField(Character.toLowerCase(getRequestClassName().charAt(0)) + getRequestClassName().substring(1) + "List");
                    methodname = "get" + getRequestClassName() + "List";
                } catch (NoSuchFieldException | NullPointerException ee) {
                    ee.printStackTrace();
                    return null;
                }
            }

            try {
                method = request.getClass().getMethod(methodname);
            } catch (NoSuchMethodException | NullPointerException e) {
                e.printStackTrace();
                return null;
            }

            try {
                if (Collection.class.isAssignableFrom(field.getType())) {
                    return (List<Object>) method.invoke(request);
                } else {
                    List<Object> convertedRequest = new ArrayList<Object>();
                    convertedRequest.add(method.invoke(request));
                    return convertedRequest;
                }
            } catch (ClassCastException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void setKeyField(Model entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                keyField = field;
                break;
            }
        }
    }

    private Field getKeyField() {
        return keyField;
    }

    // must be called with the correct service class
    public Boolean isNew(TruResponse response) {

        // am I the right service class?
        if(!(entity.getClass().getSimpleName().equals(response.getEntity().getClass().getSimpleName()))) {
            throw new IllegalArgumentException(entity.getClass().getSimpleName()+" and "+response.getEntity().getClass().getSimpleName()+" do not match");
        }

        if (getKeyField() == null) {
            setKeyField(entity);
        }

        Method method = null;
        String methodname = "";
        Long value = 0L;

        try {
            method = null;
            methodname = "get";
            methodname += keyField.getName().substring(0, 1).toUpperCase() + keyField.getName().substring(1);
            method = response.getEntity().getClass().getMethod(methodname);
            //Logger.info("Getter "+methodname+" found in "+request.getClass().getSimpleName());
        } catch (NoSuchMethodException | NullPointerException e) {
            //Logger.info("Getter "+methodname+" not found in "+request.getClass().getSimpleName()+" Throws "+e.toString());
            e.printStackTrace();
            return Boolean.TRUE;
        }

        try {
            value = (Long) method.invoke(response.getEntity());
            //Logger.info("Getter "+methodname+" executed.");
        } catch (NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            //Logger.info("Getter "+methodname+" could not be executed in "+request.getClass().getSimpleName()+" Throws "+e.toString());
            e.printStackTrace();
            return Boolean.TRUE;
        }

        if (value != null && value > 0) return Boolean.FALSE;
        else return Boolean.TRUE;

    }

    public List<TruResponse> filterNew(List<TruResponse> responseList) {

        ListIterator<TruResponse> iterator = responseList.listIterator();
        while (iterator.hasNext()) {

            TruResponse response = iterator.next();

            // am I the right service class?
            if(!(entity.getClass().getSimpleName().equals(response.getEntity().getClass().getSimpleName()))) {
                iterator.remove();
            }

            // am I new?
            if (!isNew(response)) iterator.remove();

        }

        return responseList;
    }

    // Use to add more children to a parent for a 1:N relationship between Parent and Child
    public Boolean appendChildrenReference(List<TruResponse> childrenResponse, String childListAttributeName) {

        Logger.info("Entered "+this.getClass().getSimpleName()+".appendChildrenReference with "+childrenResponse.size()+" response elements");
        // check if children exist
        List<Model> children = new ArrayList<>();
        for (TruResponse each : childrenResponse) {
            if (each.getEntity() != null) {
                Logger.info("Found entity: "+each.getEntity().getClass().getSimpleName()+" in "+each.getClass().getSimpleName());
                children.add(each.getEntity());
            }
        }

        Logger.info("children.size() = "+children.size());
        if (children.size() > 0) {
            java.lang.reflect.Method method = null;
            String methodname = "";

            // identify child reference getter method in parent
            try {
                methodname = "get";
                methodname += childListAttributeName.substring(0, 1).toUpperCase() + childListAttributeName.substring(1);
                method = entity.getClass().getMethod(methodname);
                Logger.info("Children reference getter " + methodname + " found in " + entity.getClass().getSimpleName());
            } catch (NoSuchMethodException e) {
                Logger.info("Children reference getter " + methodname + " not found in " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                return Boolean.FALSE;
            }

            // get current child reference list from parent
            try {
                children.addAll((Collection<? extends Model>) method.invoke(entity));
                Logger.info("Children reference getter " + methodname + " executed. Current no of children: "+children.size());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logger.info("Children reference getter " + methodname + " could not be executed in " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                return Boolean.FALSE;
            }

            // identify child reference setter method in parent
            try {
                methodname = "set";
                methodname += childListAttributeName.substring(0, 1).toUpperCase() + childListAttributeName.substring(1);
                method = entity.getClass().getMethod(methodname, List.class);
                Logger.info("Children reference setter " + methodname + " found in " + entity.getClass().getSimpleName());
            } catch (NoSuchMethodException e) {
                Logger.info("Children reference setter " + methodname + " not found in " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                return Boolean.FALSE;
            }

            // set child reference in parent
            try {
                method.invoke(entity, children);
                Logger.info("Children reference setter " + methodname + " executed with "+children.size()+" children");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logger.info("Children reference setter " + methodname + " could not be executed in " + entity.getClass().getSimpleName() + " Throws " + e.toString());
                return Boolean.FALSE;
            }

        }

        Logger.info("Exited "+this.getClass().getSimpleName()+".appendChildrenReference. childrenResponse="+childrenResponse.getClass().getSimpleName());
        return Boolean.TRUE;

    }

    public Boolean areChangedFieldsRelevant(TruRequest request){

        Logger.info("Entered "+this.getClass().getSimpleName()+".areChangedFieldsRelevant with request type "+
                ((request == null)?"null":request.getClass().getSimpleName())+
                ". Changed Field Count: "+((request == null)?"0":request.getChangedFields().size()));

        if(request == null || request.getChangedFields().size() == 0) return Boolean.FALSE;

        if(entity == null){
            try {
                entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                return Boolean.FALSE;
            }
        }

        List<String> entityFields = new ArrayList<>();
        for(Field field:entity.getClass().getDeclaredFields()){entityFields.add(field.getName());}
        if(!Collections.disjoint(entityFields, request.getChangedFields())){return Boolean.TRUE;}
        else return Boolean.FALSE;

    }

    // returns true only if the object was successfully created/modified and needs to be persisted
    public Boolean isChanged(TruResponse response){
        if(response == null) return Boolean.FALSE;
        if(response.getMessages() == null) return Boolean.FALSE;
        if((response.getMessages().size() == 0) && (response.getStatus() == TruResponse.STATUS_FAILURE)) return Boolean.FALSE;
        return Boolean.TRUE;
    }

    // attributeName = entity field name which needs to be joined
    // request = current request parameters (request will be casted to entity request type)
    // override in inherited service class and provide an implementation for @JoinColumn annotation
    public Model requestJoinEntity(String attributeName, TruRequest request){
        return null;
    }

    private Boolean isCompatible(Model childEntity, String attributeName) {

        Field field = null;
        String childClassName = "";

        try {
            field = entity.getClass().getDeclaredField(attributeName);
        } catch (NoSuchFieldException | NullPointerException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

        try {
            childClassName = childEntity.getClass().getName();
            Logger.info(this.getClass().getSimpleName()+".isCompatible: childClassName="+childClassName);
        } catch (NullPointerException e){
            e.printStackTrace();
            return Boolean.FALSE;
        }

        // is this a List type?
        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType pType = (ParameterizedType)field.getGenericType();
            Logger.info(this.getClass().getSimpleName()+".isCompatible: attributeName Class="+pType.getActualTypeArguments()[0].toString());
            return ((pType.getActualTypeArguments()[0].toString().toLowerCase().contains(childClassName.toLowerCase()))?Boolean.TRUE:Boolean.FALSE);
        }
        else {
            Logger.info(this.getClass().getSimpleName()+".isCompatible: attributeName Class="+field.getType().getName());
            return ((field.getType().getName().toLowerCase().equals(childClassName.toLowerCase()))? Boolean.TRUE:Boolean.FALSE);
        }

    }

    public TruResponse delete(TruRequest request) {

        TruResponse response = null;
        try {
            response = (TruResponse) Class.forName(this.getResponseClassName()).newInstance();
        } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Logger.info("Exception " + e.getMessage() + " triggered while instantiating " + this.getResponseClassName());
            return null;
        }

        if(isDeleteRelevant(request)){

            if(entity == null) {
                try {
                    entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
                } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                    return response;
                }
            }

            List<Long> deleteIds = request.getDeleteIdsByEntityName(entity.getClass().getSimpleName());
            if((deleteIds != null) && (deleteIds.size()) > 0) {
                Logger.info(this.getClass().getSimpleName()+".delete: deleteIds has "+deleteIds.size()+" Ids");
                // get entities to be deleted
                List<TruResponse> toDeleteEntityList = readById(deleteIds);

                // container for service class references
                Map<String,TruService> dInstanceMap = new HashMap<String,TruService>();
                // container for dependencies
                Map<String,Boolean> dependencies = getDeleteDependencies();

                Logger.info(this.getClass().getSimpleName()+".delete: toDeleteEntityList has "+toDeleteEntityList.size()+" entities");
                for(TruResponse eachResponse:toDeleteEntityList){

                    Boolean first = Boolean.TRUE;
                    if(eachResponse != null && eachResponse.getEntity() != null){

                        // message container
                        List<Message> thisEntityMessages = new ArrayList<Message>();
                        // for each dependency
                        for(Map.Entry<String,Boolean> each:dependencies.entrySet()){

                            // instantiate dependent service class on first run
                            if(first){
                                if(!(dInstanceMap.containsKey(each.getKey()+"Service"))){

                                    try {
                                        TruService serviceClass = (TruService) Class.forName(each.getKey()+"Service").newInstance();
                                        dInstanceMap.put(each.getKey()+"Service",(TruService) serviceClass);
                                    } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                                        Logger.info("Exception " + e.toString() + " triggered while instantiating " + each.getKey()+"Service");
                                    }

                                }
                                first = Boolean.FALSE;
                            }

                            if(dInstanceMap.get(each.getKey()+"Service") instanceof TruService){
                                // check delete dependencies
                                thisEntityMessages.addAll(dInstanceMap.get(each.getKey()+"Service").checkDeleteDependencies(eachResponse.getEntity(),getDeleteDependentEntities(eachResponse.getEntity(),each)));
                            }

                        }

                        // pass through validation
                        thisEntityMessages.addAll(validateDelete(eachResponse.getEntity()));
                        // collect validation messages
                        for(Message m:thisEntityMessages){addMessage(response,m);}
                        // check if validation allows delete
                        if(!Message.checkErrorMessageExists(thisEntityMessages)){
                            // delete entity
                            Logger.info(eachResponse.getEntity().getClass().getSimpleName()+".delete: Deleting ...");
                            if(((Model)eachResponse.getEntity()).delete()){
                                response.setStatus(TruResponse.STATUS_SUCCESS);
                                Logger.info(eachResponse.getEntity().getClass().getSimpleName()+".delete: Success. Response="+response.getClass().getSimpleName());
                            }
                            else{
                                response.setStatus(TruResponse.STATUS_FAILURE);
                                Logger.info(eachResponse.getEntity().getClass().getSimpleName()+".delete: Failed. Response="+response.getClass().getSimpleName());
                            }
                        }
                        else{
                            Logger.info(eachResponse.getEntity().getClass().getSimpleName()+".delete: Error reported! Will not delete ...");
                            response.setStatus(TruResponse.STATUS_FAILURE);
                        }

                    }
                }
            }

        }
        // Nothing to delete --> Caller has to ensure children delete are invoked
        else{response.setStatus(TruResponse.STATUS_SUCCESS);}

        return response;
    }

    // if validate returns an error message, delete will not be allowed
    public List<Message> validateDelete(Model entity){
        List<Message> deleteMessages = new ArrayList<>();
        return deleteMessages;
    }

    public Boolean isDeleteRelevant(TruRequest request){
        if(request == null) return Boolean.FALSE;

        Logger.info(this.getClass().getSimpleName()+".isDeleteRelevant: Request="+request.getClass().getSimpleName());

        if(entity == null) {
            try {
                entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                return Boolean.FALSE;
            }
        }

        Logger.info(this.getClass().getSimpleName()+".isDeleteRelevant.request.getDeleteFields() has "+request.getDeleteFields().size()+" entries");

        for(Map<String,Long> each:request.getDeleteFields()){
            if(each.containsKey(entity.getClass().getSimpleName().toLowerCase())) {
                Logger.info(this.getClass().getSimpleName()+".isDeleteRelevant: Returned TRUE for key "+entity.getClass().getSimpleName().toLowerCase());
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    // Map<dependentEntityName, Boolen.TRUE> if 1:Many dependency (i.e. List)
    // Map<dependentEntityName, Boolen.FALSE> if 1:1 dependency (i.e. single entity instance)
    // Assumes Service Class Name = EntityName+"Service"
    // Override and populate the map to ensure children are checked before parent is deleted
    public Map<String,Boolean> getDeleteDependencies(){
        Map<String,Boolean> dependencies = new HashMap<>();
        return dependencies;
    }

    public List<Model> getDeleteDependentEntities (Model parent, Map.Entry<String,Boolean> eachDependency){

        Method method = null;
        String methodname = "";
        List<Model> relatedEntityList = new ArrayList<Model>();
        // get related entities
        if(eachDependency.getValue()){
            try {
                methodname = "get";
                methodname += eachDependency.getKey()+"List";
                method = parent.getClass().getMethod(methodname);
                Logger.info(parent.getClass().getSimpleName()+"."+methodname+" found");
            } catch (NoSuchMethodException e) {
                Logger.info(parent.getClass().getSimpleName()+"."+methodname+" not found");
            }

            try {
                relatedEntityList = (List<Model>) method.invoke(parent);
                Logger.info(parent.getClass().getSimpleName()+"."+methodname+" executed");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logger.info(parent.getClass().getSimpleName()+"."+methodname+" not executed");
                e.printStackTrace();
            }
        }
        // get related entity
        else{
            try {
                methodname = "get";
                methodname += eachDependency.getKey();
                method = parent.getClass().getMethod(methodname);
                Logger.info(parent.getClass().getSimpleName()+"."+methodname+" found");
            } catch (NoSuchMethodException e) {
                Logger.info(parent.getClass().getSimpleName()+"."+methodname+" not found");
            }

            try {
                Model relatedEntity = (Model) method.invoke(parent);
                relatedEntityList.add(relatedEntity);
                Logger.info(parent.getClass().getSimpleName()+"."+methodname+" executed");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logger.info(parent.getClass().getSimpleName()+"."+methodname+" not executed");
                e.printStackTrace();
            }
        }
        return relatedEntityList;
    }

    // will recursively check the entire delete dependency tree
    public List<Message> checkDeleteDependencies(Model parent, List<Model> relatedEntityList){
        List<Message> m = new ArrayList<Message>();

        // container for service class references
        Map<String,TruService> dInstanceMap = new HashMap<String,TruService>();

        // get sub-dependencies
        Map<String,Boolean> dependencies = getDeleteDependencies();

        // for each entity
        Boolean first = Boolean.TRUE;
        for(Model e:relatedEntityList){

            // for each sub-dependency
            for(Map.Entry<String,Boolean> each:dependencies.entrySet()) {

                // instantiate service class on first run
                if(first){
                    if(!(dInstanceMap.containsKey(each.getKey()+"Service"))){

                        try {
                            TruService serviceClass = (TruService) Class.forName(each.getKey()+"Service").newInstance();
                            dInstanceMap.put(each.getKey()+"Service",(TruService) serviceClass);
                        } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                            Logger.info("Exception " + ex.toString() + " triggered while instantiating " + each.getKey()+"Service");
                            ex.printStackTrace();
                        }

                    }
                    first = Boolean.FALSE;
                }

                if(dInstanceMap.get(each.getKey()+"Service") instanceof TruService){
                    // check delete dependencies
                    m.addAll(dInstanceMap.get(each.getKey()+"Service").checkDeleteDependencies(e,getDeleteDependentEntities(e,each)));
                }

            }

            // check yourself
            m.addAll(validateDelete(e));

        }

        return m;
    }

    // orderBy = attribute name by which to order the read
    // direction = 'DESC' for descending, 'ASC' for ascending. Default is descending
    public List<TruResponse> readByAttribute(List<Map<String,String>> attrNameValueList, String orderBy, String direction) {

        if (entity == null) {
            try {
                entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                return null;
            }
        }

        java.lang.reflect.Method method = null;

        ExpressionList<?> query = getQuery();
        //Logger.info("ExpressionList<?> query = "+query.getClass().getSimpleName());

        // build query
        if(query != null){
            for(Map<String,String> each:attrNameValueList){
                if(each.keySet().size() > 0 && each.values().size() > 0){
                    query.add(eq(each.keySet().toArray()[0].toString(),each.values().toArray()[0].toString()));
                    if(orderBy != null){
                        // decide ordering
                        if(direction == null || direction.toLowerCase() != "asc"){
                            query.orderBy().desc(orderBy);
                        }
                        else query.orderBy().asc(orderBy);
                    }
                }
            }
        }

        return createReadResponse((List<Model>) query.findList());

    }

    public List<TruResponse> readByAttribute(List<Map<String,String>> attrNameValueList){

        return readByAttribute(attrNameValueList, null, null);
/*
        if (entity == null) {
            try {
                entity = (Model) Class.forName(this.getEntityClassName()).newInstance();
            } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                Logger.info("Exception " + e.toString() + " triggered while instantiating " + this.getEntityClassName());
                return null;
            }
        }

        java.lang.reflect.Method method = null;

        ExpressionList<?> query = getQuery();
        //Logger.info("ExpressionList<?> query = "+query.getClass().getSimpleName());

        if(query != null){
            for(Map<String,String> each:attrNameValueList){
                if(each.keySet().size() > 0 && each.values().size() > 0){
                    query.add(eq(each.keySet().toArray()[0].toString(),each.values().toArray()[0].toString()));
                }
            }
        }
        return createReadResponse((List<Model>) query.findList());
*/

/*
        // Check read method exists
        try {
            method = entity.getClass().getMethod("readByAttribute", List.class);
            //Logger.info("Read "+methodname+" found in "+entity.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Logger.info("Method readByAttribute not found in "+entity.getClass().getSimpleName());
            method = null;
        }

        if (method != null) {
            try {
                entityList = (List<Model>) method.invoke(entity, attrNameValueList);
                Logger.info(entity.getClass().getSimpleName()+".readByAttribute executed");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                //Logger.info("Read "+methodname+" could not be executed in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
            }
            return createReadResponse(entityList);
        }

        return null;
*/
    }

    private ExpressionList<Model> getQuery() {

        java.lang.reflect.Method method = null;

        // Check method exists
        try {
            method = entity.getClass().getMethod("getQuery");
            //Logger.info("Read "+methodname+" found in "+entity.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Logger.info("Method getQuery not found in "+entity.getClass().getSimpleName());
            method = null;
        }

        if (method != null) {
            try {
                Logger.info(entity.getClass().getSimpleName()+".getQuery executed");
                return (ExpressionList<Model>) method.invoke(entity);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                //Logger.info("Read "+methodname+" could not be executed in "+entity.getClass().getSimpleName()+" Throws "+e.toString());
            }
        }
        return null;
    }

}