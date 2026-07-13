package com.angelodibello.mediaarchive.exception;

public class MediaAssetNotFoundException extends RuntimeException {

    public MediaAssetNotFoundException(Long id) {
        super("Media asset not found with ID: " + id);
    }
}