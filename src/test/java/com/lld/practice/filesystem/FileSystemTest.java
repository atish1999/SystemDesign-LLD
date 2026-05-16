package com.lld.practice.filesystem;

import static junit.framework.Assert.assertEquals;

import org.testng.annotations.Test;

public class FileSystemTest {

  @Test
  public void givenFileSystemIsInitializedWithRoot_whenCreatingFolder_thenFolderPathIsReturned() {
    FileSystem fileSystem = new FileSystem();
    Folder folder = fileSystem.createFolder("/home");
    assertEquals("/home", folder.getPath());
  }

  @Test
  public void
      givenFileSystemIsInitializedWithRoot_whenCreatingNestedFolder_thenNestedFolderPathIsReturned() {
    FileSystem fileSystem = new FileSystem();
    fileSystem.createFolder("/home");
    Folder folder = fileSystem.createFolder("/home/user");
    assertEquals("/home/user", folder.getPath());
  }

  @Test
  public void
      givenFileSystemIsInitializedWithRoot_whenCreatingFileWithContents_thenFilePathAndContentsAreReturned() {
    FileSystem fileSystem = new FileSystem();
    fileSystem.createFolder("/home");
    fileSystem.createFolder("/home/user");
    File file = fileSystem.createFile("/home/user/hello.txt", "Hey There");
    assertEquals("/home/user/hello.txt", file.getPath());
    assertEquals("Hey There", file.getContent());
  }
}
