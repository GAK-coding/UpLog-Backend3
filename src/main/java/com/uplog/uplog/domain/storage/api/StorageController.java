package com.uplog.uplog.domain.storage.api;

import com.uplog.uplog.domain.storage.application.StorageService;
import com.uplog.uplog.domain.storage.dto.StorageDTO;
import com.uplog.uplog.domain.storage.dto.TempResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StorageController {
    private final String account="63cec4bdfcfb4cfa90682fd84b850e5b";
    private final StorageService storageService;

    @PostMapping("/storages/upload")
    public StorageDTO uploadFile(@RequestPart("file") MultipartFile multipartFile) throws IOException, URISyntaxException {

        return storageService.uploadFileToKakaoCloudStorage(account,"small", multipartFile.getOriginalFilename(), multipartFile);
    }


   @DeleteMapping("/storages/delete")
    public String deleteFile(@RequestBody StorageDTO.requestDeleteDTO requestDeleteDTO) throws URISyntaxException {

        return storageService.deleteFileToKakaoCloudStorage(requestDeleteDTO);
    }

    //테스트 용
//    @GetMapping("/storages/read")
//    public String readFile() throws URISyntaxException {
//
//        return storageService.getFileToKakaoCloudStorage();
//    }
}
