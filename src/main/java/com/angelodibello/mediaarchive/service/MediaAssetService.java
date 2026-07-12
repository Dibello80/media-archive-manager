package com.angelodibello.mediaarchive.service;

import com.angelodibello.mediaarchive.model.MediaAsset;
import com.angelodibello.mediaarchive.repository.MediaAssetRepository;
import com.angelodibello.mediaarchive.storage.FileStorageService;
import com.angelodibello.mediaarchive.util.ChecksumUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MediaAssetService {

    private final MediaAssetRepository repository;
    private final FileStorageService fileStorageService;

    public MediaAssetService(
            MediaAssetRepository repository,
            FileStorageService fileStorageService
    ) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    public MediaAsset upload(
            MultipartFile file,
            String title,
            String team,
            String player,
            String stadium,
            LocalDate gameDate,
            String codec
    ) throws IOException {

        Path storedFile = fileStorageService.store(file);

        try {
            String checksum =
                    ChecksumUtil.calculateSha256(storedFile);

            Optional<MediaAsset> existingAsset =
                    repository.findByChecksum(checksum);

            if (existingAsset.isPresent()) {
                fileStorageService.delete(storedFile);

                throw new IllegalStateException(
                        "This video has already been uploaded."
                );
            }

            MediaAsset mediaAsset = new MediaAsset(
                    file.getOriginalFilename(),
                    title,
                    team,
                    player,
                    stadium,
                    gameDate,
                    codec,
                    file.getSize(),
                    storedFile.toString(),
                    checksum,
                    LocalDateTime.now(),
                    "ARCHIVED"
            );

            return repository.save(mediaAsset);

        } catch (IOException | RuntimeException exception) {
            fileStorageService.delete(storedFile);
            throw exception;
        }
    }

    public MediaAsset save(MediaAsset mediaAsset) {
        return repository.save(mediaAsset);
    }

    public List<MediaAsset> findAll() {
        return repository.findAll();
    }

    public Optional<MediaAsset> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<MediaAsset> findByChecksum(String checksum) {
        return repository.findByChecksum(checksum);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    public List<MediaAsset> searchByTeam(String team) {
        return repository.findByTeamContainingIgnoreCase(team);
    }

    public List<MediaAsset> searchByPlayer(String player) {
        return repository.findByPlayerContainingIgnoreCase(player);
    }

    public List<MediaAsset> searchByTitle(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    public List<MediaAsset> searchByStadium(String stadium) {
        return repository.findByStadiumContainingIgnoreCase(stadium);
    }
}