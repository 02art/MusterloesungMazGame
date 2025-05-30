package com.example.musterloesungmazgame.solver;

import org.openapitools.client.model.DirectionDto;

public enum MoveDir {

    LEFT (DirectionDto.LEFT , -1,  0),
    UP   (DirectionDto.UP   ,  0, +1),
    RIGHT(DirectionDto.RIGHT, +1,  0),
    DOWN (DirectionDto.DOWN ,  0, -1);

    public final DirectionDto apiDir;
    public final int dx;
    public final int dy;

    MoveDir(DirectionDto apiDir, int dx, int dy) {
        this.apiDir = apiDir;
        this.dx = dx;
        this.dy = dy;
    }

    // For Backtracking
    public MoveDir opposite() {
        return switch (this) {
            case LEFT  -> RIGHT;
            case RIGHT -> LEFT;
            case UP    -> DOWN;
            case DOWN  -> UP;
        };
    }
}
