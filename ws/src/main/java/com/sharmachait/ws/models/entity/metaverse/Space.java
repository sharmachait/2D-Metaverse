package com.sharmachait.ws.models.entity.metaverse;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Space {
    private String id;
    private String name;
    private int width;
    private int height;
    private String thumbnail;
    private String ownerId;
    private String gameMapId;
    List<String> spaceElementIds;
}
