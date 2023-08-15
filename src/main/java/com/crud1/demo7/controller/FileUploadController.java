package com.crud1.demo7.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.crud1.demo7.models.ResponeObject;
import com.crud1.demo7.service.impl.ImageUploadService;

@Controller
@RequestMapping(path = "/api/v1/FileUpload")
public class FileUploadController {
	@Autowired
	private ImageUploadService imageUploadService;

	@PostMapping("")
	public ResponseEntity<ResponeObject> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			String generatedFilename = imageUploadService.storeFile(file);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ResponeObject("ok", "upload file successfully", generatedFilename));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
					.body(new ResponeObject("not ok", e.getMessage(), ""));

		}
	}

	@GetMapping("/file/{filename:.+}")
	public ResponseEntity<byte[]> readDetailFile(@PathVariable String filename) {
		try {
			byte[] bytes = imageUploadService.readfileContent(filename);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("")
	public ResponseEntity<ResponeObject> getuploadfiles() {
		try {
			List<String> urls = imageUploadService.loadAll().map(path -> {
				String urlPath = MvcUriComponentsBuilder
						.fromMethodName(FileUploadController.class,
								"readDetailFile",
								path.getFileName().toString())
								.build().toUri().toString();
				return urlPath;
			}).collect(Collectors.toList());
			return ResponseEntity.ok(new ResponeObject("ok", "List file successfull", urls));

		} catch (Exception e) {
			return ResponseEntity.ok(new ResponeObject("ok", " List files failed", new String[] {}));
		}
	}
}
