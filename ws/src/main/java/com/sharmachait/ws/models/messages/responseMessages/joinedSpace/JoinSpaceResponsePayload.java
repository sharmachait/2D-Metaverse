package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;
import lombok.*;
import java.util.List;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceResponsePayload{
    private List<UserSpawn> users;
    private String token;
    private String userId;
    private String spaceId;
    private int x;
    private int y;
}
