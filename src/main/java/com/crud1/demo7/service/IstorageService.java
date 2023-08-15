package com.crud1.demo7.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

public interface IstorageService {
	public String storeFile(MultipartFile multipartFile);

	public Stream<Path> loadAll();

	public byte[] readfileContent(String fileName);

	public void deleteAllfile();
}
