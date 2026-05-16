package com.lld.practice.filesystem;

import java.util.List;

// This is the orchestrator or EntryPoint
// We are following a Facade Pattern
//  - [ like maintaing the complexities by ourselves while exposing only the necessary public apis
// to client ]
public class FileSystem {
  private Folder root;

  public FileSystem() {
    this.root = new Folder("/");
  }

  public File createFile(String path, String content) {

    if (path.equals("/")) {
      throw new IllegalArgumentException("can't create file at root");
    }

    Folder parent = resolveParent(path);
    String fileName = extractName(path);

    if (parent.hasChild(fileName)) {
      throw new AlreadyExistsException(fileName);
    }

    File file = new File(fileName, content);
    parent.addChild(file);
    return file;
  }

  public Folder createFolder(String path) {

    if (path.equals("/")) {
      throw new IllegalArgumentException("we can not create root folder. It is already existent");
    }

    Folder parent = resolveParent(path);
    String name = extractName(path);

    if (parent.hasChild(name)) {
      throw new AlreadyExistsException(name);
    }

    Folder folder = new Folder(name);
    parent.addChild(folder);
    return folder;
  }

  public void delete(String path) {
    if (path.equals("/")) {
      throw new IllegalArgumentException("Deletion of root is not allowed");
    }

    Folder parent = resolveParent(path);
    String child = extractName(path);
    if (!parent.hasChild(child)) {
      throw new NotExistsException(child);
    }

    parent.removeChild(child);
  }

  public List<FileSystemEntry> list(String path) {

    FileSystemEntry parent = resolvePath(path);
    if (!parent.isDirectory()) {
      throw new RuntimeException("%s is not a folder.".formatted(path));
    }
    return ((Folder) parent).getChildren();
  }

  public FileSystemEntry get(String path) {
    return resolvePath(path);
  }

  public void rename(String srcPath, String newName) {

    if (srcPath.equals("/")) {
      throw new IllegalArgumentException("We can't rename root");
    }

    Folder parent = resolveParent(srcPath);
    String oldName = extractName(srcPath);

    if (parent.hasChild(newName)) {
      throw new AlreadyExistsException("we can't rename as the entry with the name already exists");
    }

    FileSystemEntry childEntry = parent.removeChild(oldName);
    childEntry.setName(newName);
    parent.addChild(childEntry);
  }

  public void move(String srcPath, String destPath) {

    Folder srcParent = resolveParent(srcPath);
    String srcName = extractName(srcPath);
    FileSystemEntry childEntry = srcParent.getChild(srcName);
    if (childEntry == null) {
      throw new NotExistsException(srcName);
    }

    Folder destParent = resolveParent(destPath);
    String destName = extractName(destPath);

    if (destParent.hasChild(destName)) {
      throw new AlreadyExistsException(destName);
    }

    // circular reference name
    if (childEntry.isDirectory()) {
      FileSystemEntry current = destParent;
      while (current != null) {
        if (current == childEntry) {
          throw new IllegalArgumentException("Can not move this folder");
        }
        current = current.getParent();
      }
    }

    // remove the entry from source
    srcParent.removeChild(srcName);
    childEntry.setName(destName);
    // add the entry into the destination
    destParent.addChild(childEntry);
  }

  private FileSystemEntry resolvePath(String path) {
    if (path == null || path.isEmpty()) {
      throw new IllegalArgumentException("Invalid Path provided");
    }

    if (!path.startsWith("/")) {
      throw new IllegalArgumentException("Path should always start with /");
    }

    if (path.equals("/")) {
      return root;
    }

    String[] parts = path.substring(1).split("/");
    FileSystemEntry current = root;

    for (String part : parts) {
      if (part.isEmpty()) {
        throw new IllegalArgumentException("consecutive double slash provided");
      }

      if (!current.isDirectory()) {
        throw new IllegalArgumentException("Invalid path provided");
      }

      FileSystemEntry child = ((Folder) current).getChild(part);
      if (child == null) {
        throw new IllegalArgumentException("Entry Not found");
      }
      current = child;
    }

    return current;
  }

  private Folder resolveParent(String path) {
    if ("/".equals(path)) {
      throw new IllegalArgumentException("Root can't have parents");
    }

    int lastSlashIndex = path.lastIndexOf("/");
    String parentPath = lastSlashIndex == 0 ? "/" : path.substring(0, lastSlashIndex);

    FileSystemEntry parent = resolvePath(parentPath);
    if (!parent.isDirectory()) {
      throw new IllegalArgumentException("Parent is not a directory");
    }
    return (Folder) parent;
  }

  private String extractName(String path) {
    int lastSlashIndex = path.lastIndexOf("/");
    return path.substring(lastSlashIndex + 1);
  }
}
