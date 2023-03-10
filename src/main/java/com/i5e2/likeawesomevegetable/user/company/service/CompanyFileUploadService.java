package com.i5e2.likeawesomevegetable.user.company.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.i5e2.likeawesomevegetable.common.exception.AppErrorCode;
import com.i5e2.likeawesomevegetable.common.exception.AwesomeVegeAppException;
import com.i5e2.likeawesomevegetable.company.buying.CompanyFile;
import com.i5e2.likeawesomevegetable.company.buying.CompanyImage;
import com.i5e2.likeawesomevegetable.company.buying.repository.CompanyFileJpaRepository;
import com.i5e2.likeawesomevegetable.company.buying.repository.CompanyImageJpaRepository;
import com.i5e2.likeawesomevegetable.user.basic.User;
import com.i5e2.likeawesomevegetable.user.basic.repository.UserJpaRepository;
import com.i5e2.likeawesomevegetable.user.company.CompanyUser;
import com.i5e2.likeawesomevegetable.user.company.dto.CompanyFileResponse;
import com.i5e2.likeawesomevegetable.user.company.repository.CompanyUserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyFileUploadService {
    private final AmazonS3Client amazonS3Client;
    private final UserJpaRepository userJpaRepository;
    private final CompanyUserJpaRepository companyUserJpaRepository;
    private final CompanyImageJpaRepository companyImageJpaRepository;
    private final CompanyFileJpaRepository companyFileJpaRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /*     ?????? ?????? ?????????     */
    public CompanyFileResponse UploadCompanyFile(Long companyId, MultipartFile multipartFile, String loginEmail) throws IOException {
        // ????????? ??????????????? ??????
        validateFilExists(multipartFile);

        User loginUser = validateLoginUser(loginEmail);
        CompanyUser companyUser = validateCompanyUser(companyId);

        // ????????? ???????????? ????????? ?????? ????????? ??????????????? ?????? ???????????????
        if (loginUser.getCompanyUser().getId() != companyUser.getId()) {
            throw new AwesomeVegeAppException(AppErrorCode.INVALID_PERMISSION, AppErrorCode.INVALID_PERMISSION.getMessage());
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        String originalFilename = multipartFile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(index + 1);

        // ????????? ?????? ??????
        String storeFileName = UUID.randomUUID() + "." + ext;

        // ????????? ???????????? ?????? + ?????? ??????
        String key = "companyuser/file/" + storeFileName;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new FileUploadException();
        }

        String storeFileUrl = amazonS3Client.getUrl(bucket, key).toString();
        CompanyFile companyFile = CompanyFile.makeCompanyFile(originalFilename, storeFileUrl, companyUser);
        CompanyFile savedFile = companyFileJpaRepository.save(companyFile);

        return CompanyFileResponse.of(savedFile.getCompanyFileName(), "?????? ?????? ??????");
    }

    /*     ?????? ????????? ?????????     */
    public CompanyFileResponse UploadCompanyImage(Long companyId, MultipartFile multipartFile, String loginEmail) throws IOException {
        // ????????? ??????????????? ??????
        validateFilExists(multipartFile);

        User loginUser = validateLoginUser(loginEmail);
        CompanyUser companyUser = validateCompanyUser(companyId);

        // ????????? ????????? ??????????????? url??? ?????? ???????????? ?????????
        if (loginUser.getCompanyUser().getId() != companyUser.getId()) {
            throw new AwesomeVegeAppException(AppErrorCode.INVALID_PERMISSION, AppErrorCode.INVALID_PERMISSION.getMessage());
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        String originalFilename = multipartFile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(index + 1);

        // ????????? ?????? ??????
        String storeFileName = UUID.randomUUID() + "." + ext;

        // ????????? ???????????? ?????? + ?????? ??????
        String key = "companyuser/image/" + storeFileName;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new FileUploadException();
        }

        String storeFileUrl = amazonS3Client.getUrl(bucket, key).toString();
        CompanyImage companyImage = CompanyImage.makeCompanyImage(originalFilename, storeFileUrl, companyUser);
        CompanyImage savedImage = companyImageJpaRepository.save(companyImage);

        return CompanyFileResponse.of(savedImage.getCompanyImageName(), "????????? ?????? ??????");
    }

    /*     ?????? ?????? ??????     */
    public CompanyFileResponse deleteCompanyFile(Long companyId, Long companyFileId, String filePath, String loginEmail) {

        User loginUser = validateLoginUser(loginEmail);
        CompanyUser companyUser = validateCompanyUser(companyId);

        // ????????? ????????? ??????????????? url??? ?????? ???????????? ?????????
        if (loginUser.getCompanyUser().getId() != companyUser.getId()) {
            throw new AwesomeVegeAppException(AppErrorCode.INVALID_PERMISSION, AppErrorCode.INVALID_PERMISSION.getMessage());
        }

        // ?????? ?????? ?????? ??????
        CompanyFile companyFile = validateCompanyFile(companyFileId);

        String deleteFileName = companyFile.getCompanyFileName();

        try {
            // S3 ????????? ?????? ??????
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
            // ?????? ????????? ?????? ?????????????????? ?????? ??????
            companyFileJpaRepository.delete(companyFile);
            log.info("?????? ?????? ??????");
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return CompanyFileResponse.of(deleteFileName, "?????? ?????? ??????");
    }

    /*    ?????? ?????? ??????     */
    public CompanyFileResponse deleteCompanyImage(Long companyId, Long companyImageId, String filePath, String loginEmail) {

        User loginUser = validateLoginUser(loginEmail);
        CompanyUser companyUser = validateCompanyUser(companyId);

        // ????????? ????????? ??????????????? url??? ?????? ???????????? ?????????
        if (loginUser.getCompanyUser().getId() != companyUser.getId()) {
            throw new AwesomeVegeAppException(AppErrorCode.INVALID_PERMISSION, AppErrorCode.INVALID_PERMISSION.getMessage());
        }

        // ?????? ?????? ?????? ??????
        CompanyImage companyImage = validateCompanyImage(companyImageId);

        String deleteImageName = companyImage.getCompanyImageName();

        try {
            // S3 ????????? ?????? ??????
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
            // ?????? ????????? ?????? ?????????????????? ?????? ??????
            companyImageJpaRepository.delete(companyImage);
            log.info("?????? ?????? ??????");
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return CompanyFileResponse.of(deleteImageName, "????????? ?????? ??????");
    }

    // ?????? ????????? ?????? ?????? ??????
    private CompanyUser validateCompanyUser(Long companyId) {
        CompanyUser validatedCompanyUser = companyUserJpaRepository.findById(companyId)
                .orElseThrow(() -> new AwesomeVegeAppException(
                        AppErrorCode.COMPANY_USER_NOT_FOUND,
                        AppErrorCode.COMPANY_USER_NOT_FOUND.getMessage())
                );
        return validatedCompanyUser;
    }

    // ????????? ????????? ??????
    private User validateLoginUser(String loginEmail) {
        User loginUser = userJpaRepository.findByEmail(loginEmail)
                .orElseThrow(() -> new AwesomeVegeAppException(
                        AppErrorCode.LOGIN_USER_NOT_FOUND,
                        AppErrorCode.LOGIN_USER_NOT_FOUND.getMessage()
                ));
        return loginUser;
    }

    // ??? ????????? ????????? ??????, ?????? ????????? ?????????????????? ???????????? ????????? ??????????????? ?????? ??????
    private void validateFilExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new AwesomeVegeAppException(
                    AppErrorCode.FILE_NOT_EXISTS,
                    AppErrorCode.FILE_NOT_EXISTS.getMessage()
            );
        }
    }

    // ?????? ?????? ?????? ??????
    private CompanyFile validateCompanyFile(Long companyFileId) {
        CompanyFile validatedCompanyFile = companyFileJpaRepository.findById(companyFileId)
                .orElseThrow(() -> new AwesomeVegeAppException(
                        AppErrorCode.COMPANY_FILE_NOT_FOUND,
                        AppErrorCode.COMPANY_FILE_NOT_FOUND.getMessage())
                );
        return validatedCompanyFile;
    }

    // ?????? ????????? ?????? ??????
    private CompanyImage validateCompanyImage(Long companyImageId) {
        CompanyImage validatedCompanyImage = companyImageJpaRepository.findById(companyImageId)
                .orElseThrow(() -> new AwesomeVegeAppException(
                        AppErrorCode.COMPANY_IMAGE_NOT_FOUND,
                        AppErrorCode.COMPANY_IMAGE_NOT_FOUND.getMessage())
                );
        return validatedCompanyImage;
    }

}
