package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.common;

public record FileResponseEntityModel(
    byte[] responseBody, String headerContentType, String attachmentFilename) {}
