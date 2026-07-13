package com.angelodibello.mediaarchive.dto;

import com.angelodibello.mediaarchive.model.MediaAsset;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MediaAssetResponse {

    private Long id;
    private String filename;
    private String title;
    private String team;
    private String player;
    private String stadium;
    private LocalDate gameDate;
    private String codec;
    private Long fileSizeBytes;
    private LocalDateTime uploadedAt;
    private String status;

    public MediaAssetResponse() {
    }

    public MediaAssetResponse(
            Long id,
            String filename,
            String title,
            String team,
            String player,
            String stadium,
            LocalDate gameDate,
            String codec,
            Long fileSizeBytes,
            LocalDateTime uploadedAt,
            String status
    ) {
        this.id = id;
        this.filename = filename;
        this.title = title;
        this.team = team;
        this.player = player;
        this.stadium = stadium;
        this.gameDate = gameDate;
        this.codec = codec;
        this.fileSizeBytes = fileSizeBytes;
        this.uploadedAt = uploadedAt;
        this.status = status;
    }

    public static MediaAssetResponse fromEntity(MediaAsset asset) {
        return new MediaAssetResponse(
                asset.getId(),
                asset.getFilename(),
                asset.getTitle(),
                asset.getTeam(),
                asset.getPlayer(),
                asset.getStadium(),
                asset.getGameDate(),
                asset.getCodec(),
                asset.getFileSizeBytes(),
                asset.getUploadedAt(),
                asset.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public String getTeam() {
        return team;
    }

    public String getPlayer() {
        return player;
    }

    public String getStadium() {
        return stadium;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public String getCodec() {
        return codec;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getStatus() {
        return status;
    }
}