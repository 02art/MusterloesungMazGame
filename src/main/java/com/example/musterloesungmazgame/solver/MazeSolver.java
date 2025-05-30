package com.example.musterloesungmazgame.solver;

import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.math.BigDecimal;
import java.util.*;

public class MazeSolver {

    private final DefaultApi api;
    private final BigDecimal gameId;

    private PositionDto curPos;
    private GameStatusDto curStatus;

    private static final int SIZE = 5;
    private final Set<String> visited = new HashSet<>();

    public MazeSolver(DefaultApi api, GameDto start) {
        this.api = api;
        this.gameId = start.getGameId();
        this.curPos = start.getPosition();
        this.curStatus = start.getStatus();
        visited.add(key(curPos));
    }

    public void solve() {
        List<MoveDir> path = new ArrayList<>();
        boolean reached = dfsAlgo(curPos, path);

        if (!reached) {
            System.out.println("No valid Path found");
            return;
        }

        try {
            GameDto end = api.gameGameIdGet(gameId);
            System.out.println(end);
        } catch (Exception e) {
            System.err.println("Error retrieving final state:" + e.getMessage());
        }
    }

    private boolean dfsAlgo(PositionDto pos, List<MoveDir> path) {
        if (curStatus == GameStatusDto.SUCCESS) return true;

        List<MoveDir> dirs = new ArrayList<>();
        for (MoveDir d : MoveDir.values()) {
            PositionDto nxt = move(pos, d);
            if (nxt != null && !visited.contains(key(nxt))) dirs.add(d);
        }

        dirs.sort(Comparator.comparingInt(d ->
                distance(Objects.requireNonNull(move(pos, d)), target())));

        for (MoveDir dir : dirs) {
            try {
                MoveDto srv = api.gameGameIdMovePost(gameId, new MoveInputDto().direction(dir.apiDir));

                //System.out.println(srv); view all steps in console

                switch (Objects.requireNonNull(srv.getMoveStatus())) {
                    case BLOCKED, FAILED -> { continue; }
                    default -> { }
                }

                curPos = srv.getPositionAfterMove();
                refreshStatus();

                visited.add(key(curPos));
                path.add(dir);

                if (curStatus == GameStatusDto.SUCCESS) return true;
                if (dfsAlgo(curPos, path)) return true;

                MoveDir backDir = dir.opposite();
                MoveDto back = api.gameGameIdMovePost(gameId, new MoveInputDto().direction(backDir.apiDir));
                System.out.println(back);

                curPos = back.getPositionAfterMove();
                refreshStatus();
                path.add(backDir);

            } catch (Exception e) {
                System.err.println("Error during move: " + e.getMessage());
            }
        }
        return false;
    }

    private void refreshStatus() {
        try {
            curStatus = api.gameGameIdGet(gameId).getStatus();
        } catch (Exception e) {
            System.err.println("Error refreshing game status: " + e.getMessage());
        }
    }

    private PositionDto move(PositionDto p, MoveDir d) {
        try {
            assert p.getPositionX() != null;
            int x = p.getPositionX().intValueExact() + d.dx;
            assert p.getPositionY() != null;
            int y = p.getPositionY().intValueExact() + d.dy;
            if (x < 1 || x > SIZE || y < 1 || y > SIZE) return null;
            return new PositionDto().positionX(BigDecimal.valueOf(x)).positionY(BigDecimal.valueOf(y));
        } catch (Exception e) {
            System.err.println("Error calculating move position: " + e.getMessage());
            return null;
        }
    }

    private int distance(PositionDto a, PositionDto b) {
        assert a.getPositionX() != null;
        assert a.getPositionY() != null;
        assert b.getPositionX() != null;
        assert b.getPositionY() != null;
        return Math.abs(a.getPositionX().intValueExact() - b.getPositionX().intValueExact()) +
                Math.abs(a.getPositionY().intValueExact() - b.getPositionY().intValueExact());
    }

    private PositionDto target() {
        return new PositionDto()
                .positionX(BigDecimal.valueOf(SIZE))
                .positionY(BigDecimal.valueOf(SIZE));
    }

    private String key(PositionDto p) {
        return p.getPositionX() + "-" + p.getPositionY();
    }
}
