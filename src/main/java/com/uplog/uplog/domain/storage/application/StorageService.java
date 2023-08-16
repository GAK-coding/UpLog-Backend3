package com.uplog.uplog.domain.storage.application;

import com.uplog.uplog.domain.storage.dto.CustomHttpMethod;
import com.uplog.uplog.domain.storage.dto.StorageDTO;
import com.uplog.uplog.domain.storage.dto.TempResponse;
import com.uplog.uplog.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final RestTemplate restTemplate;

    // 카카오 아이클라우드 스토리지 업로드 API 엔드포인트
    private static final String KAKAO_UPLOAD_URL = "https://objectstorage.kr-gov-central-1.kakaoicloud-kr-gov.com/v1/{account}/{bucket_name}/{object_key}";
    private final String authToken="gAAAAABk24mKTrSgXzRisek9RVAYrA99qF9Hq4OlrX-yA3NTtgeEgQgmeRzeQ9EOXZplMElv7KTR_mEORaPA59_wqZB5xmEI5xq8FY69Bpklmg2hTEOT0s_fNU8d2Iw6O88UWDvhanfDdefb8smIopSgPiMj0oMaQ4Sma01fclGkO4Gu47BWGuUmvByYAXHg3eDKsOysEaFs";

    // 카카오 클라우드 API에 업로드 요청 보내기
    public StorageDTO uploadFileToKakaoCloudStorage(String account, String bucketName, String objectKey, MultipartFile multipartFile) throws IOException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", authToken);


        StorageDTO storageDTO=StorageDTO.multipartOf(multipartFile);//사실상 없어도 되긴 하는데 aws 설정 보면서 따라해봄

        String f_objectKey=storageDTO.getId()+"/"+objectKey;

        //최종 url
        String url = KAKAO_UPLOAD_URL
                .replace("{account}", account)
                .replace("{bucket_name}", bucketName)
                .replace("{object_key}", f_objectKey);

        // 이미지 파일로 업로드하기 위해 Content-Type을 image/png로 설정
        headers.setContentType(MediaType.IMAGE_PNG);

        RequestEntity<byte[]> requestEntity = new RequestEntity<>(multipartFile.getBytes(), headers, HttpMethod.PUT, new URI(url));
        storageDTO.addUrl(url);

        // 업로드 요청 보내기
        restTemplate.exchange(requestEntity, String.class);


        return storageDTO;

    }

    //db에 저장 된 url
    public String deleteFileToKakaoCloudStorage(String url) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", authToken);

        RequestEntity<byte[]> requestEntity = new RequestEntity<>(headers, HttpMethod.DELETE, new URI(url));
        restTemplate.exchange(requestEntity, String.class);
        return "Delete Ok";
    }


    //테스트 용
    public String getFileToKakaoCloudStorage() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        //headers.set("X-Auth-Token", authToken);

        RequestEntity<byte[]> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI("https://objectstorage.kr-gov-central-1.kakaoicloud-kr-gov.com/v1/63cec4bdfcfb4cfa90682fd84b850e5b/small/qrcode_www.google.com.png"));

        restTemplate.exchange(requestEntity, String.class);



        return "read";
    }
}