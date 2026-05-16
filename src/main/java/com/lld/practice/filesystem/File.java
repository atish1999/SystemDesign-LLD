package com.lld.practice.filesystem;

// File can't be immutable as we can change the content and rename the file.
public class File extends FileSystemEntry {

  private String content;

  public File(String name, String content) {
    super(name);
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean isDirectory() {
    return false;
  }
}
