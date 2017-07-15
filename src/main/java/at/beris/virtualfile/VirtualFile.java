/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.operation.FileOperationListener;
import at.beris.virtualfile.util.DisposableObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

/**
 * Public API for a virtual file.
 */
public interface VirtualFile extends DisposableObject {

    /**
     * Convert a VirtualFile to a VirtualArchive.
     *
     * @return VirtualArchive
     */
    VirtualArchive asArchive();

    /**
     * Convert a VirtualFile to a java.io.File.
     *
     * @return File
     */
    File asFile();


    /**
     * Add attributes to this file
     *
     * @param attributes
     */
    void addAttributes(FileAttribute... attributes);

    /**
     * Calculate and return the checksum.
     *
     * @return checksum
     */
    Byte[] checksum();

    /**
     * Copy this file to a target.
     *
     * @param targetFile
     * @return Number of copied files
     */
    Integer copy(VirtualFile targetFile);

    /**
     * Copy this file to a target with a listener.
     * The listener functions will be invoked during the process.
     *
     * @param targetFile
     * @param listener
     * @return Number of coped files
     */
    Integer copy(VirtualFile targetFile, FileOperationListener listener);

    /**
     * Compare this file to a targetfile.
     *
     * @param targetFile
     * @return true if the file contents are equal
     */
    Boolean compare(VirtualFile targetFile);

    /**
     * Compare this file to a targetfile with a listener.
     * The listener functions will be invoked during the process.
     *
     * @param targetFile
     * @param listener
     * @return true if the file contents are equal
     */
    Boolean compare(VirtualFile targetFile, FileOperationListener listener);

    /**
     * Compress this file.
     */
    void compress();

    /**
     * Decompress this file.
     */
    void decompress();

    /**
     * Creates an empty file
     */
    void create();

    /**
     * Delete this file.
     */
    void delete();

    /**
     * Check if this file exists.
     *
     * @return True if file exists
     */
    Boolean exists();

    /**
     * Finds files recursively matching a filter
     *
     * @param filter A filter
     * @return A list of files
     */
    List<VirtualFile> find(Filter filter);

    /**
     * Get ACL List. (Only returns something for the Windows operating system)
     *
     * @return
     */
    List<AclEntry> getAcl();

    /**
     * Get attributes for this file
     *
     * @return attributes
     */
    Set<FileAttribute> getAttributes();

    /**
     * Get file content type.
     *
     * @return ContentType
     */
    ContentType getContentType();

    /**
     * Guesses the encoding from the content.
     *
     * @return Content encoding
     */
    String getContentEncoding();

    /**
     * Return the file creation time.
     *
     * @return creation time
     */
    FileTime getCreationTime();

    /**
     * Get the unix group. (Only returns something on unixlike operationsystems)
     *
     * @return unix group
     */
    GroupPrincipal getGroup();

    /**
     * Get InputStream for this file.
     *
     * @return InputStream
     */
    InputStream getInputStream();

    /**
     * Return the time when this file was last accessed.
     *
     * @return Time last accessed
     */
    FileTime getLastAccessTime();

    /**
     * Return the time when this file was last modified.
     *
     * @return Time last modified
     */
    FileTime getLastModifiedTime();

    /**
     * Return the URL of the target if this file is a link.
     *
     * @return Link target
     */
    URL getLinkTarget();

    /**
     * Get file name.
     *
     * @return File name
     */
    String getName();

    /**
     * Get OutputStream for this file.
     *
     * @return OutputStream
     */
    OutputStream getOutputStream();

    /**
     * Get the unix owner. (Only returns something on unixlike operationsystems)
     *
     * @return unix owner
     */
    UserPrincipal getOwner();

    /**
     * Get the parent file for this file.
     *
     * @return Parent file
     */
    VirtualFile getParent();

    /**
     * Get the path for this file.
     *
     * @return Path
     */
    String getPath();

    /**
     * Get the root file for this file (This is the first file in the hierarchy)
     *
     * @return
     */
    VirtualFile getRoot();

    /**
     * Returns the size in bytes for a file or the number of contained items for a directory.
     *
     * @return File size
     */
    long getSize();

    /**
     * Get the URL for this file
     *
     * @return
     */
    URL getUrl();

    /**
     * Checks if this file is an archive.
     *
     * @return True if file is an archive; false otherwise.
     */
    boolean isArchive();

    /**
     * Checks if this file is a directory.
     *
     * @return True if file is a directory; false otherwise.
     */
    boolean isDirectory();

    /**
     * Checks if this file is a symbolic link.
     *
     * @return True if file is a symbolic link; false otherwise.
     */
    boolean isSymbolicLink();

    /**
     * Tests whether the application can read this file.
     *
     * @return True if file is readable
     */
    boolean isReadable();

    /**
     * Tests whether the application can modify this file.
     *
     * @return True if file is writable
     */
    boolean isWritable();

    /**
     * Tests whether the application can execute this file.
     *
     * @return True if file is executable
     */
    boolean isExecutable();

    /**
     * Tests whether this file is hidden.
     *
     * @return True if file is hidden
     */
    boolean isHidden();

    /**
     * Lists contained files non-recursively.
     *
     * @return A list of files.
     */
    List<VirtualFile> list();

    /**
     * Lists contained files non-recursively filtered by a filter.
     *
     * @param filter Filter
     * @return List of files.
     */
    List<VirtualFile> list(Filter filter);

    void move(VirtualFile target);

    /**
     * Refresh this file from the underlying source. All file information be be up to date afterwards.
     */
    void refresh();

    /**
     * Remove attributes from this file.
     *
     * @param attributes Attributes to remove
     */
    void removeAttributes(FileAttribute... attributes);

    /**
     * Rename this file.
     *
     * @param newName
     */
    void rename(String newName);

    /**
     * Set Acl for this file.  (Only supported by the windows operating system)
     *
     * @param acl
     */
    void setAcl(List<AclEntry> acl);

    /**
     * Set attributes for this file.
     *
     * @param attributes
     */
    void setAttributes(FileAttribute... attributes);

    /**
     * Set creation time for this file.
     *
     * @param time New creation time
     */
    void setCreationTime(FileTime time);

    /**
     * Set the unix group. (Only works on unixlike operationsystems)
     *
     * @param group New group
     */
    void setGroup(GroupPrincipal group);

    /**
     * Set last accessed time for this file.
     *
     * @param time New last accessed time
     */
    void setLastAccessTime(FileTime time);

    /**
     * Set last modified time for this file.
     *
     * @param time New last accessed time
     */
    void setLastModifiedTime(FileTime time);

    /**
     * Set the unix owner. (Only works on unixlike operationsystems)
     *
     * @param owner New owner
     */
    void setOwner(UserPrincipal owner);

    /**
     * Free all resources allocated by this file.
     */
    void dispose();
}
