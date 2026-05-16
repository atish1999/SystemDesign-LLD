###### REQUIREMENTS
<hr/>

- A unix like file system with a single root ("/")
- there will be no relative path only absolute path.
- Operations permitted on file/folder
  - create
    - if the parent doesn't exist we can't create
  - read
    - for folder it will be listing all the contents
    - for file it will be reading the content 
    - we can get specific content for a given path
  - rename
    - we can't rename if the new name already exists in the path
  - move
  - delete
    - we can't delete root folder
- File can have only string content

<hr/>

###### OUT-OF-SCOPE
<hr/>

- file permission
- symbolic links
- timestamp
- user details