package com.sharmachait.ws.models.messages.responseMessages.movement;
import com.sharmachait.ws.models.messages.MessageType;
import lombok.*;


@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementResponse{
    private MessageType type;
    private MovementResponsePayload payload;
}
