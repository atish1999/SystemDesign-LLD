package com.lld.practice.filesystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Folder extends FileSystemEntry {

  private Map<String, FileSystemEntry> children;

  public Folder(String name) {
    super(name);
    this.children = new HashMap<>();
  }

  public boolean addChild(FileSystemEntry entry) {
    return false;
  }

  public FileSystemEntry removeChild(String name) {
    return null;
  }

  public boolean hasChild(String name) {
    return false;
  }

  public List<FileSystemEntry> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public boolean isDirectory() {
    return true;
  }
}
