package com.lld.practice.filesystem;

import java.util.Collections;
import java.util.List;

// This is the orchestrator or EntryPoint
// We are following a Facade Pattern
//  - [ like maintaing the complexities by ourselves while exposing only the necessary public apis
// to client ]
public class FileSystem {
  private Folder root;

  public FileSystem() {}

  public File createFile(String path, String content) {
    return null;
  }

  public Folder createFolder(String path) {
    return null;
  }

  public void delete(String path) {}

  public List<FileSystemEntry> list(String path) {
    return Collections.emptyList();
  }

  public FileSystemEntry get(String path) {
    return null;
  }

  public void rename(String srcPath, String newName) {}

  public void move(String srcPath, String destPath) {}
}
