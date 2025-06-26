package com.mobox.streamerordercreationservice.controler;

import com.mobox.streamerordercreationservice.model.PersonsFavoriteColor;
import com.mobox.streamerordercreationservice.service.FavoriteColorEventProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/favorite-colors")
public class CreateFavoriteColor {

    private final FavoriteColorEventProducer favoriteColorEventProducer;

    public CreateFavoriteColor(FavoriteColorEventProducer favoriteColorEventProducer) {
        this.favoriteColorEventProducer = favoriteColorEventProducer;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<String>> createFavoriteColor(@RequestBody PersonsFavoriteColor color) {
        return favoriteColorEventProducer.publishFavoriteColor(color)
                .thenApply(publishedColor -> ResponseEntity.ok()
                        .body("Favorite color submitted successfully: " + publishedColor))
                .exceptionally(throwable -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to submit favorite color: " + throwable));
    }

}
