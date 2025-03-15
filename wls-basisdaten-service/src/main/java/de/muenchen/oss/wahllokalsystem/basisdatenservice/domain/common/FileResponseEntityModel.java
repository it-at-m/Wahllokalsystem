package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common;

public record FileResponseEntityModel(byte[] responseBody, String headerContentType, String attachmentFilename) {
}
