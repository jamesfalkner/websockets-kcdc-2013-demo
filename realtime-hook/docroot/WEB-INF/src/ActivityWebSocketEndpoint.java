import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HtmlUtil;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

@ServerEndpoint(value = "/websocket/activity")
public class ActivityWebSocketEndpoint {

    private Session session;
    private static final Set<ActivityWebSocketEndpoint> connections =
        new CopyOnWriteArraySet<>();

    public ActivityWebSocketEndpoint() {

    }

    @OnMessage
    public void onMessage(String msg) throws JSONException {
        JSONObject msgObj = JSONFactoryUtil.createJSONObject(msg);
        String command = msgObj.getString("command");
        if ("switch".equals(command)) {
            useNode.set(!useNode.get());
            if (useNode.get()) {
                System.out.println("SWITCHING TO NODE INTERMEDIARY");
            } else {
                System.out.println("SWITCHING TO JAVA DIRECT");
            }
        }

    }

    public static void sendMsg(String msg) throws Exception {
        if (useNode.get()) {
            sendToNode(msg);
        } else {
            for (ActivityWebSocketEndpoint client : connections) {
                System.out.println("JAVA[" + Thread.currentThread().getName()
                    + "] sending " + msg);
                client.session.getAsyncRemote().sendText(msg);
            }
        }
    }

    @OnOpen
    public void open(Session session) {
        this.session = session;
        connections.add(this);
    }


    @OnClose
    public void end(Session session, CloseReason reason) {
        System.out.println("JAVA: CLOSING ENDPOINT " + session.getId() + ": "
            + reason.getReasonPhrase());
        connections.remove(this);
    }

    @OnError
    public void err(Session session, Throwable throwable) {
        System.out.println("JAVA: UH OH: " + session.getId() + ": " +
            throwable.getMessage());
    }

    private static AtomicBoolean useNode = new AtomicBoolean(false);

    private static void sendToNode(String msg) throws Exception {
        URL node = new URL("http://localhost:3000/postActivity?activity=" +
            HtmlUtil.escapeURL(msg));
        node.openConnection().getInputStream().close();
    }

}
