package com.angelodibello.mediaarchive.repository;

import com.angelodibello.mediaarchive.model.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaAssetRepository
        extends JpaRepository<MediaAsset, Long> {

    Optional<MediaAsset> findByChecksum(String checksum);

    List<MediaAsset> findByTeamContainingIgnoreCase(String team);

    List<MediaAsset> findByPlayerContainingIgnoreCase(String player);

    List<MediaAsset> findByTitleContainingIgnoreCase(String title);

    List<MediaAsset> findByStadiumContainingIgnoreCase(String stadium);
}