package org.drishti.esign.util;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class ByteArrayMultipartFile implements MultipartFile {

    private final String name;
    private final byte[] content;

    public ByteArrayMultipartFile(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }
    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getOriginalFilename() {
        return "";
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public Resource getResource() {
        return MultipartFile.super.getResource();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        MultipartFile.super.transferTo(dest);
    }


}
