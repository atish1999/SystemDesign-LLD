package com.lld.practice.filesystem;

public abstract class FileSystemEntry {

  private String name;
  private Folder parent;

  public FileSystemEntry(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Folder getParent() {
    return parent;
  }

  public void setParent(Folder parent) {
    this.parent = parent;
  }

  public String getPath() {
    if (parent == null) {
      return this.getName();
    }

    String path = parent.getPath();
    if ("/".equals(path)) {
      return "/" + this.getName();
    }
    return path + "/" + this.getName();
  }

  public abstract boolean isDirectory();
}
