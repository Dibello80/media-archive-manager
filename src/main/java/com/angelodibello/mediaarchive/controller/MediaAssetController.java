package com.angelodibello.mediaarchive.controller;

import com.angelodibello.mediaarchive.model.MediaAsset;
import com.angelodibello.mediaarchive.service.MediaAssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/api/media-assets")
public class MediaAssetController {

    private final MediaAssetService service;

    public MediaAssetController(MediaAssetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MediaAsset> create(
            @RequestBody MediaAsset mediaAsset
    ) {
        MediaAsset savedAsset = service.save(mediaAsset);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedAsset);
    }

    @GetMapping
    public List<MediaAsset> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaAsset> findById(
            @PathVariable Long id
    ) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String player,
            @RequestParam(required = false) String stadium,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate gameDate,
            @RequestParam(required = false) String codec
    ) {
        try {
            MediaAsset savedAsset = service.upload(
                    file,
                    title,
                    team,
                    player,
                    stadium,
                    gameDate,
                    codec
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedAsset);

        } catch (IllegalStateException exception) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(exception.getMessage());

        } catch (IOException exception) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("The video could not be stored.");
        }
    }

    @GetMapping("/search/team")
    public List<MediaAsset> searchByTeam(
            @RequestParam String team
    ) {
        return service.searchByTeam(team);
    }

    @GetMapping("/search/player")
    public List<MediaAsset> searchByPlayer(
            @RequestParam String player
    ) {
        return service.searchByPlayer(player);
    }

    @GetMapping("/search/title")
    public List<MediaAsset> searchByTitle(
            @RequestParam String title
    ) {
        return service.searchByTitle(title);
    }

    @GetMapping("/search/stadium")
    public List<MediaAsset> searchByStadium(
            @RequestParam String stadium
    ) {
        return service.searchByStadium(stadium);
    }
}