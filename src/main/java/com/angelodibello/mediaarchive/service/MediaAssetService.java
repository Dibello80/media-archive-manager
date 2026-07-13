package com.angelodibello.mediaarchive.service;

import com.angelodibello.mediaarchive.model.MediaAsset;
import com.angelodibello.mediaarchive.repository.MediaAssetRepository;
import com.angelodibello.mediaarchive.storage.FileStorageService;
import com.angelodibello.mediaarchive.util.ChecksumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.angelodibello.mediaarchive.dto.UpdateMediaAssetRequest;
import com.angelodibello.mediaarchive.exception.MediaAssetNotFoundException;
import org.springframework.util.StringUtils;

@Service
public class MediaAssetService {

    private static final Logger logger =
            LoggerFactory.getLogger(MediaAssetService.class);

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

        logger.info(
                "Starting upload for file: {}",
                file.getOriginalFilename()
        );

        Path storedFile = fileStorageService.store(file);

        try {
            String checksum =
                    ChecksumUtil.calculateSha256(storedFile);

            logger.info(
                    "Checksum calculated for file {}",
                    file.getOriginalFilename()
            );

            Optional<MediaAsset> existingAsset =
                    repository.findByChecksum(checksum);

            if (existingAsset.isPresent()) {

                logger.warn(
                        "Duplicate upload detected for file: {}",
                        file.getOriginalFilename()
                );

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

            logger.info(
                    "Saving media asset '{}' to the database.",
                    mediaAsset.getTitle()
            );

            MediaAsset savedAsset = repository.save(mediaAsset);

            logger.info(
                    "Media asset saved successfully with ID: {}",
                    savedAsset.getId()
            );

            return savedAsset;

        } catch (IllegalStateException exception) {
            fileStorageService.delete(storedFile);
            throw exception;

        } catch (IOException | RuntimeException exception) {
            logger.error(
                    "Upload failed for file: {}",
                    file.getOriginalFilename(),
                    exception
            );

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

    public void deleteById(Long id) throws IOException {

        MediaAsset asset = repository.findById(id)
                .orElseThrow(
                        () -> new MediaAssetNotFoundException(id)
                );

        Path storedFile = Path.of(asset.getStoragePath());

        fileStorageService.delete(storedFile);
        repository.delete(asset);

        logger.info(
                "Deleted media asset and stored file for ID: {}",
                id
        );
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

    public Page<MediaAsset> findAllPaged(
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(pageable);
    }

    public MediaAsset updateMetadata(
            Long id,
            UpdateMediaAssetRequest request
    ) {
        MediaAsset asset = repository.findById(id)
                .orElseThrow(
                        () -> new MediaAssetNotFoundException(id)
                );

        if (request.getTitle() != null) {
            if (!StringUtils.hasText(request.getTitle())) {
                throw new IllegalArgumentException(
                        "Title cannot be empty."
                );
            }

            asset.setTitle(request.getTitle().trim());
        }

        if (request.getTeam() != null) {
            asset.setTeam(request.getTeam().trim());
        }

        if (request.getPlayer() != null) {
            asset.setPlayer(request.getPlayer().trim());
        }

        if (request.getStadium() != null) {
            asset.setStadium(request.getStadium().trim());
        }

        if (request.getGameDate() != null) {
            asset.setGameDate(request.getGameDate());
        }

        if (request.getCodec() != null) {
            asset.setCodec(request.getCodec().trim());
        }

        if (request.getStatus() != null) {
            asset.setStatus(request.getStatus().trim().toUpperCase());
        }

        MediaAsset updatedAsset = repository.save(asset);

        logger.info(
                "Media asset metadata updated for ID: {}",
                updatedAsset.getId()
        );

        return updatedAsset;
    }

    public Path getStoredFile(Long id) {

        MediaAsset asset = repository.findById(id)
                .orElseThrow(
                        () -> new MediaAssetNotFoundException(id)
                );

        Path filePath = Path.of(asset.getStoragePath());

        if (!java.nio.file.Files.exists(filePath)) {
            throw new IllegalStateException(
                    "The stored media file is missing."
            );
        }

        return filePath;
    }
}