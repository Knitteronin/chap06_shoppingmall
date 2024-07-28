package com.javalab.boot.controller;

import com.javalab.boot.dto.upload.UploadFileDTO;
import com.javalab.boot.dto.upload.UploadResultDTO;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 업로드 레스트 컨트롤러
 */
@RestController
@Log4j2
public class UpDownController {

    //  환경설정파일에 com.javalab.boot.upload.path 이름으로 설정해놓은 값 조회
    @Value("${com.javalab.boot.upload.path}")
    private String uploadPath;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO){

        log.info("업로드 파일명 : " + uploadFileDTO);

        // 1 단계, 콘솔에서 업로드 이미지파일 확인
        /*
        if(uploadFileDTO.getFiles() != null) {
            uploadFileDTO.getFiles().forEach(multipartFile -> {
                log.info(multipartFile.getOriginalFilename());
            });
        }
        return null;
        */

        // 2 단계, 업로드 파일명 중복 회피, 섬네일 이미지 (UUID + Thumbnail)
        /*
        if(uploadFileDTO.getFiles() != null) {
            uploadFileDTO.getFiles().forEach(multipartFile -> {
                String originalName = multipartFile.getOriginalFilename();
                log.info("오리지널 파일명 : " + originalName);
                String uuid = UUID.randomUUID().toString();
                // Path : 파일 또는 디렉토리 경로를 나타내는 Path 객체 얻기
                // 업로드할 파일을 path 경로를 통해서 생성하게 됨.
                Path savePath = Paths.get(uploadPath, uuid+"_"+ originalName);

                try {
                    // 파일 복사
                    multipartFile.transferTo(savePath);

                     // 이미지 파일의 종류라면
                     // Files 클래스의 probeContentType 메서드를 사용하여
                     // 파일의 MIME(이미지,동영상) 유형 확인.
                    if(Files.probeContentType(savePath).startsWith("image")){
                        // 썸네일을 저장할 파일의 경로를 설정
                        File thumbFile = new File(uploadPath, "s_" + uuid+"_" + originalName);
                        // 섬네일 라이브러리 사용하여 원본 이미지 파일인 savePath에서
                        // 썸네일을 생성.
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }); // end forEach
        }
        return null;
        */

        // 3단계 완성
        if(uploadFileDTO.getFiles() != null){

            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile -> {

                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                String uuid = UUID.randomUUID().toString();

                Path savePath = Paths.get(uploadPath, uuid+"_"+ originalName);

                boolean image = false;

                try {
                    multipartFile.transferTo(savePath);

                    //이미지 파일의 종류라면
                    if(Files.probeContentType(savePath).startsWith("image")){

                        image = true;

                        File thumbFile = new File(uploadPath, "s_" + uuid+"_"+ originalName);

                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                list.add(UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(image).build()
                );

            });//end each

            return list;
        }//end if

        return null;
    }


    /**
     * ResponseEntity :
     *  - HTTP응답을 표현하는 클래스로, 상태 코드, 헤더, 본문 등을 포함.
     * ResponseEntity.ok() :
     *  - 메서드는 HTTP 상태 코드 200 (OK)를 가진 ResponseEntity 객체를
     *    생성하는 메서드.
     *  ResponseEntity<String> responseEntity = ResponseEntity
     *         .status(HttpStatus.OK) // 상태 코드 설정
     *         .header("Custom-Header", "foo") // 헤더 설정
     *         .body("Hello World"); // 본문 설정
     */
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName){

        /**
         * new FileSystemResource(파라미터) : 파라미터로 주어진 경로의
         * 파일을 핸들링 할 수 있는 Resource 객체 생성.
         * Resource 객체는 파일 시스템에 있는 파일을 나타냄.
         * 요청Url => http://localhost:8080/view/aaa.jpg
         */
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try{
            headers.add("Content-Type", Files.probeContentType( resource.getFile().toPath() ));
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
        /**
         * ResponseEntity.ok(): 상태 코드 200 (OK)로 응답 객체를 생성.
         * body(resource): 리소스 (파일)를 응답 본문에 추가.
         * ResponseEntity 객체에 추가적인 정보를 체이닝 방식으로 설정
         */
         //return ResponseEntity.ok().headers(headers).body(resource); // 동일한 결과

        return ResponseEntity.status(HttpStatus.OK) // 상태 코드 설정
                .headers(headers) // 헤더 설정
                .body(resource); // 본문 설정
    }

    @DeleteMapping("/remove/{fileName}")
    public Map<String,Boolean> removeFile(@PathVariable String fileName){

        /**
         * new FileSystemResource(파라미터) : 파라미터로 주어진 경로의
         * 파일을 핸들링 할 수 있는 Resource 객체 생성.
         * Resource 객체는 파일 시스템에 있는 파일을 나타냄.
         * http://localhost:8080/view/aaa.jpg(이미지 url)
         */
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;

        try {
            //
            String contentType = Files.probeContentType(resource.getFile().toPath());
            // resource에 해당하는 파일을 삭제하고 삭제 여부를 반환
            removed = resource.getFile().delete();

            //섬네일이 존재한다면 삭제
            if(contentType.startsWith("image")){
                File thumbnailFile = new File(uploadPath+File.separator +"s_" + fileName);
                thumbnailFile.delete();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        resultMap.put("result", removed);
        return resultMap;
    }

}
