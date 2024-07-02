package org.drishti.esign.util;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResourceToMultipartFileConverter {

    public static MultipartFile convertResourceToMultipartFile(Resource resource) throws IOException {
        byte[] content = IOUtils.toByteArray(resource.getInputStream());
        return new ByteArrayMultipartFile(resource.getFilename(), content);
    }


}
