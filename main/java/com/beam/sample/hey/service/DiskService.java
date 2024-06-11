package com.beam.sample.hey.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiskService {

    private final String base = "C:\\Users\\turabi.aslan\\Desktop\\ToyProjects\\Disks\\HeyDisk\\";

    private final String  avatar = "avatar\\";

    public String saveAvatar(byte[] data) throws IOException {
        String filename = UUID.randomUUID().toString();
        Path path = Paths.get(base + avatar + filename);
        Files.write(path, data);
        return filename;
    }

    public byte[] readAvatar(String filename) throws IOException{
        return Files.readAllBytes(Paths.get(base + avatar + filename));
    }

}
