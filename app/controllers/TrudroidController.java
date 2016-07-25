package controllers;

import com.google.api.client.util.Base64;
import com.google.protobuf.InvalidProtocolBufferException;
import in.trujobs.proto.TestMessage;
import play.Logger;
import play.mvc.Result;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

/**
 * Created by zero on 25/7/16.
 */
public class TrudroidController {
    public static Result getTestProto() {
        TestMessage testMessage = null;
        try {
            TestMessage.Builder pseudoTestMessage = TestMessage.newBuilder();
            pseudoTestMessage.setTestName("Testing");
            pseudoTestMessage.setTestPage("Page 1");

            testMessage = testMessage.parseFrom(Base64.decodeBase64(Base64.encodeBase64String(pseudoTestMessage.build().toByteArray())));
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (testMessage == null) {
            Logger.info("Invalid message");
            return badRequest();
        }

        return ok(Base64.encodeBase64String(testMessage.toByteArray()));
    }
}
