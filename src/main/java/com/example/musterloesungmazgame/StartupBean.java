package com.example.musterloesungmazgame;

import jakarta.annotation.PostConstruct;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.GameDto;
import org.openapitools.client.model.GameInputDto;
import org.springframework.stereotype.Component;
import com.example.musterloesungmazgame.solver.MazeSolver;

@Component
public class StartupBean {

    @PostConstruct
    public void init() {
        DefaultApi api = new DefaultApi();

        GameInputDto input = new GameInputDto();
        input.setGroupName("TeamGewinnerDerHerzen - Artur Schmidt, Diana Messmann");

        try {
            GameDto game = api.gamePost(input);
            System.out.println(game);

            new MazeSolver(api, game).solve();
        } catch (Exception e) {
            System.err.println("Error " + e.getMessage());
        }
    }
}
