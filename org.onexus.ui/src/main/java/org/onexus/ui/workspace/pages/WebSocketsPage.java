package org.onexus.ui.workspace.pages;

import org.apache.wicket.ajax.WebSocketRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class WebSocketsPage extends WebPage {

    public WebSocketsPage(PageParameters parameters) {
        super(parameters);

        add(new WebSocketBehavior()
        {
            @Override
            protected void onConnect(ConnectedMessage message) {

                String applicationName = message.getApplication().getName();
                String sessionId = message.getSessionId();
                Integer pageId = message.getPageId();

                //TODO OnexusWebApplication.get().getEventSystem().clientConnected(applicationName, sessionId, pageId);
            }

            @Override
            protected void onClose(ClosedMessage message) {

                String applicationName = message.getApplication().getName();
                String sessionId = message.getSessionId();
                Integer pageId = message.getPageId();

                //TODO OnexusWebApplication.get().getEventSystem().clientDisconnected(applicationName, sessionId, pageId);
            }

            @Override
            protected void onMessage(WebSocketRequestHandler handler, TextMessage message) {

                getSession().info("You typed: " + message.getText());
                // handler.add(feedback);
                handler.push("A message pushed by the server by using the WebSocketRequestHandler that is available in WebSocketBehavior#onMessage!");
            }

        });
    }
}
