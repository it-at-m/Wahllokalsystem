package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common;

public record FileResponseEntityModel(byte[] responseBody, String headerContentType, String attachmentFilename) {
}
