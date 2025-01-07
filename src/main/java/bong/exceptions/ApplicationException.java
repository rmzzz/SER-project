package bong.exceptions;

public class ApplicationException extends RuntimeException {
    private final String headerText;
    private final String contentText;

    public ApplicationException(String headerText, String contentText, Throwable cause) {
        super(headerText + " - " + contentText, cause);
        this.headerText = headerText;
        this.contentText = contentText;
    }

    public String getHeaderText() {
        return headerText;
    }

    public String getContentText() {
        return contentText;
    }
}
