package com.lld.practice.filesystem;

import java.util.*;

public class Folder extends FileSystemEntry {

  private final Map<String, FileSystemEntry> children;

  public Folder(String name) {
    super(name);
    this.children = new HashMap<>();
  }

  public boolean addChild(FileSystemEntry entry) {
    String name = entry.getName();
    if (children.containsKey(name)) {
      return false;
    }
    children.put(name, entry);
    entry.setParent(this);
    return true;
  }

  public FileSystemEntry removeChild(String name) {
    FileSystemEntry entry = children.remove(name);
    if (entry != null) {
      entry.setParent(null);
    }
    return entry;
  }

  public boolean hasChild(String name) {
    return children.containsKey(name);
  }

  public List<FileSystemEntry> getChildren() {
    return new ArrayList<>(children.values());
  }

  @Override
  public boolean isDirectory() {
    return true;
  }

  public FileSystemEntry getChild(String srcName) {
    return children.get(srcName);
  }
}
