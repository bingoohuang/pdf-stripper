package org.fit.pdfdom.resource;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SaveResourceToDirHandler implements HtmlResourceHandler {
    public static final String DEFAULT_RESOURCE_DIR = "resources/";

    private final File directory;
    private List<String> writtenFileNames = new LinkedList<>();

    public SaveResourceToDirHandler() {
        directory = null;
    }

    public SaveResourceToDirHandler(File directory) {
        this.directory = directory;
    }

    public String handleResource(HtmlResource resource) throws IOException {
        String dir = DEFAULT_RESOURCE_DIR;
        if (directory != null)
            dir = directory.getPath() + "/";

        String fileName = findNextUnusedFileName(resource.getName());
        String resourcePath = dir + fileName + "." + resource.getFileEnding();

        File file = new File(resourcePath);
        FileUtils.writeByteArrayToFile(file, resource.getData());

        writtenFileNames.add(fileName);

        return resourcePath;
    }

    private String findNextUnusedFileName(String fileName) {
        int i = 1;
        String usedName = fileName;
        while (writtenFileNames.contains(usedName)) {
            usedName = fileName + i;
            i++;
        }

        return usedName;
    }
}
