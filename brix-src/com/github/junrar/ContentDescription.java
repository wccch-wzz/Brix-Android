package com.github.junrar;

/* JADX INFO: loaded from: classes.dex */
public class ContentDescription {
    public String path;
    public long size;

    public ContentDescription(String path, long size) {
        this.path = path;
        this.size = size;
    }

    public int hashCode() {
        int result = (1 * 31) + (this.path == null ? 0 : this.path.hashCode());
        return (result * 31) + ((int) (this.size ^ (this.size >>> 32)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ContentDescription other = (ContentDescription) obj;
        if (this.path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!this.path.equals(other.path)) {
            return false;
        }
        if (this.size == other.size) {
            return true;
        }
        return false;
    }

    public String toString() {
        return this.path + ": " + this.size;
    }
}
