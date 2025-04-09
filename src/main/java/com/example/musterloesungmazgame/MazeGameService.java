package com.example.musterloesungmazgame;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.math.BigDecimal;

public class MazeGameService {

    private final DefaultApi api;
    private BigDecimal currentGameId;

    public MazeGameService() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("https://mazegame.rinderle.info");
        this.api = new DefaultApi(apiClient);
    }

    public GameDto createNewGame(String groupName) {
        GameInputDto input = new GameInputDto();
        input.setGroupName(groupName);

        try {
            GameDto game = api.gamePost(input); // ✅ korrekt
            currentGameId = game.getGameId();   // ✅ BigDecimal
            System.out.println("Spiel gestartet mit ID: " + currentGameId);
            return game;
        } catch (Exception e) {
            System.err.println("Fehler beim Spielstart: " + e.getMessage());
            return null;
        }
    }

    public MoveDto move(DirectionDto direction) {
        if (currentGameId == null) {
            throw new IllegalStateException("Kein aktives Spiel!");
        }

        MoveInputDto moveInput = new MoveInputDto();
        moveInput.setDirection(direction);

        try {
            MoveDto move = api.gameGameIdMovePost(currentGameId, moveInput); // ✅ korrekt
            PositionDto pos = move.getPositionAfterMove();
            System.out.println("Neue Position: X=" + pos.getPositionX() + ", Y=" + pos.getPositionY());
            System.out.println("Move-Status: " + move.getMoveStatus());
            return move;
        } catch (Exception e) {
            System.err.println("Fehler beim Bewegen: " + e.getMessage());
            return null;
        }
    }
}
