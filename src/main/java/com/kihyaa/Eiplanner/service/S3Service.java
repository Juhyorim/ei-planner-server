package com.kihyaa.Eiplanner.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.s3.PresignedRequest;
import com.kihyaa.Eiplanner.dto.s3.PresignedResonse;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client amazonS3Client;
    private final S3Utils s3Utils;
    private final MemberRepository memberRepository;

    public PresignedResonse createPresignedUrl(Member member, PresignedRequest presignedRequest) {
        String fileName = generateFileName(presignedRequest.fileName());
        Date expiration = calculateExpiration();

        GeneratePresignedUrlRequest urlRequest = preparePresignedUrlRequest(fileName, expiration, presignedRequest.fileType());
        String imageUrl = s3Utils.createImageUrl(fileName);

        updateMemberProfile(member, imageUrl);

        return buildPresignedResponse(urlRequest);
    }

    private String generateFileName(String originalFileName) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + originalFileName;
    }

    private Date calculateExpiration() {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + 1000 * 60 * 60);
        return expiration;
    }

    private GeneratePresignedUrlRequest preparePresignedUrlRequest(String fileName, Date expiration, String fileType) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(s3Utils.getBucket(), fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        request.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
        request.setContentType(fileType);

        return request;
    }

    private void updateMemberProfile(Member member, String imageUrl) {
        member.changeProfileImgUrl(imageUrl);
        memberRepository.save(member);
    }

    private PresignedResonse buildPresignedResponse(GeneratePresignedUrlRequest urlRequest) {
        return PresignedResonse.builder()
                .ProfileImageUrl(amazonS3Client.generatePresignedUrl(urlRequest))
                .build();
    }
}

