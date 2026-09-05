package com.brixcore.download.game;

import com.brixcore.game.Library;

/* JADX INFO: loaded from: classes9.dex */
public class LibraryDownloadException extends Exception {
    private final Library library;

    public LibraryDownloadException(Library library, Throwable cause) {
        super("Unable to download library " + library, cause);
        this.library = library;
    }

    public Library getLibrary() {
        return this.library;
    }
}
