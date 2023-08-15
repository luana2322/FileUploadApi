package com.crud1.demo7.service.impl;

import java.io.IOException;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.crud1.demo7.service.IstorageService;


@Service
public class ImageUploadService implements IstorageService {
	private final Path storageFoler = Paths.get("uploads");

	public ImageUploadService() {
		try {
			Files.createDirectories(storageFoler);
		} catch (IOException e) {
			throw new RuntimeException("Cannot initialize storage ", e);
		}
	}

	private boolean isImageFile(MultipartFile file) {
		String fileextention = FilenameUtils.getExtension(file.getOriginalFilename());
		return Arrays.asList(new String[] { "png", "jpg", "jpeg", "bmp" }).contains(fileextention.trim().toLowerCase());
	}

	@Override
	public String storeFile(MultipartFile multipartFile) {
		try {
			System.out.println("lưu file");

			if (multipartFile.isEmpty()) {
				throw new RuntimeException("Fail to storage empty file!!");
			}

			if (!isImageFile(multipartFile)) {
				throw new RuntimeException("You can only upload image file");
			}

			float fileSizeInMegabytes = multipartFile.getSize() / 1_000_000.0f;

			if (fileSizeInMegabytes > 5.0f) {
				throw new RuntimeException("File must be <=5Mb");
			}

			String fileEXtention = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
			String generatedFilename = UUID.randomUUID().toString().replace("-", "");

			generatedFilename = generatedFilename + "." + fileEXtention;
			Path destinaltionPath = this.storageFoler.resolve(Paths.get(generatedFilename)).normalize()
					.toAbsolutePath();

			if (!destinaltionPath.getParent().equals(this.storageFoler.toAbsolutePath())) {

				throw new RuntimeException("cannot storage file outside current directory.");
			}

			try (InputStream inputStream = multipartFile.getInputStream()) {
				Files.copy(inputStream, destinaltionPath, StandardCopyOption.REPLACE_EXISTING);

			}

			return generatedFilename;

		} catch (Exception e) {
			throw new RuntimeException("Failed to storage file", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
			try {
				return Files.walk(this.storageFoler, 1)
						.filter(path->!path.equals(this.storageFoler)&&!path.toString().contains("._"))
						.map(this.storageFoler::relativize);
			} catch (IOException e) {
				throw new RuntimeException("Failed to stored files",e);
			}
		
	}

	@Override
	public byte[] readfileContent(String fileName) {
		try {
			Path file=storageFoler.resolve(fileName);
			Resource resource=new UrlResource(file.toUri());
			if(resource.exists()&&resource.isReadable()) {
				 	byte[] bytes=StreamUtils.copyToByteArray(resource.getInputStream());
				 	return bytes;
				 	
			}else {
				throw new RuntimeException("Could not read file: "+fileName);
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Could not read file: "+fileName);
			
		}
	}

	@Override
	public void deleteAllfile() {
		// TODO Auto-generated method stub

	}

}
