package com.angelodibello.mediaarchive.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "media_assets")
public class MediaAsset {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String title;

    private String team;

    private String player;

    private String stadium;

    private LocalDate gameDate;

    private String codec;

    private Long fileSizeBytes;

    @Column(nullable = false)
    private String storagePath;

    @Column(unique = true, nullable = false)
    private String checksum;

    private LocalDateTime uploadedAt;

    private String status;

    public MediaAsset() {
    }

    public MediaAsset(
            String filename,
            String title,
            String team,
            String player,
            String stadium,
            LocalDate gameDate,
            String codec,
            Long fileSizeBytes,
            String storagePath,
            String checksum,
            LocalDateTime uploadedAt,
            String status
    ) {
        this.filename = filename;
        this.title = title;
        this.team = team;
        this.player = player;
        this.stadium = stadium;
        this.gameDate = gameDate;
        this.codec = codec;
        this.fileSizeBytes = fileSizeBytes;
        this.storagePath = storagePath;
        this.checksum = checksum;
        this.uploadedAt = uploadedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDate gameDate) {
        this.gameDate = gameDate;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}