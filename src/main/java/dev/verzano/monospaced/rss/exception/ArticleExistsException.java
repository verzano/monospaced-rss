package dev.verzano.monospaced.rss.exception;

public class ArticleExistsException extends Exception {
    public ArticleExistsException(String message) {
        super(message);
    }
}
