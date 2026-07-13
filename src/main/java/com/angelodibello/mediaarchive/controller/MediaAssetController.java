package com.angelodibello.mediaarchive.controller;

import com.angelodibello.mediaarchive.model.MediaAsset;
import com.angelodibello.mediaarchive.service.MediaAssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import com.angelodibello.mediaarchive.dto.MediaAssetResponse;

import java.io.IOException;
import java.time.LocalDate;

import java.util.List;
import com.angelodibello.mediaarchive.dto.UpdateMediaAssetRequest;

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
    ) throws IOException {

        service.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/upload")
    public ResponseEntity<MediaAssetResponse> upload(

            @RequestParam("file") MultipartFile file,

            @RequestParam("title") String title,

            @RequestParam(required = false) String team,

            @RequestParam(required = false) String player,

            @RequestParam(required = false) String stadium,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate gameDate,

            @RequestParam(required = false) String codec

    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "A video file is required."
            );
        }

        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException(
                    "Title is required."
            );
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null ||
                !originalFilename.toLowerCase().endsWith(".mp4")) {

            throw new IllegalArgumentException(
                    "Only MP4 files are supported."
            );
        }

        MediaAsset savedAsset = service.upload(
                file,
                title.trim(),
                team,
                player,
                stadium,
                gameDate,
                codec
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MediaAssetResponse.fromEntity(savedAsset));

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
    @GetMapping("/page")
    public Page<MediaAsset> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return service.findAllPaged(
                page,
                size,
                sortBy,
                direction
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MediaAssetResponse> updateMetadata(
            @PathVariable Long id,
            @RequestBody UpdateMediaAssetRequest request
    ) {
        MediaAsset updatedAsset =
                service.updateMetadata(id, request);

        return ResponseEntity.ok(
                MediaAssetResponse.fromEntity(updatedAsset)
        );
    }
}