package com.bezkoder.spring.files.upload.db.controller;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bezkoder.spring.files.upload.db.message.ResponseFile;
import com.bezkoder.spring.files.upload.db.message.ResponseMessage;
import com.bezkoder.spring.files.upload.db.model.FileDB;
import com.bezkoder.spring.files.upload.db.service.FileStorageService;

@Controller
@CrossOrigin("http://localhost:8081")
public class FileController {

  @Autowired
  private FileStorageService storageService;

  @PostMapping("/upload")
  public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
    String message = "";
    try {
      storageService.store(file);

      message = "Uploaded the file successfully: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    } catch (Exception e) {
      message = "Could not upload the file: " + file.getOriginalFilename() + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
    }
  }

  @GetMapping("/files")
  public ResponseEntity<List<ResponseFile>> getListFiles() {
    List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
      String fileDownloadUri = ServletUriComponentsBuilder
          .fromCurrentContextPath()
          .path("/files/")
          .path(dbFile.getId())
          .toUriString();

      return new ResponseFile(
          dbFile.getName(),
          fileDownloadUri,
          dbFile.getType(),
          dbFile.getData().length);
    }).collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(files);
  }

  @GetMapping("/files/{id}")
  public ResponseEntity<Resource> getFile(@PathVariable String id) throws FileNotFoundException {
	  System.out.println(id);
    FileDB fileDB = storageService.getFile(id);
    
    System.out.println(fileDB.getType()+":"+fileDB.getId());
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(fileDB.getType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
            .body(new ByteArrayResource(fileDB.getData()));
    //if you have inline in headers it will show preview and download button in browser
    //but if you have attachement in headers it will downloads automatically
    //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
  }
  @PostMapping("/certificate")
  public ResponseEntity<Resource> getFiles(@RequestBody Student student) throws FileNotFoundException {
	 
    FileDB fileDB = storageService.getFile(student.getId());
    System.out.println(student.getId()+":"+fileDB.getType());
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(fileDB.getType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileDB.getName() + "\"")
            .body(new ByteArrayResource(fileDB.getData()));
    
  }
}
