package data;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Range;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import burp.api.montoya.ui.contextmenu.WebSocketEditorEvent;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static utils.Utilities.insertPlaceholder;

public class WebSocketContextMenuEventAdapter implements Function<WebSocketContextMenuEvent, List<InitialWebSocketMessage>>
{
    @Override
    public List<InitialWebSocketMessage> apply(WebSocketContextMenuEvent event)
    {
        return event.messageEditorWebSocket().isPresent()
                ? convertSingleMessage(event.messageEditorWebSocket().get())
                : event.selectedWebSocketMessages().stream().map(InitialWebSocketMessage::from).toList();
    }

    private static List<InitialWebSocketMessage> convertSingleMessage(WebSocketEditorEvent webSocketEditorEvent)
    {
        return List.of(convertEventToMessage(webSocketEditorEvent));
    }

    private static InitialWebSocketMessage convertEventToMessage(WebSocketEditorEvent webSocketEditorEvent)
    {
        Optional<Range> range = extractRangeFromEvent(webSocketEditorEvent);

        if (range.isEmpty())
        {
            return InitialWebSocketMessage.from(webSocketEditorEvent.webSocketMessage());
        }

        WebSocketMessage webSocketMessage = webSocketEditorEvent.webSocketMessage();

        ByteArray finalPayload = insertPlaceholder(webSocketMessage.payload(), range.get(), "%s");

        return new InitialWebSocketMessage(webSocketMessage.upgradeRequest(), finalPayload);
    }

    private static Optional<Range> extractRangeFromEvent(WebSocketEditorEvent webSocketEditorEvent)
    {
        Optional<Range> rangeOptional = webSocketEditorEvent.selectionOffsets();

        if (rangeOptional.isEmpty())
        {
            return Optional.empty();
        }

        Range range = rangeOptional.get();
        return range.startIndexInclusive() == range.endIndexExclusive() ? Optional.empty() : Optional.of(range);
    }
}
